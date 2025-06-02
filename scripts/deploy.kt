#!/usr/bin/env kotlin

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

// Constants
val HOME_DIR = System.getProperty("user.home")
val DEFAULT_SCRATCH_DIR = "$HOME_DIR/scratch"
val DEFAULT_DOWNLOADS_DIR = "$HOME_DIR/Downloads"
val REPO_URL = "git@git.soma.salesforce.com:MarketingCloudSdk/MarketingCloudSdk-Android.git"

// Helper functions
fun promptUser(prompt: String, default: String? = null): String {
    val defaultText = if (default != null) " [$default]" else ""
    print("$prompt$defaultText: ")
    val input = readLine() ?: throw IllegalStateException("No input provided")
    return if (input.isBlank() && default != null) default else input
}

fun createDirectory(path: String) {
    File(path).mkdirs()
}

fun moveFile(source: String, destination: String) {
    File(source).copyTo(File(destination), overwrite = true)
}

fun extractZip(zipFile: String, destination: String) {
    ZipInputStream(File(zipFile).inputStream()).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            val file = File(destination, entry.name)
            if (entry.isDirectory) {
                file.mkdirs()
            } else {
                file.parentFile.mkdirs()
                file.outputStream().use { output ->
                    zip.copyTo(output)
                }
            }
            entry = zip.nextEntry
        }
    }
}

fun updateMavenMetadata(metadataFile: File, version: String) {
    val content = metadataFile.readText()
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    
    // First, ensure the version is in the versions list if it's not already there
    val versionPattern = "<version>$version</version>"
    val updatedContent = if (!content.contains(versionPattern)) {
        content.replace(
            "<versions>",
            "<versions>\n      <version>$version</version>"
        )
    } else {
        content
    }
    
    // Then update the lastUpdated timestamp using regex
    val finalContent = updatedContent.replace(
        Regex("<lastUpdated>\\d{14}</lastUpdated>"),
        "<lastUpdated>$timestamp</lastUpdated>"
    )
    
    metadataFile.writeText(finalContent)
}

fun updateSignatures(file: File) {
    val basePath = file.absolutePath
    val md5 = ProcessBuilder("md5", "-q", basePath).start().inputStream.bufferedReader().readText().trim()
    val sha1 = ProcessBuilder("shasum", "-a", "1", basePath).start().inputStream.bufferedReader().readText().split(" ")[0]
    val sha256 = ProcessBuilder("shasum", "-a", "256", basePath).start().inputStream.bufferedReader().readText().split(" ")[0]
    val sha512 = ProcessBuilder("shasum", "-a", "512", basePath).start().inputStream.bufferedReader().readText().split(" ")[0]
    
    File("$basePath.md5").writeText(md5)
    File("$basePath.sha1").writeText(sha1)
    File("$basePath.sha256").writeText(sha256)
    File("$basePath.sha512").writeText(sha512)
}

fun createIndexHtml(directory: File, files: List<String>) {
    val content = """
        <html>
        <body>
        <h1>Directory listing</h1>
        <hr/>
        <pre>
        <a href="../">../</a>
        ${files.filter { it != "index.html" }.joinToString("\n") { "<a href=\"$it\">$it</a>" }}
        </pre>
        </body>
        </html>
    """.trimIndent()
    
    File(directory, "index.html").writeText(content)
}

fun parseVersion(version: String): Triple<String, String, String> {
    // Expected format: 9.0.1.256000000
    val parts = version.split(".")
    if (parts.size != 4) throw IllegalArgumentException("Invalid version format. Expected format: X.Y.Z.RRRMMSSSS")
    
    val majorMinor = "${parts[0]}.${parts[1]}"
    val fullVersion = "${parts[0]}.${parts[1]}.${parts[2]}"
    val releaseNumber = parts[3].substring(0, 3) // Get first 3 digits of release number
    val maintenanceNumber = parts[3].substring(3, 5).toInt().toString() // Convert to single digit
    
    // Format: X.Y.Z.R.M for directories (ignoring SDK Explorer patch)
    val directoryVersion = "$fullVersion.$releaseNumber.$maintenanceNumber"
    
    return Triple(majorMinor, fullVersion, directoryVersion)
}

fun main() {
    try {
        println("\n=== Starting Deployment Process ===\n")
        
        // Get working directory
        val scratchDir = promptUser("Enter working directory", DEFAULT_SCRATCH_DIR)
        println("Using working directory: $scratchDir")
        
        // Set up directory paths
        val releasesDir = "$scratchDir/_RELEASES"
        val reposDir = "$scratchDir/_REPOS"
        
        // Get source directory
        val sourceDir = promptUser("Enter source directory", DEFAULT_DOWNLOADS_DIR)
        println("Using source directory: $sourceDir")
        
        // Step 1: Get release tag and create release directory
        val releaseTag = promptUser("Enter the release TAG (e.g., 9.0.1.256000000)")
        val (majorMinor, fullVersion, directoryVersion) = parseVersion(releaseTag)
        println("\nParsed version components:")
        println("- Major.Minor: $majorMinor")
        println("- Full Version: $fullVersion")
        println("- Directory Version: $directoryVersion")
        
        val releaseDir = File(releasesDir, directoryVersion)
        createDirectory(releaseDir.absolutePath)
        println("\nCreated release directory: ${releaseDir.absolutePath}")
        
        // Step 2: Move and extract release file
        println("\nLooking for release zip file...")
        val zipFile = File(sourceDir).listFiles { file -> 
            file.name.contains("release-$releaseTag") && file.extension == "zip" 
        }?.firstOrNull() ?: throw IllegalStateException("No zip file found with version $releaseTag")
        println("Found zip file: ${zipFile.name}")
        
        moveFile(zipFile.absolutePath, File(releaseDir, zipFile.name).absolutePath)
        println("Moved zip file to release directory")
        
        println("Extracting zip file...")
        extractZip(File(releaseDir, zipFile.name).absolutePath, releaseDir.absolutePath)
        println("Extraction complete")
        
        // Step 3: Move APKs
        println("\nLooking for APK files...")
        val apks = File(sourceDir).listFiles { file -> 
            file.name.contains("sdkx-") && 
            file.name.contains("-$releaseTag") && 
            file.extension == "apk" 
        }
        if (apks.isNullOrEmpty()) {
            println("Warning: No APK files found")
        } else {
            apks.forEach { apk ->
                moveFile(apk.absolutePath, File(releaseDir, apk.name).absolutePath)
                println("Moved APK: ${apk.name}")
            }
        }
        
        // Step 4: Move mapping.txt
        println("\nLooking for mapping file...")
        val mappingFile = File(sourceDir).listFiles { file -> 
            file.name.contains("mapping") && 
            file.name.contains("-$releaseTag") 
        }?.firstOrNull()
        
        if (mappingFile != null) {
            moveFile(mappingFile.absolutePath, File(releaseDir, mappingFile.name).absolutePath)
            println("Moved mapping file: ${mappingFile.name}")
        } else {
            println("Warning: mapping.txt not found. Please complete this task manually.")
        }
        
        // Step 6: Create scratch directory
        println("\nSetting up repository...")
        val repoDir = File(reposDir, directoryVersion)
        createDirectory(repoDir.absolutePath)
        println("Created repository directory: ${repoDir.absolutePath}")
        
        // Step 7: Clone repository
        println("Cloning repository...")
        ProcessBuilder("git", "clone", REPO_URL, repoDir.absolutePath).start().waitFor()
        println("Repository cloned successfully")
        
        println("Checking out gh-pages branch...")
        ProcessBuilder("git", "checkout", "gh-pages")
            .directory(repoDir)
            .start()
            .waitFor()
        println("Switched to gh-pages branch")
        
        // Step 8: Copy version folder from repo to repository
        println("\nCopying version folder to repository...")
        val sourceVersionDir = File(releaseDir, "repo/com/salesforce/marketingcloud/marketingcloudsdk/$fullVersion")
        val destVersionDir = File(repoDir, "repository/com/salesforce/marketingcloud/marketingcloudsdk/$fullVersion")
        createDirectory(destVersionDir.parentFile.absolutePath)
        println("Copying from: ${sourceVersionDir.absolutePath}")
        println("Copying to: ${destVersionDir.absolutePath}")
        
        fun copyRecursively(src: File, dest: File) {
            if (src.isDirectory) {
                dest.mkdirs()
                src.listFiles()?.forEach { child ->
                    copyRecursively(child, File(dest, child.name))
                }
            } else {
                src.copyTo(dest, overwrite = true)
            }
        }
        copyRecursively(sourceVersionDir, destVersionDir)
        println("Copied version folder successfully")
        
        // Recursively delete sources files from the destination version directory
        println("\nCleaning up sources files...")
        val sourcesFiles = destVersionDir.listFiles { file ->
            file.isFile && (
                file.name.endsWith("-sources.jar") ||
                file.name.endsWith("-sources.jar.md5") ||
                file.name.endsWith("-sources.jar.sha1") ||
                file.name.endsWith("-sources.jar.sha256") ||
                file.name.endsWith("-sources.jar.sha512")
            )
        }?.toList() ?: emptyList()
        
        if (sourcesFiles.isEmpty()) {
            println("No sources files found to delete")
        } else {
            println("Found ${sourcesFiles.size} sources files to delete:")
            sourcesFiles.forEach { file ->
                println("Deleting: ${file.name}")
                if (file.delete()) {
                    println("Successfully deleted: ${file.name}")
                } else {
                    println("Failed to delete: ${file.name}")
                }
            }
        }
        
        // Step 9: Create index.html for version directory
        println("\nCreating index.html files...")
        val files = destVersionDir.listFiles()?.map { it.name } ?: emptyList()
        createIndexHtml(destVersionDir, files)
        println("Created index.html in version directory")
        
        // Step 10: Update parent index.html
        val parentDir = destVersionDir.parentFile
        val parentFiles = parentDir.listFiles()?.map { it.name } ?: emptyList()
        createIndexHtml(parentDir, parentFiles)
        println("Created index.html in parent directory")
        
        // Step 11: Update maven-metadata.xml
        println("\nUpdating maven-metadata.xml...")
        val metadataFile = File(parentDir, "maven-metadata.xml")
        if (metadataFile.exists()) {
            updateMavenMetadata(metadataFile, fullVersion)
            println("Updated maven-metadata.xml with version $fullVersion")
            // Generate checksums for maven-metadata.xml
            updateSignatures(metadataFile)
            println("Generated checksums for maven-metadata.xml")
        } else {
            println("Warning: maven-metadata.xml not found")
        }
        
        // Step 12: Update _config.yml if needed
        println("\nChecking _config.yml...")
        val configFile = File(repoDir, "_config.yml")
        if (configFile.exists()) {
            val configContent = configFile.readText()
            val currentVersion = configContent.lines()
                .find { it.contains("currentVersion:") }
                ?.substringAfter("currentVersion:")
                ?.trim()
                ?.trim('"')
            val currentMajorMinor = configContent.lines()
                .find { it.contains("currentMajorMinor:") }
                ?.substringAfter("currentMajorMinor:")
                ?.trim()
                ?.trim('"')
            
            if (currentVersion != fullVersion || currentMajorMinor != majorMinor) {
                val updatedContent = configContent
                    .replace("currentVersion: \".*\"", "currentVersion: \"$fullVersion\"")
                    .replace("currentMajorMinor: \".*\"", "currentMajorMinor: \"$majorMinor\"")
                configFile.writeText(updatedContent)
                println("Updated _config.yml with new version information")
            } else {
                println("No version update needed in _config.yml")
            }
        } else {
            println("Warning: _config.yml not found")
        }
        
        println("\n=== Deployment completed successfully! ===")
        println("\nSummary:")
        println("- Working directory: $scratchDir")
        println("- Source directory: $sourceDir")
        println("- Release files: ${releaseDir.absolutePath}")
        println("- Repository: ${repoDir.absolutePath}")
        println("- Artifacts: ${destVersionDir.absolutePath}")
        
        // Create release branch and prepare for pull request
        println("\nPreparing for peer review...")
        val branchName = "release/$directoryVersion"
        println("Creating branch: $branchName")
        
        // Create and checkout new branch
        ProcessBuilder("git", "checkout", "-b", branchName)
            .directory(repoDir)
            .start()
            .waitFor()
        println("Created and switched to branch: $branchName")
        
        // Add all changes
        ProcessBuilder("git", "add", ".")
            .directory(repoDir)
            .start()
            .waitFor()
        println("Added all changes to git")
        
        // Commit changes
        val commitMessage = "Release $directoryVersion"
        ProcessBuilder("git", "commit", "-m", commitMessage)
            .directory(repoDir)
            .start()
            .waitFor()
        println("Committed changes with message: $commitMessage")
        
        // Push branch to remote
        ProcessBuilder("git", "push", "-u", "origin", branchName)
            .directory(repoDir)
            .start()
            .waitFor()
        println("Pushed branch to remote repository")
        
        // Create Pull Request using GitHub CLI
        println("\nCreating Pull Request...")
        
        // Create PR using GitHub CLI
        val prCommand = arrayOf(
            "gh", "pr", "create",
            "--base", "gh-pages",
            "--head", branchName,
            "--title", "Release $directoryVersion",
            "--body", """
                This PR includes the following changes:
                
                1. Added version artifacts for $directoryVersion
                2. Updated maven-metadata.xml with version $directoryVersion
                3. Generated checksums for maven-metadata.xml
                4. Updated _config.yml with new version information
                
                Please review the changes and approve if everything looks correct.
            """.trimIndent()
        )
        
        val prProcess = ProcessBuilder(*prCommand)
            .directory(repoDir)
            .redirectErrorStream(true)
            .start()
        
        val prOutput = prProcess.inputStream.bufferedReader().use { it.readText() }
        val prExitCode = prProcess.waitFor()
        
        if (prExitCode == 0) {
            println("Successfully created Pull Request!")
            println("PR URL: $prOutput")
        } else {
            println("\nFailed to create Pull Request. Please create it manually:")
            println("1. Go to the repository on GitHub")
            println("2. Create a new Pull Request from branch: $branchName")
            println("3. Set the target branch to: gh-pages")
            println("4. Add reviewers and submit for peer review")
        }
        
    } catch (e: Exception) {
        println("\nError during deployment: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
} 