---
name: verifying-maven-release
description: Use when reviewing or validating a Maven repository release PR on the gh-pages docs/artifacts branch (Android-Docs-Artifacts-Staging or the sdk-android docs repo) — checking that newly added module versions contain no source files, that every md5/sha1/sha256/sha512 hash matches, that maven-metadata.xml and index.html files are correct, and that POM inter-module dependencies resolve. Triggers: "verify the release", "validate the maven modules", "check the hashes/index files", reviewing a gh-pages artifact PR.
---

# Verifying a Maven Release

## Overview

The gh-pages branch hosts a static Maven repository (`repository/com/salesforce/marketingcloud/<module>/<version>/`). A release PR adds new version directories — each with an `.aar`, a `.pom`, four hash sidecars per artifact (`.md5/.sha1/.sha256/.sha512`), and a per-version `index.html` — plus updates to the module's `maven-metadata.xml` and `index.html`.

**Core principle: a release directory contains published binaries only — never source — and every hash, index link, and dependency must be internally consistent.** Manual spot-checking misses things; run the script, which verifies the whole release deterministically.

## When to Use

- Reviewing a PR that adds module versions to the `repository/` tree on gh-pages
- A request like "verify the release", "validate the new maven modules", "are the hashes/index files correct?"
- Before merging an SDK artifact-publish PR

**Not for:** auditing legacy/historical artifacts already on gh-pages (old `-javadoc.jar`, plugin `.jar`, `.DS_Store`, older index conventions). Those predate the script's expectations and are out of scope for release validation — use `--all` only if you explicitly want a full-repo audit.

## Quick Reference

```bash
# From the repo root (the dir containing 'repository/'), on the PR branch:
.claude/skills/verifying-maven-release/scripts/verify-maven-release.sh repository --base gh-pages
```

| Flag | Meaning |
|---|---|
| `--base <ref>` | Release = version dirs **added** vs `<ref>` (default `gh-pages`). **Use this.** |
| `<path> ...` | Treat explicit version dir(s) as the release, e.g. `…/sfmcsdk/3.1.1` |
| `--all` | Run release-scoped checks over every version dir (full audit; surfaces legacy noise) |
| `--help` | Print full header docs |

Exit `0` = PASS, `1` = FAIL. Read the printed "Release dirs (N)" list first — confirm N and the modules match what the PR claims to add.

## What It Checks

**Repo-wide (always):**
1. **Hashes** — every `.md5/.sha1/.sha256/.sha512` matches a freshly computed hash of its target (`.aar`, `.pom`, `maven-metadata.xml`).
2. **Metadata vs dirs** — each `maven-metadata.xml` `<versions>` list matches the on-disk version directories (no missing, extra, or duplicate).
3. **POM resolution** — every inter-module `com.salesforce.marketingcloud` dependency in every POM resolves to a version directory present in the repo.

**Release-scoped (only the added version dirs):**
4. **No source files** — only `.aar/.pom/.html` + hash sidecars present; the AAR contains a compiled `classes.jar` and no `.java`/`.kt`; no `-sources.jar`/`-javadoc.jar`.
5. **Index files** — the per-version `index.html` links every artifact in its dir, and the module `index.html` links the new version.

## Workflow

1. **Checkout the PR branch** and confirm you're on it (`git rev-parse --abbrev-ref HEAD`). The working tree must hold the new dirs; if it shows the base branch, the new version dirs vanish and checks 1/4/5 report nothing to do.
2. **Run** the Quick Reference command.
3. **Verify the "Release dirs" list** equals the modules/versions the PR description claims.
4. **Read the result.** On FAIL, each line names the file and the problem. On PASS, report the dir count and that all five checks passed.

## Common Mistakes

- **Running on the wrong branch.** If checked out on `gh-pages`/base, `--base gh-pages` finds zero added dirs and the source/index checks silently pass with nothing to inspect. Always confirm the PR branch is checked out. (This actually happened: a mid-session checkout to gh-pages made the new dirs disappear.)
- **Treating legacy noise as a release defect.** `--all` flags old javadoc jars, `version-safety-plugin*.jar`, `.DS_Store`, and pre-existing index conventions. These are not part of the release — scope with `--base` instead.
- **Comparing version counts via a naive grep.** Some modules (e.g. `marketingcloudsdk`) use a bare top-level `<version>` as the release marker in addition to the `<versions>` list, so a flat grep double-counts. Check 3 reads only inside the `<versions>` block — trust it over an ad-hoc grep.
- **Manually re-hashing one file and declaring victory.** There are hundreds of sidecars; check 2 verifies all of them. Run the script.

## Notes

- Portable across macOS (`shasum`/`md5`) and Linux (`sha256sum`/`md5sum`).
- AAR internal inspection needs `unzip`; if absent the script warns and skips only that sub-check.
- Self-contained: the script lives in this skill's `scripts/` dir, so the skill drops into a plugin's `skills/` folder unchanged.
