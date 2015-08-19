import challenge_response
from sklearn import datasets
from sklearn import svm

import numpy as np
import matplotlib.pyplot as plt

######trainer.py
#Handles the training of the machine learning models. Offers methods to run #different types of machine learning tests, and returns their results.
class trainer:
	#sets up the data used to fit any given model. expects a response object.
	def __init__(self, response_set):
		#TODO set up the digits object
		self.digits = datasets.load_digits()
		self.response_set = response_set


	#support vector classification
	def	raw_classify_svm(self):
		xx, yy, zz = np.meshgrid(np.linspace(-3, 3, 500),
                     np.linspace(-3, 3, 500), np.linspace(-3, 3, 500), indexing='ij')

		#these three lines generate random dummy values to train with
		#X is coordinate pairs, 300 on them between -3 and 3
		#Y is true if the coordinate pair is in the 2 or 4 quadrent false otherwise
		np.random.seed(0)
		X = np.random.randn(300, 2)
		Y = np.logical_xor(X[:, 0] > 0, X[:, 1] > 0)
		
		#TODO put my data in the correct format
		#print self.response_set[0].data_list.challengeList
		print self.response_set[0].data_list.respList.__len__()
		print self.response_set[1].data_list.respList.__len__()
		#print self.response_set[0].data_list.pressureList

		#TODO
		X=np.array([])
		Y=np.array([])
		
		l=0

		for response in self.response_set:
			X = np.append(X,[1]) #response.data_list.challengeList) #Use the challenges as training vectors
			Y = np.append(Y,[l])
			l=(l+1)%2

		X = X.reshape(X.size/3,3)
#[response.data_list.respList, response.data_list.pressureList]) #use respList, pressureList for real number in regression

		#print X.shape
		#print Y.shape
		#print X

		# fit the model
		clf = svm.NuSVC()
		clf.fit(X, Y) #clf.fit([[x,y],[x,y],[x,y]...], [true,false,true...])

		#Determine the ability to predict the response given the challenge after being trained with a number of 

		# plot the decision function for each datapoint on the grid
		#Z = clf.decision_function(np.c_[xx.ravel(), yy.ravel(), zz.ravel()])
		#Z = Z.reshape(xx.shape)

		#plt.imshow(Z, interpolation='nearest',
				   #extent=(xx.min(), xx.max(), yy.min(), yy.max()), aspect='auto',
				   #origin='lower', cmap=plt.cm.PuOr_r)
		#contours = plt.contour(xx, yy, Z, levels=[0], linewidths=2,
				               #linetypes='--')
		#plt.scatter(X[:, 0], X[:, 1], s=30, c=Y, cmap=plt.cm.Paired)
		#plt.xticks(())
		#plt.yticks(())
		#plt.axis([-3, 3, -3, 3])
		
		#plot the thing
		#print clf.predict([1,2,3]);

		#plt.show()

		return "UNIMPLEMENTED"

	
	#use logistic regression to predict
	def	raw_classify_logistic_regression(self):
		#TODO
		print self.response_set
		return "UNIMPLEMENTED"


	#use an evolutionary strategy to predict
	def raw_classify_evolution(self):
		#TODO
		return "UNIMPLEMENTED"


	#TODO write a function to determine the accuracy of a given prediction model given N data sets; graph this data? is there a python library for graphing? ;; Train with part of the data ;; check if the prediction is correct with the remaining data
	def raw_prediction_accuracy_svm(self):
		#TODO
		return "UNIMPLEMENTED"

	#TODO write a function that will use (myplotlab probablly) to output a graph. this graph will show how much error in prediction there is for a given machine learning tactic given N inputs. N will be varied to see how it affects the prediction correct percentage
