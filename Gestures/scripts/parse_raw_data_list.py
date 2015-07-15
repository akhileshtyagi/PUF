#! /bin/python2

import binascii
import os
from os.path import join, dirname, basename
from math import sqrt, atan, sin, cos, fabs
from pylab import *
from util import simpleMovingAverage, cumulativeMovingAverage, distBetweenPts, calcNextPt, DataList
import csv

CSV_OUTPUT_DIR = "/home/element/PUF/Gestures/OutputCSVs/tim_2000_1"
GENERATED_OUTPUT_DIR = "/home/element/PUF/Gestures/GeneratedOutput/"

#this file will be able to get data lists retrieved directly from a device instead of though email.
#this is essentially the code for get data lists from util.py with a few modifications
#this file also contains modified code to create the responses
def main():
	print "bullacks"
	NormalizedStrat4()
	
def getRawDataLists(directory="/home/rascheel/git/PUFProject/OutputCSVs/"):
    dataLists = [] #list of pressures using DataList class

    # Walk through all the lower directories
    for root, dirs, files in os.walk(directory):
        # We only care about directories that have files
        if(len(files) != 0):
            files.sort()

            # The directories with files have the .csv files
            # Get the pressure data from them and store in list
            for fileName in files:
                challengeList = []
                pressureList = []
                respList = []
                testerName = ""
                deviceName = ""
                seed = ""
                seed = int(fileName.split(" ")[0])
                with open(join(root, fileName), "rb") as csvfile:
                    respReader = csv.reader(csvfile)
                    responseStarted = False
                    for row in respReader:
                        if(responseStarted):
                            respX = float(row[0])
                            respY = float(row[1])
                            respList.append((respX, respY)) #Append the points as x,y tuples
                            pressureList.append(float(row[2]))
                        elif(row[0] != "ChallengeX" and row[0] != "X"):
                            challX = float(row[0])
                            challY = float(row[1])
                            challengeList.append((challX, challY))
                            testerName = row[2]
                            deviceName = row[3]

                        if(row[0] == "X" and row[1] == "Y" and row[2] == "PRESSURE"):
                            responseStarted = True
                dataLists.append(DataList(fileName, testerName, deviceName, seed, challengeList, respList, pressureList))
    return dataLists


def NormalizedStrat4():
    # Walk through all the lower directories
    for root, dirs, files in os.walk(CSV_OUTPUT_DIR):
        # We only care about directories that have files
        if(len(files) != 0):
            files.sort()

            # The directories with files have the .csv files
            # Get the pressure data from them and store in list
            challengeList = []
            pressureList = []
            respList = []
            testerName = ""
            deviceName = ""
            for fileName in files:
                seed = int(fileName.split(" ")[0])
                #Only seeds above 2000 are the single line challenges
                if( seed >= 2000 ):
                    print fileName
                    with open(join(root, fileName), "rb") as csvfile:
                        respReader = csv.reader(csvfile)
                        responseStarted = False
                        for row in respReader:
                            if(responseStarted):
                                respX = float(row[0])
                                respY = float(row[1])
                                respList.append((respX, respY)) #Append the points as x,y tuples
                                pressureList.append(float(row[2]))
                            elif(row[0] != "ChallengeX" and row[0] != "X"):
                                challX = float(row[0])
                                challY = float(row[1])
                                challengeList.append((challX, challY))
                                testerName = row[2]
                                deviceName = row[3]

                            if(row[0] == "X" and row[1] == "Y" and row[2] == "PRESSURE"):
                                responseStarted = True
                    challengeLength = 0
                    isX = False
                    for i in range(0, len(challengeList)-1):
                        yLength = fabs(challengeList[i][1] - challengeList[i+1][1])
                        xLength = fabs(challengeList[i][0] - challengeList[i+1][0])
                        if(xLength > yLength):
                            challengeLength = xLength
                            isX = True
                        else:
                            challengeLength = yLength
                            isX = False
                    normalizedDist = challengeLength/32.0

                    normalizedList = []
                    currPt = challengeList[0]
                    for i in range(0, 32):
                        normalizedList.append(currPt)
                        if(isX):
                            currPt = (currPt[0]+normalizedDist, currPt[1])
                        else:
                            currPt = (currPt[0], currPt[1]+normalizedDist)

                    normalizedPressList = []
                    print len(respList)
                    print len(pressureList)
                    for normPt in normalizedList:
                        if(isX):
                            closestLeftPt = respList[0]
                            closestRightPt = respList[-1]
                            closestLeftPtInd = 0
                            closestRightPtInd = -1
                            if(respList[0][0] > normPt[0]):
                                closestLeftPt = respList[-1]
                                closestRightPt = respList[0]
                                closestLeftPtInd = -1
                                closestRightPtInd = 0

                            for i in range(0, len(respList)):
                                if(respList[i][0] <= normPt[0]): #if it's to the left
                                    if((normPt[0] - respList[i][0]) < (normPt[0] - closestLeftPt[0])):
                                        closestLeftPt = respList[i]
                                        closestLeftPtInd = i
                                if(respList[i][0] > normPt[0]): #if it's to the left
                                    if((respList[i][0]-normPt[0]) < (closestRightPt[0]-normPt[0])):
                                        closestRightPt = respList[i]
                                        closestRightPtInd = i

                            if(closestRightPt == closestLeftPt):
                                closestLeftPtInd = closestLeftPtInd - 1
                                closestLeftPt = respList[closestLeftPtInd]

                            deltaX = closestRightPt[0]-closestLeftPt[0]
                            deltaY = closestRightPt[1]-closestLeftPt[1]
                            angle = fabs(atan(deltaY/deltaX))

                            deltaX1 = normPt[0]-closestLeftPt[0]
                            h1 = deltaX1/cos(angle)

                            h = deltaX/cos(angle)
                            h2 = h-h1

                            interpPress = (h1/h)*pressureList[closestLeftPtInd]+(h2/h)*pressureList[closestRightPtInd]
                            normalizedPressList.append(interpPress)
                        else:
                            closestLowerPt = respList[0]
                            closestUpperPt = respList[-1]
                            closestLowerPtInd = 0
                            closestUpperPtInd = -1
                            if(respList[0][1] > normPt[1]):
                                closestLowerPt = respList[-1]
                                closestUpperPt = respList[0]
                                closestLowerPtInd = -1
                                closestUpperPtInd = 0

                            for i in range(0, len(respList)):
                                if(respList[i][1] <= normPt[1]): #if it's below
                                    if((normPt[1] - respList[i][1]) < (normPt[1] - closestLowerPt[1])):
                                        closestLowerPt = respList[i]
                                        closestLowerPtInd = i
                                if(respList[i][1] > normPt[1]): #if it's above
                                    if((respList[i][1]-normPt[1]) < (closestUpperPt[1]-normPt[1])):
                                        closestUpperPt = respList[i]
                                        closestUpperPtInd = i

                            if(closestUpperPt == closestLowerPt):
                                closestLowerPtInd = closestLowerPtInd - 1
                                closestLowerPt = respList[closestLowerPtInd]

                            deltaX = closestUpperPt[0]-closestLowerPt[0]
                            deltaY = closestUpperPt[1]-closestLowerPt[1]
                            angle = fabs(atan(deltaX/deltaY))

                            deltaY1 = normPt[1]-closestLowerPt[1]
                            h1 = deltaY1/cos(angle)

                            h = deltaY/cos(angle)
                            h2 = h-h1

                            interpPress = (h1/h)*pressureList[closestLowerPtInd]+(h2/h)*pressureList[closestUpperPtInd]
                            normalizedPressList.append(interpPress)
                    print "gg yo"
                    #Calculate moving average n = 5 for use in arbiter
                    movingAvg5 = simpleMovingAverage(normalizedPressList, 40)

                    #Build the bit string and convert it to a bytearray() type for writing 
                    #to a binary file
                    bitString = ""
                    for i in range(0, len(normalizedPressList)):
                        bitString += str(arbiter(normalizedPressList[i], movingAvg5[i]))
                    byteArr = convBitStrToByteArr(bitString)

                    #Copy the directory structure of the OutputCSVs folder
                    deviceName = basename(dirname(root))
                    testerName = basename(root)

                    #If the directory doesn't exist make it
                    att_path = os.path.join(GENERATED_OUTPUT_DIR, "NormalizedStrat4", deviceName, testerName, fileName + ".bin")
                    if not os.path.exists(os.path.dirname(att_path)):
                        os.makedirs(os.path.dirname(att_path))

                    #Write binary file
                    with open(att_path, "wb") as outputFile:
                        outputFile.write(byteArr)
                    
                    pressureList = []
                    respList = []
                    challengeList = []
                    testerName = ""
                    deviceName = ""


def arbiter(val1, val2):
    return 0 if val1 > val2 else 1 #ternary operation

def convBitStrToByteArr(bitString):

    byteToBuild = 0

    #Chop up the bitstring into a list of 8-length strings
    byteList = [bitString[i:i+8] for i in range(0, len(bitString), 8)]

    byteArr = bytearray()
    for byteStr in byteList:
        for i in range(0,8):
            if i < len(byteStr):
                byteToBuild = byteToBuild | (int(byteStr[i]) << (7-i))
        byteArr.append(byteToBuild)
        byteToBuild = 0

    return byteArr

main()
