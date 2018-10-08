#! /bin/python2.7

# combine data files from normalization schemes
# these will be given to NIST statistical randomness tests
# string length is 128 bits

import os
import sys
from os.path import join, dirname, basename

GENERATED_OUTPUT_DIR = "../ryan_data/ryan_data/OutputGenerated/"
RESULTS_OUTPUT_DIR = "data/"

def main():
    strategy_map={}

    #If the output directory doesn't exist make it
    outputFile = os.path.join(RESULTS_OUTPUT_DIR)
    if not os.path.exists(outputFile):
        os.makedirs(os.path.dirname(outputFile))

    # for all files in the input directories
    for root, dirs, files in os.walk(GENERATED_OUTPUT_DIR):
        if(len(files) != 0):
            #Copy the directory structure of the OutputCSVs folder
            stratName = basename(dirname(dirname(root))) 
            deviceName = basename(dirname(root))
            testerName = basename(root)

            inputBinaryFile = join(root, files[0])

            print "Parsing: %s, device: %s, tester: %s" % (stratName, deviceName, testerName)
            # create list if key doesn't yet exist
            if not stratName in strategy_map:
                strategy_map[stratName]=[]
            # append the file contents to the list
            with open(inputBinaryFile,'rb') as file:
                strategy_map[stratName].append(file.read())
    
    for k,v in strategy_map:
        output=os.path.join(outputFile,k,'.bin')
        with open(output,'wb') as file:
            # write a combined output file for each strategy
            for response in v:
                file.write(response)
