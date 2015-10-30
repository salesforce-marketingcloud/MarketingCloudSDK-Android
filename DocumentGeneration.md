#Generating JavaDoc in Android Studio

## Previous steps
In order to generate the JavaDoc, when coding you have to write doc comments for the JavaDoc tool (more info  

[here](http://www.oracle.com/technetwork/articles/java/index-137868.html)).

## Generation

When your comments are ready, follow this steps to automatically generate the documentation:

1. In Android Studio, go to **Tools** -> **Generate JavaDoc…**

2. In **Other command line arguments** enter: 

```-bootclasspath /Users/admin/Library/Android/sdk/platforms/android-22/android.jar```
(substitute this path with the appropriate one).

3. Configure as wished.

4. Choose an output directory and click **Ok**.

The documents will been saved in the output directory.

## Visualization

1. Go to the output folder you defined when generating the docs.

2. Open "index.html" with any browser.
