#! /usr/bin/ruby

$LOAD_PATH << '.'

require "keyboard.rb"

##
# the purpose of this script is to converty x,y
# formatted keyboard data into
# keycode data.
#
# I am writing this script because I feel like the previous version
# my not have been completly correct.
# (it may have had errors sometimes)
##
DATA_XY = "data_xy"
DATA_CONVERTED = "data_converted"

class ConvertXY
	include Keyboard
	
	def initialize(data_xy_folder, data_converted_folder)
		@data_xy_folder = data_xy_folder
		@data_converted_folder = data_converted_folder
		
		# create a keyboard for converting
		@keyboard = Keyboard.new
	end
	
	# convert xy data file type to a file of a character file type
	def convert_file(file_name)
		# for each line of the file,
		# output a line which has x,y exchanged
		# for the converted character
		#
		# create a file to write converted lines to
		output_file = File.open("#{@data_converted_folder}/#{file_name}", 'w')
		
		# iterate over the unconverted data
		File.open("#{@data_xy_folder}/#{file_name}", 'r') do |file_handle|
			file_handle.each_line do |entire_line|
				# split the entire line into segments split by ','
				line = entire_line.split(', ')
			
				# extract the (x,y) values from the line
				x = line[1]
				y = line[2]
				
				# get the corresponding character from the keyboard
				code = @keyboard.translate(x, y)
				
				# replace x, y with code in the line
				write_line = "#{line[0]}, #{code}, #{line[3]}"
				
				# write the line to the output file
				output_file.write(write_line)
			end
		end
	end
end

# ask the class to preform the conversion for each data file
converter = ConvertXY.new(DATA_XY, DATA_CONVERTED)

# read in all files in the directory
Dir.foreach(DATA_XY) do |file|
	# don't do anything for . or ..
	next if file == '.' or file == '..'
	
	# convert these files
	converter.convert_file(file)
end
