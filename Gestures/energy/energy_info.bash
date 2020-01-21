#! /bin/bash


# dump energy info from Android device
# visualize it using Battery Historian


# adb shell dumpsys batterystats [package name]
#adb shell dumpsys batterystats --reset
adb shell dumpsys batterystats > batterystats.txt

# bugreport.zip interpreted by battery historian
#adb bugreport > bugreport.zip

