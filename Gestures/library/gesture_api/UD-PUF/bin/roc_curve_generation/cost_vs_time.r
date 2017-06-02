#
# the purpose of this script is to
# analyze the best model found by "svm_classifier.r"
#
# 3. quantify exactly how good the algorithm is
#
source("utility.r")
source("response_encoding.r")

library("caret")
library("e1071")
library("pROC")

##
# functions
##

##
# script
##

#best_algorithm <- "svmLinear"
best_algorithm <- "svmLinear2"

# read and format data
raw_data <- read_raw_data("normalized_data.csv")
data <- format_data(raw_data)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# make tuning grids
grid_0 <- expand.grid(cost=c(1))
grid_1 <- expand.grid(cost=c(16))

# make control, preform the training
control_0 <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T,
    timingSamps=20)

control_1 <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T,
    timingSamps=20)

model_0 <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneGrid=grid_0)

model_1 <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneGrid=grid_1)

# detemrine the best parameters
summary(model_0)
summary(model_1)

#display the timing
model_0$timings
model_1$timings