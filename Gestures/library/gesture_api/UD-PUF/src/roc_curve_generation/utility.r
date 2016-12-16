#! /usr/bin/R

#
# the goal of this file is to provide non-specific
# utility functions such as those necessary for:
#   reading data files
#   plotting specifically formatted data
#

#
# given a threshold, compare_data
# compute the FNR -- false negative rate
# false negatives / positives
#
FNR <- function(threshold, compare_data){
    # count the number of false negatives
    number_false_negatives <- sum(
        (compare_data$compare_value < threshold) & (compare_data$positive == 1))

    # count the number of negatives in the data set
    number_positives <- sum(compare_data$positive == 1)

    return(number_false_negatives / number_positives)
}

#
# given a threshold, compare_data
# compute the FPR -- false positive rate
# false positives / negatives
#
FPR <- function(threshold, compare_data){
    # count the number of false positives
    number_false_positives <- sum(
        (compare_data$compare_value >= threshold) & (compare_data$positive == 0))

    # count the number of negatives in the data set
    number_negatives <- sum(compare_data$positive == 0)

    return(number_false_positives / number_negatives)
}

#
# read a single response from
# the format printed out by r
# return the response in a dataframe format
#
read_response <- function(response){
    response_string <- toString(response)

    # this will get the response as a list
    converted_response <- read.csv(text=response_string,
        col.names = c("x","y","pressure"), header=FALSE)

    # convert the list form of a response into a data frame
    #converted_response <- data.frame(converted_response$x,
    #    converted_response$y, converted_response$pressure)

    return(converted_response)
}

#
# read data from the data folders
# assumes the data format and file structure
# returns data in format:
# user, device, challenge, response
#
read_raw_data <- function(){
    # reada in the raw data file generated
    # by CompareValueGenerator.java
    raw_data <- read.csv("raw_data.csv")

    # interpret each of the responses
    raw_data$response <- lapply(raw_data$response, read_response)

    return(raw_data)
}

#
# convert the response from double type into factor type
#
#TODO warning: this returns the underlying integer representation which
#TODO is likely not useful
response_to_factor <- function(response){
    response$x <- as.factor(response$x)
    response$y <- as.factor(response$x)
    response$pressure <- as.factor(response$pressure)

    return(response)
}

#
# test functions
#
#raw_data <- read_raw_data()
#head(raw_data$response)

#raw_data$response[[1]]$pressure
#response_string <- toString(raw_data$response[[1]])
#response_string
#response <- read.csv(text=response_string,
#    col.names = c("x","y","pressure"), header=FALSE)
#response

#head(raw_data)
#sapply(raw_data, mode)
#tail(raw_data)

#warnings()