#
# This class will attempt to use SMV
# classifier on raw data and generate plots accordingly
#

# libraries
library(e1071)

# import utility functions
source("utility.r")
source("response_encoding.r")

# set RNG seed
set.seed(1)

###
# CONSTANTS
###

# number of thresholds used
n_threshold <- 2000 #TODO set 20000
# what percent of data is used for training
# (training / (training + test))
training_ratio <- 0.8

###
# FUNCTIONS
###

#
# takes in raw data and puts it in a format required for svm
#
format_data <- function(raw_data){
    # use svm to classify data
    # combine user, device, challenge together
    #TODO response should pick out actual properties of response from the data
    data <- data.frame(
        "classification" = paste(raw_data$user, raw_data$device, raw_data$challenge, sep="_"),
        "response" = response_encoding(raw_data$response),
        stringsAsFactors=FALSE)

    # encode each of the classifications as an integer,
    # otherwise apparently they will get cast to floats
    # in unpredictable ways
    #
    # first, make a list of all the unique classifications
    # the rows are named the classification value
    classification_map <- data.frame(row.names = unique(data$classification))

    # second, assign an integer value to each unique classification
    classification_map$value <- as.factor(c(1:nrow(classification_map)))

    # third, replace the data classifications with integers
    classification_list <- vector("integer", nrow(data))
    for(i in 1:nrow(data)){
        # row names are classifications, value column holds the integer mapping
        #data$classification[[i]] <- classification_map[data$classification[[i]], "value"]
        classification_list[[i]] <- classification_map[data$classification[[i]], "value"]
    }

    # put the classifications into data
    data <- subset(data, select=-classification)
    data$classification <- classification_list

    return(data)
}

###
# BEGIN SCRIPT
###

# read the raw data file
raw_data <- read_raw_data()

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

#data <- format_data(raw_data)
data <- format_data(training_data)
predict_data <- format_data(test_data)

#TODO something is going wrong... there are 9 classifications instead of 8
#data

#stopifnot(F)

# create an svm model
#TODO creating a tuned model will make this unnecessary
model <- svm(data$classification~., data=data, type="C", kernel="linear")

# tune the model
#TODO
#
# in order to tun the model
# this preforms cross validation
# cross validation validates a model
# assesses how the results of a statistical analysis will
# generalize to an independent data set
#tune.out <- tune(svm, data$classification~., data=data, type="C", kernel="linear",
  #ranges=list(cost=c(0.001, 0.01, 0.1, 1, 5, 10, 100)))
#summary(tune.out)

# tune.out stores the best model obtained
#model <- tune.out$best.model

# use the model to predict on the test data
prediction <- predict(model, predict_data)
table(predict=prediction, truth=predict_data$classification)

#TODO figure out some way of analyzing the accuracy of the svm predictor
#TODO how "good" is it?

# print useful thing about smv classifier
#summary(model)

# plot the svm classifier
pdf("output/svm_classifier.pdf")
plot(model, data)
dev.off()

head(data)
#stopifnot(FALSE)