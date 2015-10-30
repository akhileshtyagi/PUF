#! /bin/bash

adb start-server
adb push $1 /sdcard/download
