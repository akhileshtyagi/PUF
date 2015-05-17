#overview
This file explains, in sparce detail, the function of each file included herein.

##android_source
These are files taken from android source version 5.1.1_r1 which have been modified. The goal is to preform the model comparason in android

###InputMethodService.java
completly deviod of any explaination

##java_files
Contained in this directory is the model building and comparason. This has been refactored from the python code.

###Chain.java
This class represents the marcov model. It knows how to compute probability of states and stuff like that. Also, it can be compared to other marcov models. I didn't implement the comparason by overriding .equals() or implementing the comparable interface because I wanted to return a double indicating the percent difference.

###ChainBuilder.java
This is the class that should be used to build the Marcov model. It provides the method handle_touch() which takes a touch and does all necessary things to add it to the Marcov model. This class also knows how to preform the authentication. Authentication can be started by calling the authenticate() method. This class builds both the user model and the authentication model symotaniously.

###CompareChains.java
preforms the authentication. Computations are done on multiple threads to hopefully increase speed. Everything needed for the computation is computed first.

###Distribution.java
This class represents the distribution for a list of touches. It contains min, max, average, and standard deviation for the entire list.

###Main.java
This class contains a main method which is used to test that model building and model comparason is working correctly. Will allow the user to select between tests for correctness and tests for speed.

###Model_compare.java
This is more for testing than anything else. The actual comparason is preformed in the Chain class' compare_to method. This was simply the inital home for the code.

###Touch.java
Represents a single touch which has a keycode, pressure, and timestamp associated with it. timestamp refers to the uptime of the device in millisecconds. pressure is in some way related to the way the user interacts with the device. supposadly it is the current at the edge of the screen when the user touches the device's screen.

###Utilities.java
Initially most of the python code was moved into this file before being refactored into classes. Whenever a piece of the program was refactored, it was removed from this file. The hope is eventually this file will contain nothing. It is more for test purposes, allowing each of the methods to be run in java and python; compared for differences in output.

###Window.java
Keeps a list of touches. Represents a single window in the Marcov model and provides a hashcode method. Can be compared to other windows.
