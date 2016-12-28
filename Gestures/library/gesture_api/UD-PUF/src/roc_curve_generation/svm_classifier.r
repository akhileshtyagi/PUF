#
# This class will attempt to use SMV
# classifier on raw data and generate plots accordingly
#

# configure multicore
#library(doMC)
#registerDoMC(cores=1)

# libraries
#library(e1071)
library(caret)

# import utility functions
source("utility.r")
source("response_encoding.r")

# turn off/on error reporting
#options(error=function()traceback(2))

# set RNG seed
set.seed(1)

###
# RESOURCES
###
# http://machinelearningmastery.com/compare-models-and-select-the-best-using-the-caret-r-package/
# http://machinelearningmastery.com/how-to-estimate-model-accuracy-in-r-using-the-caret-package/
# https://topepo.github.io/caret/adaptive-resampling.html
# ftp://cran.r-project.org/pub/R/web/packages/caret/caret.pdf
# http://machinelearningmastery.com/compare-the-performance-of-machine-learning-algorithms-in-r/
# http://stats.stackexchange.com/questions/82162/kappa-statistic-in-plain-english
# http://machinelearningmastery.com/tuning-machine-learning-models-using-the-caret-r-package/

###
# CONSTANTS
###

# number of thresholds used
#n_threshold <- 2000 #TODO set 20000
# what percent of data is used for training
# (training / (training + test))
#training_ratio <- 0.8

###
# FUNCTIONS
###

#
# takes in raw data and puts it in a format required for svm
#
format_data <- function(raw_data){
    # use svm to classify data
    # combine user, device, challenge together
    data <- data.frame(
        "classification" = paste(raw_data$user, raw_data$device, raw_data$challenge, sep="_"),
        "response" = response_encoding(raw_data),
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

    # change the type of classification to factor
    data$classification <- as.factor(data$classification)

    return(data)
}

###
# BEGIN SCRIPT
###

# read the raw data file
#raw_data <- read_raw_data("raw_data.csv")
raw_data <- read_raw_data("normalized_data.csv")

# stopifnot(
#     min(unlist(lapply(raw_data$response, length))) ==
#     max(unlist(lapply(raw_data$response, length))))

# format te raw data
# this includes encoding of the response
data <- format_data(raw_data)

# print(head(data))
# stopifnot(F)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# print(tail(data))
#stopifnot(F)

#
# tuning with e1071 package
#

# tune the model (using leave-one-out cross validation?)
#
# in order to tun the model
# this preforms cross validation
# cross validation validates a model
# assesses how the results of a statistical analysis will
# generalize to an independent data set
#
#TODO create an tune many different models
#TODO use carret to compare the models to find the best
#
# control for tuning
#TODO change to cross=10
# tune_control <- tune.control(nrepeat = 1, sampling = c("cross"), cross=5)

# create various models and tune them
#model_type_list <- c("svm", "lm", "knn", "rpart", "randomForest", "nnet")

#tune.out <- tune("svm", classification~., data=data, type="C", kernel="linear",
#  ranges=list(cost=c(0.001, 0.01, 0.1, 1, 5, 10, 100)),
#  tunecontrol=tune_control)

#
# tuning with caret package
#
# construct models in a list
# define classifiers to be used
#method_list_extensive <- c("lvq", "svmRadial", "svmLinear", "svmPoly",
#    "svmExpoString", "svmBoundrangeString", "svmSpectrumString",
#    "rotationForest", "rocc", "ranger", "nnet", "nb", "lm", "bag")

#method_list_basic <- c("svmRadial", "rocc", "ranger", "nnet") #"lvq",
    #"lm", "bag", "nb", "ada", "binda", "blackboost", "bstSm") "rotationForest"

method_list_unknown <- c("svmExpoString", "svmBoundrangeString", "svmSpectrumString",
    "svmRadialCost", "svmRadialSigma")

# very slow but good models
method_list_slow <- c("evtree")

# model types which preform well (over 80% accuracy)
# "rFerns",
method_list_good <- c("svmLinear", "svmRadial",
    "svmPoly", "ranger", "nb", "lda")
    #, "wsrf")#,
    #"svmRadialCost", "svmRadialSigma")

# model types which preform poorly (leq 80% accuracy)
method_list_poor <- c("xyf", "rpart", "pam", "nnet", "gamSpline", "bayesglm",
    "svmRadialWeights")

#method_list_basic_1 <- c("svmRadial", "ranger")

method_list <- method_list_good

# make a list for models
model_list <- vector("list", length(method_list))
names(model_list) <- method_list

# prepare training scheme(S)
# 3 repeats of 10 fold crossvalidation
control <- trainControl(method="repeatedcv", number=10, repeats=3)

#stopifnot(F)

# train each model in model list
for(i in 1:length(method_list)){
    print(paste("begin", method_list[i]))

    # tuneLength is the number of parameter values to try for each model
    model_list[[i]] <- train(classification~., data=data,
        method=method_list[i], trControl=control, tuneLength=3)
}

# train the LVQ model
# set.seed(7)
# modelLvq <- train(classification~., data=data, method="lvq", trControl=control)
# # train the GBM model
# set.seed(7)
# #modelGbm <- train(classification~., data=data, method="gbm", trControl=control, verbose=FALSE)
# # train the SVM model
# set.seed(7)
# modelSvm <- train(classification~., data=data, method="svmRadial", trControl=control)

# tune.out stores the best model obtained
# get the best model for each of the models
#TODO set model equal to best model
#model <- tune.out$best.model

# use carret resample to find the best of the best models
# collect resamples given list of models
# results <- resamples(list(LVQ=modelLvq, SVM=modelSvm))
#results <- resamples(list(LVQ=modelLvq, GBM=modelGbm, SVM=modelSvm))
results <- resamples(model_list)

# summarize the results
summary(results)

#
# test that the differences in models are significant
#
# difference in model predictions
diffs <- diff(results)
# summarize p-values for pair-wise comparisons
summary(diffs)

###
# PLOTS
###

pdf("output/classifier_parallel_plot.pdf")
parallelplot(results)
dev.off()

pdf("output/classifier_splom_plot.pdf")
splom(results)
dev.off()

pdf("output/classifier_density_plot.pdf")
densityplot(results)
dev.off()

# boxplots of results
pdf("output/classifier_box_plot.pdf")
bwplot(results)
dev.off()

# dot plots of results
pdf("output/classifier_dot_plot.pdf")
dotplot(results)
dev.off()

# ALSO: print all plots in one file for easy viewing
pdf("output/classifier_all.pdf")
#xyplot(results)
parallelplot(results)
splom(results)
densityplot(results)
bwplot(results)
dotplot(results)
dev.off()

# plot the best classifier
#pdf("output/classifier_plot.pdf")
#plot(model, data)
#dev.off()

###
# END SCRIPT
###