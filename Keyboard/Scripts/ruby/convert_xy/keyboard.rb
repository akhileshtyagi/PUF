##
# this class represents the
# android keyboard from the project
# it will be able to say, given an (x,y)
# what is the associated character
##
module Keyboard
	QWERTY_XML = "qwerty.xml"
	SYMBOLS_XML = "symbols.xml"
	SYMBOLS_SHIFT_XML = "symbols_shift.xml"

	class Keyboard
		# parse the xml to create a table that relates
		# x,y values to android codes
		def initialize
			parse_file(QWERTY_XML)
			parse_file(SYMBOLS_XML)
			parse_file(SYMBOLS_SHIFT_XML)
			
			# perhaps determining the width and height of the keyboard
			# could allow the information in the xml to be used more
			# effectively
			#TODO
		end
		
		def parse_file(file_name)
			#TODO
		end
	
		# translate an x,y value into a character
		def translate(x, y)
			#TODO
			
			return 97
		end
	end
end

