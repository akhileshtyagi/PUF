##
# this class represents the
# android keyboard from the project
# it will be able to say, given an (x,y)
# what is the associated character
##
module Keyboard
	# adjust this value to change the number of keys which map to a given region
	KEYS_PER_REGION = 1.0 # default 1.0
	REGION_WIDTH = 10.0 # default 10.0
	REGION_HEIGHT = 50.0 # default 50.0
	
	# constant file names
	QWERTY_XML = "qwerty.xml"
	SYMBOLS_XML = "symbols.xml"
	SYMBOLS_SHIFT_XML = "symbols_shift.xml"
	
	##
	# different devices have different numbers of pixels
	##
	class Device
		def initialize
			@device_width = 0
			@device_height = 0
			@keyboard_width = 0
			@keyboard_height = 0
			
			# 1dip = 1pixel at 160 dpi
			@dpi = 160
		end
		
		def keyboard_width
			@keyboard_width
		end
		
		def keyboard_height
			@keyboard_height
		end
		
		# determine keyboard height and width from a data file
		def infer_dimensions(file_name)
			max_x = 0;
			max_y = 0;
			File.open(file_name, 'r') do |file_handle|
				file_handle.each_line do |entire_line|
					# split the entire line into segments split by ','
					line = entire_line.split(',')
					
					# extract the (x,y) values from the line
					x = line[1].to_i
					y = line[2].to_i
					
					max_x = (x > max_x)?(x):(max_x)
					max_y = (y > max_y)?(y):(max_y)
				end
			end
		
			# round all of these values up to the nearest multiple of 80
			max_x = (max_x.to_f / 80).ceil*80
			max_y = (max_y.to_f / 80).ceil*80
			
			print "max_x: #{max_x}, max_y: #{max_y}\n"
			
			# maximum x value
			@device_width = max_x
			
			# TODO
			@device_height = 0;
			
			# same as device width
			@keyboard_width = @device_width
			
			# maximum y value
			@keyboard_height = max_y
		end
	end
	
	##
	# nexus 2013
	##
	class Nexus2013 < Device
		def initialize
			@device_width = 1200
			@device_height = 1920
			
			@dpi = 323
		end
	end
	
	##
	# nexus 2012
	##
	class Nexus2012 < Device
		def initialize
			@device_width = 800
			@device_height = 1280
			
			@dpi = 216
		end
	end

	##
	# represents a key in the keyboard
	##
	class Key
		# key_x, key_y describe where the key is on the keyboard
		# key 0,0 is the top left key
		def initialize(key_code, key_label, key_xb, key_yb, width, height)
			@key_code = key_code
			@key_label = key_label
			@key_x = key_xb
			@key_y = key_yb
			@width = width
			@height = height
		end
		
		##
		# returns true if this key contains the x,y value
		# given the parameters of the device (dip, width ect)
		#
		# interpret width as a percent of the width
		# interpret height in dip
		#
		# use the key_x, key_y values to determine what
		# x,y of the key are in pixels
		##
		def contains(x, y, device)
			# get the x,y,width,height values of the key in pixels
			pixel_width = device.keyboard_width * @width / 100.0
			
			# the above pixel_width is in theory correct, but doesn't work
			#actual_keyboard_width = device.keyboard_width
			#pixel_width = actual_keyboard_width / 10.0
			
			pixel_height = device.keyboard_height / 4
			
			# this should work in theory
			pixel_x = @key_x * device.keyboard_width / 10.0
			#pixel_x = @key_x * actual_keyboard_width / 10.0
			
			# lower left corner of the keyboard is 0,0 in coordinate plane
			# so, key 0,0 in the top left is keyboard_height - key_height
			pixel_y = device.keyboard_height - (@key_y * pixel_height)
			
			# determine if x,y are within the pixel bounds for this key
			# the problem is, it is returning true each time on the first one
			confined_x = ((x >= pixel_x) and (x <= (pixel_x + pixel_width)))
			
			# checking as if pixel_y is at the top left
			confined_y = ((y <= pixel_y) and (y >= (pixel_y - pixel_height)))
			
			
			# make sure the contains parameters are correct
			#if (confined_x and confined_y)
				#print "x: #{x}, y: #{y}\n"
				#print "pixel_x: #{pixel_x}, pixel_y: #{pixel_y}, pixel_width: #{pixel_width}, pixel_height: #{pixel_height}\n"
				#print "is_contained?: #{(confined_x and confined_y)}, code: #{@key_code}\n"
			#end
			
			return (confined_x and confined_y)
		end
		
		# return the key_code
		def key_code
			@key_code
		end
		
	end

	##
	# defines a keyboard which 
	# has a device and uses
	# the device parameters to translate
	# characters
	##
	class Keyboard
		# parse the xml to create a table that relates
		# x,y values to android codes
		def initialize(file_name)
			parse_file(QWERTY_XML)
			parse_file(SYMBOLS_XML)
			parse_file(SYMBOLS_SHIFT_XML)
			
			# this represents the device used
			@device = Device.new
			@device.infer_dimensions(file_name)
			
			#TODO account for spacing between the keys
			# define consistent parameters
			key_width = REGION_WIDTH * KEYS_PER_REGION;
			key_height = REGION_HEIGHT * KEYS_PER_REGION;
			
			# create a set of keys
			@row_array = []
			
			# first row
			@row_array[0] = []
			@row_array[0] << Key.new(113, "q", 0, 0, key_width, key_height)
			@row_array[0] << Key.new(119, "w", 1, 0, key_width, key_height)
			@row_array[0] << Key.new(101, "e", 2, 0, key_width, key_height)
			@row_array[0] << Key.new(114, "r", 3, 0, key_width, key_height)
			@row_array[0] << Key.new(116, "t", 4, 0, key_width, key_height)
			@row_array[0] << Key.new(121, "y", 5, 0, key_width, key_height)
			@row_array[0] << Key.new(117, "u", 6, 0, key_width, key_height)
			@row_array[0] << Key.new(105, "i", 7, 0, key_width, key_height)
			@row_array[0] << Key.new(111, "o", 8, 0, key_width, key_height)
			@row_array[0] << Key.new(112, "p", 9, 0, key_width, key_height)
			
			# second row
			@row_array[1] = []
			# dummy key to take care of space on keyboard
			#TODO this doesn't actually help becuase keys don't know about one-another
			#row_array[1] << Key.new(-1000, "|", 0, 1, 5, key_height)
			# include the empty space to the left of a as part of a
			@row_array[1] << Key.new(97, "a", 0.0, 1, 15, key_height)
			@row_array[1] << Key.new(115, "s", 1.5, 1, key_width, key_height)
			@row_array[1] << Key.new(100, "d", 2.5, 1, key_width, key_height)
			@row_array[1] << Key.new(102, "f", 3.5, 1, key_width, key_height)
			@row_array[1] << Key.new(103, "g", 4.5, 1, key_width, key_height)
			@row_array[1] << Key.new(104, "h", 5.5, 1, key_width, key_height)
			@row_array[1] << Key.new(106, "j", 6.5, 1, key_width, key_height)
			@row_array[1] << Key.new(107, "k", 7.5, 1, key_width, key_height)
			# include the empty space to the right of l as part of l
			@row_array[1] << Key.new(108, "l", 8.5, 1, 15, key_height)
			
			# third row
			@row_array[2] = []
			# shift is extra large
			@row_array[2] << Key.new(-1, "shift", 0, 2, 15, key_height)
			@row_array[2] << Key.new(122, "z", 1.5, 2, key_width, key_height)
			@row_array[2] << Key.new(120, "x", 2.5, 2, key_width, key_height)
			@row_array[2] << Key.new(99, "c", 3.5, 2, key_width, key_height)
			@row_array[2] << Key.new(118, "v", 4.5, 2, key_width, key_height)
			@row_array[2] << Key.new(98, "b", 5.5, 2, key_width, key_height)
			@row_array[2] << Key.new(110, "n", 6.5, 2, key_width, key_height)
			@row_array[2] << Key.new(109, "m", 7.5, 2, key_width, key_height)
			@row_array[2] << Key.new(-5, "delete", 8.5, 2, 15, key_height)
			
			# forth row
			@row_array[3] = []
			# shift is extra large
			@row_array[3] << Key.new(-3, "done", 0, 3, 20, key_height)
			@row_array[3] << Key.new(-2, "123", 2, 3, 15, key_height)
			@row_array[3] << Key.new(32, "space", 3.5, 3, 30, key_height)
			@row_array[3] << Key.new(46, ". ,", 6.5, 3, 15, key_height)
			@row_array[3] << Key.new(10, "return", 8, 3, 20, key_height)
		end
		
		def parse_file(file_name)
			# decided not to go this route. hard coded instead
		end
		
		# translate an x,y value into a character
		def translate(x, y)
			# loop through all the keys,
			# return of the code of the first one whoe's
			# contains method returns true
			@row_array.each do |row|
				row.each do |key|
					# does this key contain the x,y value on this device?
					if key.contains(x, y, @device)
						return key.key_code
					end
				end
			end
			
			print "(#{x}, #{y}) has no matching key.\n"
			
			# indicates not found condition
			return -1000
		end
	end
end
