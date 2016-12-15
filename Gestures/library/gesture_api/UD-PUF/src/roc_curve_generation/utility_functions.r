#! /usr/bin/R

#
# the goal of this file is to provide non-specific
# utility functions such as those necessary for:
#   reading data files
#   plotting specifically formatted data
#

# install libraries
#install(ggplot2)

# load libraries
#library(ggplot2)

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