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
# generate threhold, FNR, FPR for compare_data
#
generate_threshold_data <- function(compare_data, n_thresholds){
    # create a data frame of thresholds 0.00005
    threshold <- seq(0, 1.0, by=(1/n_thresholds))
    rate_data <- data.frame(threshold)

    # for each threshold
    for(i in 1:nrow(rate_data)){
        # compute FNR
        rate_data[i, "FNR"] <- FNR(rate_data$threshold[i], compare_data)

        # compute FPR
        rate_data[i, "FPR"] <- FPR(rate_data$threshold[i], compare_data)
    }

    return(rate_data)
}

###
# UNUSED FUNCTIONS
###

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

split_data <- function(raw_data){
    # for each classification:
    # split the raw data into two sets,
    # 1. training data  (raw_training_data)
    # 2. test data      (raw_test_data)
    #
    # each classification should end up with
    # training_ratio data in the training set
    # and 1-training_ratio data in the test set
    #
    # want list of index_list (this is all i need)
    # 1. determine the indexes of each class in the raw data
    # get a list of unique classes
    raw_data_frame <- data.frame(
            "classification" = paste(raw_data$user, raw_data$device, raw_data$challenge, sep="_"),
            stringsAsFactors=FALSE)

    class_list <- unique(raw_data_frame$classification)
    list_of_index_list <- vector("list", length(class_list))
    for(i in 1:length(class_list)){
        list_of_index_list[[i]] <- raw_data_frame$classification == class_list[[i]]
    }

    # list_of_index_list now contains the
    # one list for the indexes of each class in raw data
    #
    # 2. given the indexes, sort training_ratio into
    #   training set and 1-training_ratio into test set
    #   for each class
    # done by iterating over each set of indicies
    #
    # determine the length of the training list
    training_data_list_size <- 0
    for(i in 1:length(list_of_index_list)){
        training_data_list_size <- training_data_list_size +
            floor((training_ratio*length(list_of_index_list[[i]])))
    }

    training_data_list <- vector("list", training_data_list_size)
    test_data_list <- vector("list", length(unlist(list_of_index_list)) - training_data_list_size)

    stopifnot(length(training_data_list) + length(test_data_list) == length(unlist(list_of_index_list, recursive=F)))

    #TODO make a training_list_of_index_list and test_list_of_index_list
    #
    #TODO what if insead of trying to pull information out of the list of data,
    #TODO I make a list of booleans for training data and test data
    #TODO then I could accomplish getting the data with just one access
    #TODO although, I would still need to combine them together
    #TODO into the different data sets,
    #TODO so what if I used the vector to access at that point?

    training_index <- 1
    test_index <- 1
    for(i in 1:length(list_of_index_list)){
        # get a list of the data which needs to be allocated
        list_of_data <- raw_data[list_of_index_list[[i]], ]

        #print(head(list_of_data)) #TODO
        #print(typeof(list_of_data)) #TODO

        #TODO should it be nrow instead of length?? NO
        stopifnot(length(list_of_data) != length(list_of_index_list[[i]]))

        # allocate the data to either training set or test set
        # training
        training_size <- floor((training_ratio*length(list_of_data)))
        for(j in 1:training_size){
            #print(unname(list_of_data[j, ]))
            training_data_list[[training_index]] <- list_of_data[j, ]
            training_index <- training_index + 1
        }

        # test
        for(j in (training_size+1):(length(list_of_data) - training_size)){
            test_data_list[[test_index]] <- list_of_data[j, ]
            test_index <- test_index + 1
        }
    }

    # create training data and test data from the lists
    #
    # what I wanted was for each index of training_data_list to be one row
    test_data_list_size <- length(unlist(list_of_index_list)) - training_data_list_size

    # training data
    training_init_user_list <- vector("list", training_data_list_size)
    training_init_device_list <- vector("list", training_data_list_size)
    training_init_challenge_list <- vector("list", training_data_list_size)
    training_init_response_list <- vector("list", training_data_list_size)
    for(i in 1:training_data_list_size){
        training_init_user_list[[i]] <- training_data_list[[i]][["user"]]
        training_init_device_list[[i]] <- training_data_list[[i]][["device"]]
        training_init_challenge_list[[i]] <- training_data_list[[i]][["challenge"]]
        training_init_response_list[[i]] <- training_data_list[[i]][["response"]]
    }

    # test data
    test_init_user_list <- vector("list", test_data_list_size)
    test_init_device_list <- vector("list", test_data_list_size)
    test_init_challenge_list <- vector("list", test_data_list_size)
    test_init_response_list <- vector("list", test_data_list_size)
    for(i in 1:test_data_list_size){
        test_init_user_list[[i]] <- test_data_list[[i]][["user"]]
        test_init_device_list[[i]] <- test_data_list[[i]][["device"]]
        test_init_challenge_list[[i]] <- test_data_list[[i]][["challenge"]]
        test_init_response_list[[i]] <- test_data_list[[i]][["response"]]
    }

    training_data <- data.frame(
        "user"=I(training_init_user_list),
        "device"=I(training_init_device_list),
        "challenge"=I(training_init_challenge_list),
        "response"=I(training_init_response_list))

    test_data <- data.frame(
         "user"=I(test_init_user_list),
         "device"=I(test_init_device_list),
         "challenge"=I(test_init_challenge_list),
         "response"=I(test_init_response_list))

    #TODO why does each of these show a null?
    unique(training_data$user)
    unique(training_data$device)
    unique(training_data$challenge)

    data <- format_data(training_data)
    predict_data <- format_data(test_data)

    #TODO something is going wrong... there are 9 classifications instead of 8
    #data

    #stopifnot(F)
}