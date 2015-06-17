import challenge_response
from sklearn import datasets
from sklearn import svm

######trainer.py
#Handles the training of the machine learning models. Offers methods to run #different types of machine learning tests, and returns their results.
class trainer:
	#sets up the data used to fit any given model. expects a response object.
	def __init__(self, response):
		#TODO set up the digits object
		self.digits = datasets.load_digits()


	#support vector classification
	def	classify_svm(self):
		#TODO use gridsearch and crossvalidation to find good parameters
		#create the model		
		clf = svm.SVC(0.001,100)
		#train the model
		clf.fit(self.digits.data[:-1], self.digits.target[:-1])
		#use the model to predict the next thing
		clf.predict(digits.data[-1])
