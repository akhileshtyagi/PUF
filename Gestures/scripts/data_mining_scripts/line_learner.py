import challenge_response
from sklearn import datasets
from sklearn import svm

import numpy as np
import matplotlib.pyplot as plt
import math

#This class does not take into account the challenge. The only thing it cares about is how the user drew on the screen.
#This class will use separate predictors for x,y,pressure. Each predictor will be trained with some number of previous points.
class line_learner:
	def __init__(self, num_prev_points):
		self.prev_points = num_prev_points
		
		#initialize predictors
		self.x_predictor = svm.NuSVC()
		self.y_predictor = svm.NuSVC()
		self.pressure_predictor = svm.NuSVC()


	#TODO takes a list of (x,y) and a pressure list and trains the models. The lists taken as parameters are the response lists
	def train(self, x_y_list, pressure_list):
		#keep track of prev_points number of previous points and use them for featues of the model.
		#put self.prev_points+1 number of points in a list.
		#TODO use up to the list-1 as featues of the model
		#the target is the last item in the list.
		#remove the last item in the list.
		#add a new item to the list from x_y_list, pressure_list
		current_points = []

		#Add the first points to the list
		for x in range(0, self.prev_points+1):
			add_list = []

			add_list.append(x_y_list[x][0])
			add_list.append(x_y_list[x][1])
			add_list.append(pressure_list[x])
			
			current_points.append(add_list)

		print current_points

	#TODO takes a list of (x,y) pairs and returns a list of (x,y,pressure). this (x,y,pressure) list is the models prediction based on past lines the user has drawn.
	def predict(self, x_y_list):
		pass
