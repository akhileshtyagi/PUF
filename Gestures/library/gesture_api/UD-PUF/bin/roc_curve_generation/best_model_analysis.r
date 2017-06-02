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
#
# could apply predicate and create a new data frame
# so that it could be used with confusion matrix
#
#TODO don't I only want to do the predicate when the user_device is the same?
pair_predicate_apply <- function(resamples, predicate){
    #TODO return x$pred x$obs for each class with predicate
    #data_frame <- data.frame(col.names=c("pred, obs"))
    #len = (nrow(resamples)*(nrow(resamples)-1)) / 2
    #result <- logical(length=len)
    #result <- logical(length=nrow(resamples))
    #result <- logical()
    true_count = 0
    total_count = 0

    # for every unique pair
    #j=1
    for(i in 1:(nrow(resamples)-1)){
        for(j in (i+1):nrow(resamples)){
            # determine if the prediction was correct in each i,j
            a <- resamples$pred[[i]] == resamples$obs[[i]]
            b <- resamples$pred[[j]] == resamples$obs[[j]]

            if(predicate == "and"){
                c <- a && b
            }

            if(predicate == "or"){
                c <- a || b
            }

            #result <- append(result, c)
            #result[[i*nrow(resamples) + j]] <- c

            if(c){
                true_count <- true_count + 1
            }

            total_count <- total_count +1
        }
    }

    # compute the accuracy
    #accuracy <- sum(result) / length(result)
    accuracy <- true_count / total_count

    print(accuracy)

    #print(result)
    #print(length(result))
    #print(sum(result))
    #print(result)

    return(accuracy)
}

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

# make control, preform the training
control <- trainControl(method="repeatedcv", number=10, repeats=3,
    #summaryFunction=twoClassSummary,
    #classProbs=T,
    savePredictions = T,
    timingSamps=20)

model <- train(classification~., data=data,
        method=best_algorithm, trControl=control, tuneLength=9)

# detemrine the best parameters
summary(model)

# goal: get confusion matrix (FAR, FRR) for the best model
#
# make predictions using best model
# first, extract resamples with prediction lines
# with best tuning parameters
best_parameter_resamples <- subset(model$pred, cost == model$bestTune$cost)

# display the confusion matrix to get FRR FAR
#confusionMatrix(model)
cmatrix <- confusionMatrix(best_parameter_resamples$pred, best_parameter_resamples$obs,
    #mode="prec_recall")
    mode="sens_spec")
    #mode="everything")
cmatrix

#display the timing
model$timings

#TODO this best_parameter_resamples set can be transformed to give
#TODO the a && b prediction accuracies
time_start <- proc.time()
predicate_resamples <- pair_predicate_apply(best_parameter_resamples, "and")
predicate_resamples <- pair_predicate_apply(best_parameter_resamples, "or")
proc.time() - time_start


#confusionMatrix(predicate_resamples$pred, predicate_resamples$obs,
#    mode="everything")

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
