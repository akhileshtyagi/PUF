import sys
import os
import binascii
from util import hammingDistance

##
# The purpose of this script is to provide
# hamming difference functions to
# compute the hamming difference in all
# possible situations
##

#
# input is 2D array with each element
# in the first dimension is an array
# representing a quantized response
#
# output is average hamming distance
# TODO this should be a list of averages, or
# TODO simply an aggregate average
#

# Calculate average hamming distance between runs of the same 
# routes but different devices/users
def average_hamming_d_device_d_user_s_challenge(byteArrList):
    hamming_sum = 0
    n_sum = 0

    print "\n##### Average hamming distance between runs of same routes, different devices/users #####"
    for i in range(0, len(byteArrList)):
        for j in range(i, len(byteArrList)):
            shortLen = len(byteArrList[i]) if len(byteArrList[i]) < len(byteArrList[j]) else len(byteArrList[j])
            avgHamDist = 0
            #byte length of 16 creates 128-bit sequences
            for k in range(0, shortLen, 16):
                if((len(byteArrList[i][k:k+16]) == 16) and (len(byteArrList[j][k:k+16]) == 16)):
                    avgHamDist += hammingDistance(byteArrList[i][k:k+16], byteArrList[j][k:k+16])
            avgHamDist /= (shortLen/16)
            print "%i -> %i = %i" % (i, j, avgHamDist)

            # compute the aggregate average
            hamming_sum += avgHamDist
            n_sum += 1

    return hamming_sum / n_sum


# Calculate average hamming distance between runs of same device/users 
# but different routes
def average_hamming_s_device_s_user_d_challenge(byteArrList):
    hamming_sum = 0
    n_sum = 0

    print "\n##### Average hamming distance between runs of same devices/user, different routes #####"
    count = 0
    for byteArr in byteArrList:
        avgHamDist = 0
        comparisons = 0
        for i in range(0, len(byteArr)/16):
            for j in range(i, len(byteArr)/16):
                if( i != j ):
                    avgHamDist += hammingDistance(byteArr[i:i+16], byteArr[j:j+16])
                    comparisons += 1
        avgHamDist /= comparisons
        print "%i = %i" % (count, avgHamDist)
        count += 1

        hamming_sum += avgHamDist
        n_sum += 1
    print "\n"

    return hamming_sum / n_sum


# Calculate average hamming distance between runs of the same 
# routes, same user, same device
def average_hamming_s_device_s_user_s_challenge(byteArrList):
    hamming_sum = 0
    n_sum = 0

    print "\n##### Average hamming distance between runs of same routes, same devices, same users #####"
    for i in range(0, len(byteArrList)):
        for j in range(i, len(byteArrList)):
            shortLen = len(byteArrList[i]) if len(byteArrList[i]) < len(byteArrList[j]) else len(byteArrList[j])
            #byte length of 16 creates 128-bit sequences
            if( shortLen > 3 ):
                hamDist = hammingDistance(byteArrList[i][0:3], byteArrList[j][0:3])
                print "%i -> %i = %i" % (i, j, hamDist)

                hamming_sum += hamDist
                n_sum += 1

    return hamming_sum / n_sum


# disables printing
def disable_print():
    sys.stdout = open(os.devnull, 'w')
