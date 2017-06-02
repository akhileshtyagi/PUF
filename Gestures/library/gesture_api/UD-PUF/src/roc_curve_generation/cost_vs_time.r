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

best_algorithm <- "svmLinear"
#best_algorithm <- "svmLinear2"

# read and format data
raw_data <- read_raw_data("normalized_data.csv")
data <- format_data(raw_data)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# make tuning grids
#grid_0 <- expand.grid(cost=c(1))
#grid_1 <- expand.grid(cost=c(16))
grid_0 <- expand.grid(C=c(1))
grid_1 <- expand.grid(C=c(16))

# make control, preform the training
control_0 <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T,
    timingSamps=100)

control_1 <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T,
    timingSamps=100)

model_0 <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneGrid=grid_0)

model_1 <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneGrid=grid_1)

# confusion matrix for both models
cmatrix_0 <- confusionMatrix(model_0$pred$pred, model_0$pred$obs,mode="sens_spec")
cmatrix_0

cmatrix_1 <- confusionMatrix(model_1$pred$pred, model_1$pred$obs,mode="sens_spec")
cmatrix_1

# detemrine the best parameters
print(summary(model_0))
print(summary(model_1))

#display the timing
print(model_0$times)
print(model_1$times)