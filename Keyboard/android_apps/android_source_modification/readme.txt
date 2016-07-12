The plan:
1. Change the name of the origional source files to $FILE + "_origional.java"
2. Write a file with the origonal name, $FILE, which extends $FILE_origional.java
3. Override the necessary methods, adding log statements to log intents where applicable

Forseeable problems:
1. Which methods do I need to override to get all required information
2. Can Source modifications be done in this way? I wonder.

The Execution:
1. For Activity, ActivityManagerService, the plan works as expected
2. For ActivityManagerNative,
    + the functionality which needs to be changed
    is not in the public class.
    + for this, the best that I can think is
        - override the class in the same file,
        - change the asInterface() method of ActivityManagerNative
            to return an instance of the newly created class


Where are the files in the Andorid Source:
app:
    $ANDROID_SOURCE_DIR/frameworks/base/core/java/android
server:
    $ANDROID_SOURCE_DIR/frameworks/base/services/core/java/com/android