#
# the goal of this file is to analyze the hamming distance between responses
#

source("utility.r")

###
# FUNCTIONS
###

###
# BEGIN SCRIPT
###

# read the raw data file
raw_data <- read_raw_data("quantized_data.csv")

# format te raw data
# this includes encoding of the response
#data <- format_data(raw_data)

print(head(data))
stopifnot(F)

# remove NA from the data
# before_removal <- nrow(data)
# data <- na.omit(data)
# print(paste("NA rows removed:", before_removal - nrow(data)))

# print(tail(data))
#stopifnot(F)