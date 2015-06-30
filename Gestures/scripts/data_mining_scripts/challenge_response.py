######challenge_response.py
#This class is simply a wrapper around code that is already written. It pulls functionality from other scrips and puts it all in one place conviently.
#Defined in this file is a class which contains information about the challenge #response pairs. This class knows how to read the data sets, and put them into a #usable format.
import NormalizedStrat4 from responseGen
class response:
	#takes in the file containing information about the challenge response. This method will read in data from this file and populate the appropriate instance variables
	def __init__(self, data_list_object):
		#the data list object		
		self.data_list = data_list_object
		
		#TODO build the response and store it in a variable
		#TODO find the response in the output folders
		self.response = "TODO"
