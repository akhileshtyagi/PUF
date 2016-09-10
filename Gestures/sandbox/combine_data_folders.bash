#!/bin/bash
DATA_FILE="data_sets_combined"

# create a folder for each shape type
# move corresponding data from each data set to this folder

# create a folder for the combined data sets
rm -r $DATA_FILE
mkdir $DATA_FILE

# for all file types, create a folder
for set_filename in data_sets/*; do 
  # for each file type
  for data_filename in $set_filename/json/*; do 
    DATA_FOLDER=`basename $data_filename`
    mkdir $DATA_FILE/$DATA_FOLDER
  done
  
  break 1;
done

# for each data,
# put it into a folder using it's basename
count=0
for set_filename in data_sets/*/json; do 
  for data_filename in $set_filename/*; do
    BASE_NAME=`basename $data_filename`
    #echo "cp $data_filename $DATA_FILE/`echo $BASE_NAME`_$count"
    
    cp $data_filename $DATA_FILE/`echo $BASE_NAME`/`echo $BASE_NAME`_$count
  done
  
  ((count=count+1))
done
