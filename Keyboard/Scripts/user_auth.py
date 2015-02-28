__author__ = "Ian Richardson - iantrich@gmail.com"
__author__ = 'Tim Dee - timdee@iastate.edu'
# Will take in a random set of data try to authenticate the user for a set of token/window/threshold values

import os
import csv
import platform
import myutilities

# TODO List user profiles available
# TODO List available sets of raw data

#@param: model is the lookuptable for a token/window/time_threshold value
#@param: raw_data_path is the path to the raw data to authenticate against the model
#
#@return: True if the user is authenticated
#@return: False if the user is not authenticated
def authenticate_model(model, raw_data_path):
    #above this probability we authenticate the user. Below it, we do not.
    PROBABILITY_THRESHOLD=.5 
    
    base_table = model.get('table')
    base_distribution = model.get('distribution')
    base_window = model.get('window')
    base_token = model.get('token')
    base_threshold = model.get('threshold')

    #find the probability that this raw_data matches the model
    user_probability=myutilities.build_lookup(raw_data_path,
                                               base_table,
                                               base_distribution,
                                               base_window,
                                               base_threshold,
                                               base_token,
                                               True)

    #The above code will only quantize one file, the one requested
    if (user_probability>=PROBABILITY_THRESHOLD):
        auth=True
        #print("user authenticates with likelihood of: "+str(user_probability)+"\n")
    else:
        auth=False
        #print("user fails authentication with likelihood of: "+str(user_prability)+"\n")

    return auth



#no longer used!
def authenticate():
    # Directory to data
    path = os.path.dirname(os.path.realpath(__file__))
    path += '/Data/'

    # Specify user to profile
    user = None
    device = None
    good_dir = False

    #token/window/threshold values to authenticate against
    token = None
    window = None
    threshold = None

    #get the profile to authenticate against
    while not good_dir:
        user = raw_input("Specify the user to profile as spelled in the /Scripts/Data/Tables/ folder: ")
        device = raw_input("Specify the device name to profile as spelled in the /Scripts/Data/Tables/" + user + "/ folder: ")
        data_path = path + 'Tables/' + user + '/' + device + '/'

        if not os.path.exists(data_path):
            good_dir = False
            print("ERROR: The specified user/device combination doesn't exist in /Scripts/Data/Tables/\nPlease try again\n")
        else:
            good_dir = True

    user_path = path + 'User Profiles/' + user + '/' + device + '/'

    #get the token/window/threshold values
    valid_windows=['5','6','7','8','9','10','15','20','25']
    valid_tokens=['10','20','30','40','50','60','70','80','90','100']
    #TODO also get the number of tokens automatically like this
    #TODO fix this to get all window sizes automatically from directory names
    #for subdir,dir,file in os.walk(user_path):
    #    valid_windows.append(subdir)

    good_value=False
    while not good_value:
        token=raw_input("enter token value from "+str(valid_tokens))
        if token in valid_tokens:
            good_value=True
        else:
            print("not a valid token")

    good_value=False
    while not good_value:
        window=raw_input("enter window value from "+str(valid_windows))
        if window in valid_windows:
            good_value=True
        else:
            print("that value is out of range\n")

    good_value=False
    while not good_value:
        threshold=float(raw_input("enter threshold value 0<x<1: "))
        if threshold > 0 and threshold <= 1:
            good_value=True
        else:
            print("that value is out of range\n")

    #get the raw data to try to authenticate against user profile,token,window,threshold
    data_set = None
    good_file = False

    while not good_file:
        data_set = raw_input("Specify the file name of the user dataset to match against in /Scripts/Raw Data/Datasets/: ")

        data_set_desc = data_set.split("_")
        # Grab date from csv file
        data_set_date = data_set_desc[0]
        # Grab user from csv file
        data_set_user = data_set_desc[1]
        # Grab device from csv file
        data_set_device = data_set_desc[2]
        # Grab device orientation from csv file
        data_set_orientation = os.path.splitext(data_set_desc[3])[0]

        data_set = path + 'Raw Data/Datasets/' + data_set
        try:
            f = open(data_set)
            f.close()
        except IOError:
            good_file = False
            print("ERROR: The specified file name does not exist in /Scripts/Raw Data/Datasets/\nPlease try again\n")
        else:
            good_file = True


    data_path = path + 'Tables/' + user + '/' + device + '/'

    if not os.path.exists(user_path):
        os.makedirs(user_path)

    allProbabilities = []

    for subdir, dirs, files in os.walk(data_path):
        for f in files:
            file_desc = f.split("_")
            # Grab date from csv file
            file_date = file_desc[0]
            # Grab user from csv file
            file_user = file_desc[1]
            # Grab device from csv file
            file_device = file_desc[2]
            # Grab device orientation from csv file
            file_orientation = os.path.splitext(file_desc[3])[0]
            if platform.system()=='Windows':
                # Grab token size from csv file
                file_token_size = os.path.splitext(file_desc[4])[0].split(" ")[1]
                # Grab window size from csv file
                file_window_size = os.path.splitext(file_desc[5])[0].split(" ")[1]
            else:
                file_token_size = os.path.splitext(file_desc[4])[1]
                file_window_size = os.path.splitext(file_desc[5])[1]

            #compairing window and token sizes of the file to those entered
            print("checking: "+f+"\n")
            # we only want that calculations are preformed for window, tokens specified
            if int(window)==int(file_window_size) and int(token)==int(file_token_size):
                # Rebuild cluster ranges into 2D array and lookup table into a dictionary
                cluster = 0
                lookup = 0
                clusterRanges = []
                lookupTable = {}
                #TODO file path needs to be date + / + f
                with open(data_path+file_date+'/'+file_window_size+'/'+f, 'rt') as csvfile:
                    reader = csv.reader(csvfile)
                    for row in reader:
                        if row is "Cluster Ranges":
                            cluster = 1
                        elif row is "Lookup Table":
                            cluster = 0
                            lookup = 1
                        elif cluster:
                            clusterRanges.append([float(row[0]), float(row[1])])
                        elif lookup:
                            # Check if window sequence already in lookup table
                            if float(row[0]) in lookupTable:
                                # Check if the next touch is in the key's dict
                                keyDict = lookupTable.get(float(row[0]))
                                keyDict[float(row[1])] = float(row[2])
                                # Add the value probability back into the table
                                lookupTable[float(row[0])] = keyDict
                            else:
                                lookupTable[float(row[0])] = {float(row[1]): float(row[2])}

                # Open data set and go through it line by line
                with open(data_set, 'rt') as csvfile2:
                    reader2 = csv.reader(csvfile2)
                    clusterQuantized = []
                    i = 0
                    # Quantize the clusters
                    print "Quantizing cluster ranges for token: " + str(file_token_size) + " and window: " + str(file_window_size)
                    while i < clusterRanges.__len__():
                        clusterQuantized.append((clusterRanges[i][1] + clusterRanges[i][0]) / 2)
                        i += 1
                    quantizedValues = []
                    prob = 1
                    for row in reader2:
                        # Place touch pressure based on clustering
                        i = 0
                        while i < clusterRanges.__len__():
                            item = float(row[3])
                            if item >= clusterRanges[i][0] and item < clusterRanges[i][1]:
                                quantizedValues.append(clusterQuantized[i])
                                break
                            i += 1
                        # Grab a window from data set and the following touch and match to the lookup table
                        if len(quantizedValues) is int(file_window_size)+1:
                            value = quantizedValues[len(quantizedValues)-1]

                            key = 0.0
                            # Calculate key value using window touches
                            for idx, val in enumerate(quantizedValues):
                                if idx <= file_window_size:
                                    key += (val * 1 * (int(file_window_size)-idx))

                            # Setting default probability
                            defProb = 0.01

                            # Match value with sequence key in lookup table
                            # Check if the key is already in the table dict
                            if key in lookupTable:
                                # Check if the next touch is in the key's dict
                                keyDict = lookupTable.get(key)
                                if value in keyDict:
                                    # Multiply probability by our ongoing P(L, m)
                                    prob *= keyDict.get(value)
                                # Next touch not found, use default
                                else:
                                    prob *= defProb

                            # Sequence not found, use default
                            else:
                                prob *= defProb
                            # Shift out oldest touch
                            quantizedValues.pop(0)
                    allProbabilities.append([prob, file_window_size, file_token_size])

    # determine if probability is sufficient to authenticate user
    if not allProbabilities:
        print("no data exists for the given token and window size\n")
    else:
        #The above code will only quantize one file, the one requested
        if allProbabilities[0]>=threshold:
            print("user authenticates with likelihood of: "+str(allProbabilities[0])+"\n")
        else:
            print("user fails authentication with likelihood of: "+str(allProbabilities[0])+"\n")