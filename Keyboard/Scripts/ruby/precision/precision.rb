#! /bin/ruby

# data set name as argument
data_set_name = ARGV.first

#
# determine the precision of data set in pressure
#
# open the file
data_set_file = File.open(data_set_name, "r")

# create a list of pressure values
pressure_list = []
data_set_file.each_line do |line|
 pressure = line.split(',')[2].to_f
 pressure_list.push(pressure)
 puts pressure
end

# look through list of pressure values for the minimum distance
min_difference = 1.0
# for each value
pressure_list.each do |value|
 # compute the difference between all other values
 pressure_list.each do |other_value|
  # do not include the value itself
  # if the difference is 0, do no set min_difference
  difference = (value - other_value).abs
  
  if difference != 0
   min_difference = difference
  end
 end
end

puts "min_difference: #{min_difference}"
