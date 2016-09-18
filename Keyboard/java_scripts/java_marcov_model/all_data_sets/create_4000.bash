#! /bin/bash

# this script takes every file in the current file and makes a new file
# this new file contains the last 4512 lines of the file
#TODO repace 4512 with the shortest file length
FILES=./*
for f in $FILES
do
  echo "Processing $f file..."
  
  # take action on each file. $f store current file name
  NEW_FILE="${f}_4512"
  echo $NEW_FILE
  tail -4512 $f > $NEW_FILE
done

rm create_4000.bash_4512
