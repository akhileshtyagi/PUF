#! /bin/python2.7

# combine data files from normalization schemes
# these will be given to NIST statistical randomness tests
# string length is 128 bits

import os
import sys
from os.path import join, dirname, basename

GENERATED_OUTPUT_DIR = "/home/element/PUF/Gestures/ryan_data/OutputGenerated/"
RESULTS_OUTPUT_DIR = "data/"

def bin_to_ascii(binary):
    s=""
    # convert binary string to ascii bit by bit
    # one bit of the binary becomes a 1 or 0 in ascii
    # input is a binary string
    # each character is treated as 8 bit
    #for c in binary:
    #    character=int(c,2)
    #    for i in range(8):
    #        mask=2**i
    #        s+= (character & mask)

    return s.join(format(x,'b') for x in bytearray(binary))

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

    #TEST
    #print strategy_map
    print ord(strategy_map['Strat1'][0][1])
    
    #TODO the data may NOT be getting output properly

    # output as binary
    for k in strategy_map:
        output_bin=os.path.join(outputFile,k+'.bin')
        output_ascii=os.path.join(outputFile,k+'.txt')
        # output as binary
        with open(output_bin,'wb') as file:
            for v in strategy_map[k]:
                # write a combined output file for each strategy
                for response in v:
                    file.write(response)
        # also output as ASCII
        with open(output_ascii,'w') as file:
            for v in strategy_map[k]:
                # write a combined output file for each strategy
                for response in v:
                    file.write(bin_to_ascii(response))

    #TEST test that output is same as input
    with open('data/Strat1.bin','rb') as file:
        print ord(file.read()[1])



#if __name__ == '__main__':
main()
