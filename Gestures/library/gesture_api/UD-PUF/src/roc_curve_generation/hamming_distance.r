#
# the goal of this file is to analyze the hamming distance between responses
#

source("utility.r")

###
# FUNCTIONS
###
interpret_bits <- function(response){
    response_string <- toString(response)

    # this will get the response as a list
    converted_response <- read.csv(text=response_string, header=FALSE)

    return(converted_response)
}

# determine the difference between resp1 and resp2
hamming_distance <- function(resp1, resp2){
    sum = 0
    for(i in length(resp1)){
        if(resp1[[i]] != resp2[[i]]){
            sum = sum + 1;
        }
    }

    print(length(resp1))

    return(sum)
}

###
# BEGIN SCRIPT
###

# read the raw data file
raw_data <- read.csv("quantized_data.csv")

# interpret each of the responses
raw_data$response <- lapply(raw_data$response, interpret_bits)

# raw_data$response are all 128-bit responses
#TODO determine average hamming distance
#TODO determine average difference in hamming distance

print(head(raw_data$response))
print(hamming_distance(raw_data$response[[1]], raw_data$response[[1]]))
#print(head(data))
stopifnot(F)

# remove NA from the data
# before_removal <- nrow(data)
# data <- na.omit(data)
# print(paste("NA rows removed:", before_removal - nrow(data)))

# print(tail(data))
#stopifnot(F)