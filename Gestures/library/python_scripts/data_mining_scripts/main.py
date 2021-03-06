#! /bin/python2

import test_cases
import trainer
import challenge_response
from scripts import parse_raw_data_list

######main.py
#Provides the ability to:
#1) run all machine learning tests
#2) run each of the machine learning tests individually

#for tests 1,2 I can use scikit-learn
#for evolutionary strategies I will have to use something else. Probabally PyBrains
def main():
	prompt = "Please select an option:\n"
	prompt = prompt + "0) run all tests\n"
	prompt = prompt + "1) support vector machine, classify, raw data\n"
	prompt = prompt + "2) Logistic Regression\n"
	prompt = prompt + "3) evolutionary strategy\n"
	prompt = prompt + "4) line learner\n"
	prompt = prompt + "51) support vector machine, prediction accuracy, raw data\n"
	prompt = prompt + "40) test cases\n"
	prompt = prompt + "42) exit\n"

	#populate data_sets with files for a given (user,device,clallenge,response)
	#this particular directory contains a few thousand of the same swipe
	data_sets = parse_raw_data_list.getRawDataLists("/home/element/PUF/Gestures/OutputCSVs/tim_2000_1")
	#print data_sets

	#TODO create response set for a given (user, device, challenge). 
	response_set = []
	for data_set in data_sets:
		response = challenge_response.response(data_set)
		response_set.append(response)

	worker = trainer.trainer(response_set)
	selection = -1

	while selection != 42:
		selection = input(prompt)
		
		if selection == 0:
			#TODO run all tests
			print worker.raw_classify_svm()
			print worker.raw_classify_logistic_regression()
			print worker.raw_classify_evolution()

		if selection == 1:
			print worker.raw_classify_svm()

		if selection == 2:
			print worker.raw_classify_logistic_regression()

		if selection == 3:
			print worker.raw_classify_evolution()

		if selection == 4:
			print worker.line_learner_svm()
		
		if selection == 40:
			#call the test code main class. This is for testing functionality
			print test_cases.begin_test_suite(response_set)

		if selection == 51:
			print worker.raw_prediction_accuracy_svm()

#finally, call the main method
main()
