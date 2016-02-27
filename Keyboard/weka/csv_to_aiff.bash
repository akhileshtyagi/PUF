#! /bin/bash

# The purpose of this script is to rename csv files to aiff files
# usage: ./csv_to_aiff.bash "filename"
for i in $*; do 
 mv "$i" "$i.arff"
 echo "moving $i to $i.arff" 
done
