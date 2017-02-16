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

#best_algorithm <- "svmLinear"
best_algorithm <- "svmLinear2"

# read and format data
raw_data <- read_raw_data("normalized_data.csv")
data <- format_data(raw_data)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# make control, preform the training
control <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T)

model <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneLength=9)

#TODO detemrine the best parameters
summary(model)

# make cool plots
pdf("output/best_plot.pdf")
plot(model)
dev.off()

pdf("output/best_ROC.pdf")
# select a parameter to plot for
selectedIndices <- model$pred$mtry == 20
plot.roc(model$pred$obs[selectedIndices],
         model$pred$M[selectedIndices])
dev.off()
