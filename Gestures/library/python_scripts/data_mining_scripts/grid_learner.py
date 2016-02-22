import challenge_response
from sklearn import datasets
from sklearn import svm

import numpy as np
import matplotlib.pyplot as plt
import math

class grid_learner:

	def __init__(self):


		for i in range(0,self.num_predictors):
			#######
			#1 2 3#
			#4 5 6#
			#7 8 9#
			#######
			row_num = i/num_predict_per_row

			top_left = i*width + row_num*height
			area_size = {width, height}

			self.screen_areas.append(screen_area(top_left, area_size))


	#TODO takes a list of (x,y) and a pressure list and trains the models.
	def train(self, x_y_list, pressure_list):
		pass


	#TODO takes a list of (x,y) pairs and returns a list of (x,y,pressure). this (x,y,pressure) list is the models prediction based on past lines the user has drawn.
	def predict(self, x_y_list):
		#TODO will need to do something different with trained and untrained predictors
		pass


	# translates a screen point into a predictor number / screen area index. This is an inefficient method of doing this, but this code is geared toward ease of coding rather than performance.
	def point_to_area_index(self, point):
		for x in self.screen_areas:
			if x.contains(point):
				return x

		return None


	# calculates the width of a screen area. Creates square areas.
	def screen_area_width(self, num_screen_areas, screen_size):
		total_pixels = screen_size[0]*screen_size[1]
		pixels_per_screen_area = total_pixels/num_screen_areas
		return math.sqrt(pixels_per_screen_area)

	
	# calculates the hight of a screen area. Creates square areas.
	def screen_area_height(self, num_screen_areas, screen_size):
		return screen_area_width(num_screen_areas, screen_size)



#screen areas are arranged in a grid
#screen areas are squares or rectangles
#screen areas are described in terms of their top left, bottom right, top right, and bottom left coordinate
#screen areas start at (0,0) in the top left. Go to (x_max, y_max) in bottom right
#every screen area has a predictor associated with it
class screen_area:
	def __init__(self, top_left, area_size):
		self.top_left = top_left
		self.area_size = area_size #{x,y}
		
		#initialize predictor
		self.area_predictor = svm.NuSVC()
		self.pressure_predictor = svm.NuSVC()

	
	# determines if this screen area contains the x,y point. Edges of the area are included meaning that if the point falls on the edge, then this mehtod still returns true.
	def contains(self, x, y):
		#simple range check... Remember (0,0) is topleft spot
		if(x > self.top_left) and (x < self.top_left+self.area_size[0]):
			if(y > self.top_left) and (y < self.top_left+self.area_size[1]):
				return True

		return False
