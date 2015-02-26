__author__ = 'Tim Dee - timdee@iastate.edu'
#This script will take in raw data and attempt to compute the effectiveness of each window, token
#size. Effectiveness is measured by the number of desirable outcomes by the total number of
#outcomes.

import myutilities
import build_lookup_table
import user_auth
import os

def print_effectiveness():
 	max_percent=0
	max_window=0
 	max_token=0
 	max_time=0;

	#read in good data and bad data
 	raw_data_dir = os.path.join(myutilities.get_current_dir(),"Data","Raw Data")
 	good_data=os.path.join(raw_data_dir,"tim dee","07924e50","2-2-26_timdee_07924e50.csv")
 	bad_data=os.path.join(raw_data_dir,"Ian Richardson","nexus-02","02-25-14_IanRichardson_015d4a82904c0c07.csv")

 	#for each number of tokens
 	for i in [5, 6, 7, 8, 9, 10, 11, 15, 20, 25]:
  		#for each window size
  		for j in [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]:
  			for k in [0, 500, 600, 700, 800, 900, 1000]:
	   			#determine the effectiveness of our authentication method
	   			percent=calc_effectiveness(j,i,k,good_data,bad_data)
	   			#keep track of the best effectiveness
	   			if (percent > max_percent):
	    				max_percent=percent
	    				max_window=j
	    				max_token=i
	    				max_time=k

	print "best effectiveness:"+max_percent+" window:"+max_window+" tokens:"+max_token+" time:"+max_time

	return


last_path=None
table={}
def calc_effectiveness(window, token, time_threshold, raw_good_data_path, raw_bad_data_path):
	#constant values
	CHUNK_SIZE=50 	#number of lines of raw data to try to authenticate against the model
	NUM_CHUNK=100	#number of chunks to try to authenticate against the model
	global last_path #bad programming!
	global table     #also bad programming!

	good_outcomes=0
	bad_outcomes=0
	temp_file_path=myutilities.get_current_dir()+ '/temp'
	temp_file=0

	###
	#for all sets of good data that can be generated
	#good data generation happens by taking in a raw dataset, generating a model with the first half,
	#then trying to authenticate with the second half
	###
	#determine the numbeer of lines in raw_good_data
	good_file=open(raw_good_data_path,'r')

	for i,line in enumerate(good_file):
		pass

	good_file_lines=i+1
	good_file.close()


	#split good data in half by creating a temp file and copying in the first half of good_data
	temp_file=open(temp_file_path,'w')
	good_file=open(raw_good_data_path,'r')

	for i in range(0,good_file_lines/2):
		temp_file.write(good_file.readline())

	temp_file.close()


	#build a table with this temp file, we don't want to build the table more than once
	if last_path!=raw_good_data_path:
		table=build_lookup_table.build_table(temp_file_path)
		last_path=raw_good_data_path

	###
	#use user_auth to determine whether or not each of these good data passes
	#if user is authenticated, good_outcomes++
	#if user is not authenticated, bad_outcomes++
	###
	#grab the particular lookup table we're interested in
	#TODO get the model from tables for this window,token,time value
	model_twt=5;

	for x in range(0,NUM_CHUNK):
		#put next CHUNK_SIZE lines in temp file from good_data
		temp_file=open(temp_file_path,'w')
		for y in range(0,CHUNK_SIZE):
			#good_file is still open from before
			temp_file.write(good_file.readline())

		if user_auth.authenticate(model_twt,temp_file):
			good_outcomes+=1
		else:
			bad_outcomes+=1

		temp_file.close()


	###
	#try to authenticate with bad data (not from correct user)
	#if user is authenticated, bad_outcomes++
	#if user is not authenticated, good_outcomes++
	###
	bad_file=open(raw_bad_data_path)

	for x in range(0,NUM_CHUNK):
		#put next CHUNK_SIZE lines in temp file from bad_data
		temp_file=open(temp_file_path,'w')
		for y in range(0,CHUNK_SIZE):
			temp_file.write(bad_file.readline())

		if user_auth.authenticate(model_twt,temp_file):
			bad_outcomes+=1
		else:
			good_outcomes+=1

		temp_file.close()


	good_file.close()
	bad_file.close()

	effectiveness=good_outcomes/(good_outcomes+bad_outcomes)
	print "effectiveness:"+effectiveness+" window:"+window+" token:"+token+" time:"+time_threshold+"\n"

	return effectiveness