__author__ = 'Tim Dee - timdee@iastate.edu'
# This script will take in raw data and attempt to compute the effectiveness of each window, token,time_threshold
# size. Effectiveness is measured by the number of desirable outcomes by the total number of
# outcomes.

import myutilities
import build_lookup_table
import user_auth
import os

# prints out a percent effectiveness for each token,window,threshold
# also logs all information to a log file
def print_effectiveness():
    max_percent = 0
    max_window = 0
    max_token = 0
    max_time = 0;

    # read in good data and bad data
    raw_data_dir = os.path.join(myutilities.get_current_dir(), "Data", "Raw Data")
    good_data = os.path.join(raw_data_dir, "tim dee", "07924e50", "2-2-26_timdee_07924e50.csv")
    bad_data = os.path.join(raw_data_dir, "Ian Richardson", "nexus-02", "02-25-14_IanRichardson_015d4a82904c0c07.csv")
    # good_data=os.path.join(raw_data_dir,"Ian Richardson","nexus-02","test_small.csv")

    # log file
    log_file_path = os.path.join(myutilities.get_current_dir(), "log_effectiveness.txt")
    log_file = open(log_file_path, 'w')

    # write the header to the log file
    log_file.write("effectiveness\twindow\ttoken\ttime\n")

    # for each window
    for i in [3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 20, 25]:
        # for each number of tokens
        for j in [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]:
            for k in [500, 600, 700, 800, 900, 1000]:
                # determine the effectiveness of our authentication method
                percent = calc_effectiveness(i, j, k, good_data, bad_data)
                log_file.write(str(percent) + "\t" + str(j) + "\t" + str(i) + "\t" + str(k) + "\n")

                # keep track of the best effectiveness
                if percent > max_percent:
                    max_percent = percent
                    max_window = j
                    max_token = i
                    max_time = k

    print "best effectiveness:" + str(max_percent) + " window:" + str(max_window) + " tokens:" + str(
        max_token) + " time:" + str(max_time) + "\n"
    log_file.write("best effectiveness:" + str(max_percent) + " window:" + str(max_window) + " tokens:" + str(
        max_token) + " time:" + str(max_time) + "\n")

    log_file.close()
    return


# TODO remove
# these are here so that they persist between calls to the function,
# this allows the table to be built once for a given set of data
# last_path=""
# table={}
def calc_effectiveness(window, token, time_threshold, raw_good_data_path, raw_bad_data_path):
    # constant values
    CHUNK_SIZE = 50  # number of lines of raw data to try to authenticate against the model
    NUM_CHUNK = 100  # number of chunks to try to authenticate against the model
    global last_path  # bad programming!
    global table  # also bad programming!

    good_outcomes = 0
    bad_outcomes = 0
    temp_file_path = myutilities.get_current_dir() + '/temp'
    temp_file = 0

    ###
    # for all sets of good data that can be generated
    # good data generation happens by taking in a raw dataset, generating a model with the first half,
    # then trying to authenticate with the second half
    ###
    # determine the numbeer of lines in raw_good_data
    good_file = open(raw_good_data_path, 'r')

    for i, line in enumerate(good_file):
        pass

    good_file_lines = i + 1
    good_file.close()


    # split good data in half by creating a temp file and copying in the first half of good_data
    temp_file = open(temp_file_path, 'w')
    good_file = open(raw_good_data_path, 'r')

    for i in range(0, good_file_lines / 2):
        temp_file.write(good_file.readline())

    temp_file.close()

    # TODO remove
    # build a table with this temp file, we don't want to build the table more than once
    # if last_path!=raw_good_data_path:
    # 	table=build_lookup_table.build_table(temp_file_path)
    # 	last_path=raw_good_data_path

    ###
    # use user_auth to determine whether or not each of these good data passes
    # if user is authenticated, good_outcomes++
    # if user is not authenticated, bad_outcomes++
    ###
    # grab the particular lookup table we're interested in
    # get the model from tables for this window,token,time value
    # loop through them all and pull out the one we want
    # figure out if i'm using this correctly
    # model_twt=None

    # for i, lookup in enumerate(table):
    # 	base_table = lookup.get('table')
    # 	base_distribution = lookup.get('distribution')
    # 	base_window = lookup.get('window')
    # 	base_token = lookup.get('token')
    # 	base_threshold = lookup.get('threshold')

    # print "table values"
    # print base_window
    # print base_token
    # print base_threshold

    # print "parameters"
    # print window
    # print token
    # print str(time_threshold) +'\n'


    # 	if(base_window==window and base_token==token and base_threshold==time_threshold):
    # 		model_twt=lookup
    # 		break

    # there is no table built for this window, token, threshold combination
    # if(model_twt==None):
    # 	return 0
    # TODO remove

    ###
    # try to authenticate with good data (from correct user)
    # if user is authenticated, good_outcomes++
    # if user is not authenticated, bad_outcomes++
    ###
    for x in range(0, NUM_CHUNK):
        # put next CHUNK_SIZE lines in temp file from good_data
        temp_file = open(temp_file_path, 'w')
        for y in range(0, CHUNK_SIZE):
            # good_file is still open from before
            temp_file.write(good_file.readline())

        if user_auth.authenticate_model(raw_good_data_path, temp_file_path, window, token, time_threshold):
            good_outcomes += 1
        else:
            bad_outcomes += 1

        temp_file.close()

    ###
    # try to authenticate with bad data (not from correct user)
    # if user is authenticated, bad_outcomes++
    # if user is not authenticated, good_outcomes++
    ###
    bad_file = open(raw_bad_data_path)

    for x in range(0, NUM_CHUNK):
        # put next CHUNK_SIZE lines in temp file from bad_data
        temp_file = open(temp_file_path, 'w')
        for y in range(0, CHUNK_SIZE):
            temp_file.write(bad_file.readline())

        if user_auth.authenticate_model(raw_good_data_path, temp_file_path, window, token, time_threshold):
            bad_outcomes += 1
        else:
            good_outcomes += 1

        temp_file.close()

    good_file.close()
    bad_file.close()

    effectiveness = (1.0 * good_outcomes) / (1.0 * (good_outcomes + bad_outcomes))
    print "effectiveness:" + str(effectiveness) + " window:" + str(window) + " token:" + str(token) + " time:" + str(
        time_threshold)

    return effectiveness