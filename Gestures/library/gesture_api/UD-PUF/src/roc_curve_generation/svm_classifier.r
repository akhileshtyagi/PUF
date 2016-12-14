#
# This class will attempt to use SMV
# classifier on raw data and generate plots accordingly
#

# libraries
library(ggplot2)
library(e1071)

# import utility functions
source("utility_functions.r")

# read the raw data file
raw_data <- read_raw_data()

# convert the raw data to the form needed for svm
raw_data$response <- lapply(raw_data$response, response_to_factor)

# use svm to classify data
x <- subset(raw_data, select=-response)
y <- data.frame(raw_data$response)

#typeof(as.factor(raw_data$response[[1]]$y))
#typeof(raw_data$response[[1]]$pressure)

#svm_classifier <- svm(x, y, kernel="linear")

#data <- data.frame(
#    x=data.frame(raw_data$user, raw_data$device, raw_data$challenge),
#    y=as.factor(raw_data$response))
#svm_classifier <- svm(y~., data=data, kernel="linear")
#TODO

# print useful thing about smv classifier
#summary(svm_classifier)

# plot the svm classifier
#TODO

# display an ROC curve for the classifier
#TODO