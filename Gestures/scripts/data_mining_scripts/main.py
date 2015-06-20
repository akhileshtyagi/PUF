#! /bin/python2

import trainer
import challenge_response

######main.py
#Provides the ability to:
#1) run all machine learning tests
#2) run each of the machine learning tests individually
def main():
	prompt = "Please select an option:\n"
	prompt = prompt + "0) run all tests\n"
	prompt = prompt + "1) support vector classification\n"
	prompt = prompt + "42) exit\n"

	#TODO populate data_sets with files for a given (user,device,clallenge,response)
	data_sets = []

	#TODO create responses for all data sets for a given (user, challenge)	
	response_set = []
	for data_set in data_sets:
		response = challenge_response.response(data_set)
		response_set.append(resposne)

	worker = trainer.trainer(response_set)
	selection = -1

	while selection != 42:
		selection = input(prompt)
		
		if selection == 0:
			#TODO run all tests
			print worker.classify_svm()

		if selection == 1:
			print worker.classify_svm()


#finally, call the main method
main()
