#! /bin/python2

import os
import sys

from contextlib import contextmanager
from os.path import join, dirname, basename
from ctypes import *
from test1 import stdout_redirected

LIBRARY_LOCATION = "/usr/lib/libtestu01.so"
TEMP_FILE_NAME = "temp_file"
OUTPUT_FOLDER_NAME = "/home/element/PUF/Gestures/library/gesture_api/UD-PUF/src/roc_curve_generation/output_prg/"
INPUT_FOLDER_NAME = "/home/element/PUF/Gestures/library/gesture_api/UD-PUF/src/roc_curve_generation/input_prg/"

##
# This file provides the java implementation with
# the ability to run testU01 suite
##

#
# given a list of responses
# run TESTU01 on the iresponses
#
# the test battery will be run on each response
# response types are initially unicode strings
#
#TODO is response binary simply all the responses
# for the same (user, device, challenge) concatinated?
# if so, I could only pass in responses for the same u,d,c
# then I could write a file with all responses in response_list
# together
#
# for each (user,device,challenge) tripple
#   concatinate all responses
# response list is all responses for one (user,device,challenge)
#
def TESTU01_output(response_list, file_name):
    with open(os.path.join(INPUT_FOLDER_NAME, file_name), 'wb+') as output_file:
        for response in response_list:
            #print response
        
            # write the response out to a file
            #print byte_array_to_string(create_byte_array(response))
            output_file.write(create_byte_array(response))

    #print byte_array_to_string(read_as_byte_array(TEMP_FILE_NAME))
    #c_double(500))


#
# run TESTU01 on the files in input folder
#
def TESTU01_input():
    libc = CDLL(LIBRARY_LOCATION)
    
    # each file in inputfolder
    for (dir_path, dir_name, file_name_list) in os.walk(INPUT_FOLDER_NAME):
        for part_file_name in file_name_list:
            file_name = os.path.join(INPUT_FOLDER_NAME, part_file_name)
            output_file_name = os.path.join(OUTPUT_FOLDER_NAME, part_file_name)
       
            #print file_name
            #print byte_array_to_string(read_as_byte_array(file_name))
            
            # run battery of tests, output to file
            # get the file size in bytes
            temp_file_size = os.path.getsize(file_name)

            # skip those files which are not large enough
            if temp_file_size < 500:
                print file_name + " skipped, not enough bytes"
                continue

            # run the test battery on that file
            print "testing " + file_name
            print "output to " + output_file_name
            with stdout_redirected(output_file_name):
            #libc.bbattery_RabbitFile(c_char_p(file_name), c_double(temp_file_size))
                libc.bbattery_RabbitFile(c_char_p(file_name), c_double(500))

#
# read file_name as byte_array
#
def read_as_byte_array(file_name):
    byte_array = bytearray()
    with open(file_name, 'rb') as input_file: 
        while True:
            # read one byte
            byte = input_file.read(1)

            # test if EOF
            if not byte: 
                break;

            byte_array.append(byte)

    return byte_array

#
# convert a bytearray() to string
#
def byte_array_to_string(byte_array):
    s = ""
    for byte in byte_array:
        for i in range(0,8):
            # for each bit
            s += "1" if (byte & (1<<(7-i))) else "0"

    return s

#
# given a response,
# create an array of bytes to represent it
#
def create_byte_array(response): 
    byteArr = bytearray()

    byte = 0
    j = 7
    for i in range(0, len(response)):
        byte = byte | (int(response[i]) << j)

        j -= 1
        if j < 0:
            byteArr.append(byte)
            byte = 0
            j=7

    if j != 7:
        byteArr.append(byte)

    return byteArr

#
# read in a test file and
# print it out.
#
# this will tell me what format thedata needs to be in
#
def test_data():
    file_name = "responseBinary"

    #inputBinaryFile = join(root, files[0])

    #print inputBinaryFile
    #print "\n"
    print c_char_p(file_name)

    with open(file_name, 'rb') as input_file:
        while True:
            # read one byte
            byte = input_file.read(1)

            # test if EOF
            if not byte: 
                break;

            # bit value
            string_value = ""
            for i in range(0,8):
                string_value += "1" if int(byte) | (1<<i) == 1 else "0"

            print string_value

#
# if this script is called
#
if __name__ == '__main__':
    print "running TESTU01"
    TESTU01_input()

#
# test calls
#
#test_data()
#TESTU01([])

# overall response file
#byte_array = read_as_byte_array("/home/element/PUFProject/OutputGenerated/Strat4/nexus-09/Jake/responseBinary")

#print byte_array_to_string(byte_array)
#print len(byte_array)

# single response file
#byte_array = read_as_byte_array("/home/element/PUFProject/OutputGenerated/")
#print byte_array_to_string(byte_array)
#print len(byte_array)
