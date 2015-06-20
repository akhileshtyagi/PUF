
######challenge_response.py
#This class is simply a wrapper around code that is already written. It pulls functionality from other scrips and puts it all in one place conviently.
#Defined in this file is a class which contains information about the challenge #response pairs. This class knows how to read the data sets, and put them into a #usable format.
class response:
	#takes in the file containing information about the challenge response. This method will read in data from this file and populate the appropriate instance variables
	def __init__(self, data_list_object):
		self.user = "TODO"
		self.device = "TODO"
		#describes what challenge was presented to the user
		self.challenge_descriptor = 0
		#a list containing lists of [x,y,pressure]		
		self.response = [[]]
		
		#TODO interpret the data file
		
