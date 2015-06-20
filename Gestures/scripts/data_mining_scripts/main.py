#! /bin/python2

import trainer
import challenge_response
from scripts import util

######main.py
#Provides the ability to:
#1) run all machine learning tests
#2) run each of the machine learning tests individually

#for tests 1,2 I can use scikit-learn
#for evolutionary strategies I will have to use something else. Probabally PyBrains
def main():
	prompt = "Please select an option:\n"
	prompt = prompt + "0) run all tests\n"
	prompt = prompt + "1) support vector machine\n"
	prompt = prompt + "2) Logistic Regression\n"
	prompt = prompt + "3) evolutionary strategy\n"
	prompt = prompt + "42) exit\n"

	#populate data_sets with files for a given (user,device,clallenge,response)
	data_sets = util.getRawDataLists()

	#TODO create response set for a given (user, device, challenge).
	#in other words, user, device the 
	#response_set = []
	#for data_set in data_sets:
	#	response = challenge_response.response(data_set)
	#	response_set.append(resposne)

	worker = trainer.trainer(data_sets)
	selection = -1

	while selection != 42:
		selection = input(prompt)
		
		if selection == 0:
			#TODO run all tests
			print worker.classify_svm()
			print worker.classify_logistic_regression()
			print worker.classify_evolution()

		if selection == 1:
			print worker.classify_svm()

		if selection == 2:
			print worker.classify_logistic_regression()

		if selection == 3:
			print worker.classify_evolution()


#finally, call the main method
main()
