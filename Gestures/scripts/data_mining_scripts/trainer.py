import challenge_response
from sklearn import datasets
from sklearn import svm

######trainer.py
#Handles the training of the machine learning models. Offers methods to run #different types of machine learning tests, and returns their results.
class trainer:
	#sets up the data used to fit any given model. expects a response object.
	def __init__(self, response_set):
		#TODO set up the digits object
		self.digits = datasets.load_digits()
		self.response_set = response_set


	#support vector classification
	def	classify_svm(self):
		#TODO use gridsearch and crossvalidation to find good parameters
		#create the model		
		clf = svm.SVC(gamma=0.001, C=100)
		#train the model
		#clf.fit(self.digits.data[:-1], self.digits.target[:-1])
		#use the model to predict the next thing
		#clf.predict(digits.data[-1])
		return "UNIMPLEMENTED"

	
	#use logistic regression to predict
	def	classify_logistic_regression(self):
		#TODO
		print self.response_set
		return "UNIMPLEMENTED"


	#use an evolutionary strategy to predict
	def classify_evolution(self):
		#TODO
		return "UNIMPLEMENTED"


	#TODO write a function to determine the accuracy of a given prediction model given N data sets; graph this data? is there a python library for graphing? ;; Train with part of the data ;; check if the prediction is correct with the remaining data


	#TODO write a function that will use (myplotlab probablly) to output a graph. this graph will show how much error in prediction there is for a given machine learning tactic given N inputs. N will be varied to see how it affects the prediction correct percentage
