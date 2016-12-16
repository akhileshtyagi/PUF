#
# This class will attempt to use SMV
# classifier on raw data and generate plots accordingly
#

# libraries
library(e1071)

# import utility functions
source("utility.r")

# read the raw data file
raw_data <- read_raw_data()

# use svm to classify data
# combine user, devic, challenge together
data <- data.frame(
    "classification" = paste(raw_data$user, raw_data$device, raw_data$challenge, sep="_"),
    "response" = c(1)) #TODO this should pick out actual properties of response

head(data)

model <- svm(data$classification~., data=data, type="C", kernel="linear")

#svm_classifier <- svm(raw_data$response~., data=raw_data, type="C", kernel="linear")
#svm_classifier <- svm(data, classes, type="C", kernel="linear")

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