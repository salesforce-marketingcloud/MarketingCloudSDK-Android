#Generating JavaDoc in Android Studio

Javadoc is a tool for generating API documentation in HTML format from doc comments from source code.  
This document explains how to generate JavaDocs with Android Studio.  
For more info on guidelines for style, tag and image conventions click [here](http://www.oracle.com/technetwork/articles/java/index-137868.html)).

## Generation

When comments are finalized, follow these steps to automatically generate the JavaDoc:

1. Go to **Tools** -> **Generate JavaDoc…**

2. In **Other command line arguments** enter the path to the android.jar as the bootclasspath, e.g: 

    ```-bootclasspath /Users/YourUserName/Library/Android/sdk/platforms/android-22/android.jar```

3. Configure any additional settings specific to the documentation.

4. Documents will be saved in the **Output directory**, choose a folder and click **Ok**.


## Visualization

1. Go to the output directory you defined when generating the docs.

2. Open "index.html" with any browser.
