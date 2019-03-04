#! /usr/bin/Rscript

##
# the purpose of this file is to compare
# different machine learning classifiers
# and determine which is the best for solving
# this problem
##
#install.packages('caret', dependencies=TRUE)
library(caret)

source("data_read.r")

REPEATS <- 1 #3
TUNE_LENGTH <- 1 #3

#TODO what does accuracy mean here?
#TODO I don't htink using k-fold cross validation in this
#TODO way is actually giving me what I want for accuracy.
#TODO this needs further thought
##
# The accuracy metric is incorrect here
#
# what it gives me is whether a user can be predicted based on 
# a subset of the (ngram, key, probability, weight)
#
# what I actually want is whether they can be predicted from
# a model built from a SUBSET of the RAW DATA
# 	This would include many of the windows
#	This would not have exactly the same probability or weight for a given (ngram, key)
#
# I could produce training and validation output files
#TODO is there a better way to do this?

# I also want to know how a GROUP of (ngram, key, probability, weight) are
# predicted.
# Not how each individual one is predicted.

# TODO TODO I could try to pick out the top 50 or so highest probability ngrams
##

###############
#TODO one thing I could try is to make each n-gram one feature
#TODO for each classification I then have ngram_0, key_0, probability_0, weight_0
#
#TODO I could then (for each classification) make many such models (chain_data files) with, say 800, touch events
#
# the problem with this approach is how to get the same featues for all models
# 	- ensure all featues line up
#	- ngram_0 must always correspond to the same window
#
# TODO to implement this
#	. modify ChainDataGenerate to produce multiple files based on different data
#	. modify R files which convert this chain data into features
###############

##
# begin script
# set one of the following to TRUE
#
# raw_data is [time,key,pressure]
# chain_data_b does not work ???
# successor_vector_b is [ngram,pngram,pkey1,pkey2,...,pkeyN]
# token_data is [time,key,tokenized_pressure]
#
# Z_SEQUENCE expands the raw data into n-grams of this length.
# Z_SEQUENCE <- 1  is equivalent to the origional data set
##
raw_data_b <- FALSE
chain_data_b <- FALSE
successor_vector_b <- FALSE
token_data_b <- TRUE
Z_SEQUENCE <- 1

##
# raw data
##
# [key, pressure] [classification]
if(raw_data_b){
	raw_data <- read_raw_data("../../data_sets")
	data <- expand_raw_data(raw_data)
	data <- create_z_sequence(data,Z_SEQUENCE)
}

# reform the data to mix all the classifications together.
# this will allow k-fold cross-validation to train with equal numbers from each data set
#data <- evenly_distribute_class(data)

##
# chain_data
##
# [classification] [window, successor, probability]
# [classification] [window, successor, probability, weight] 
#	weight  = p(successor) / n (steady state probability of being in successor)
#TODO is there any other way I could privde the data to the classifier?
#TODO how do i uniquely represent the windows? 
#	(perhaps each token in window is a different feature?)
#	perhaps map<possible_window, int> ( although I don't think this option is good )
#	
# all tokens need to be represented differently in 'chain_data'
#	perhaps make a map<possible_token, int>
#TODO update PUF repository.... (remove .Rdata)
#data <- read_chain("chain_data/2_2_1000_10000_10000")
#data <- read_chain("chain_data/2_2_1000_800_800")
#data <- read_chain("chain_data/1_2_1000_1600_3200")
#data <- read_chain("chain_data/1_2_1000_6400_6400")

# way 1 of giving it to the classifier.... 20% accuracy
if(chain_data_b){
	raw_data <- read_chain_data("chain_data/2_2_1000_10000_10000")
	data <- convert_feature_chain_data(raw_data)
	data <- format_data(raw_data)
}

# output format
# [classification,ngram,prob_ngram,<successor_vector>]
if(successor_vector_b){
	raw_data <- read_successor_data("successor_data/1_2_1000_3200_3200")
	#data <- expand_successor_data(raw_data)
	data<-data.frame(raw_data)
}

if(token_data_b){
	raw_data <- read_raw_data("token_data")
	data <- expand_raw_data(raw_data)
	data <- create_z_sequence(data,Z_SEQUENCE)
}

#print(head(data))
#stopifnot(F)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# define the machine learning methods to use
method_list_test <- c("svmLinear2")
method_list_good <- c("svmLinear2", "svmRadial", "svmPoly")#, "ranger", "nb")
method_list <- method_list_test #TODO change to _good

# make a list for models
model_list <- vector("list", length(method_list))
names(model_list) <- method_list

# prepare training scheme(S)
# 3 repeats of k-fold crossvalidation
control <- trainControl(method="repeatedcv", number=5, repeats=REPEATS, timingSamps=20, sampling="down")

# extract featues and classification
if(raw_data_b){
	# raw data
	# X is all columns not the first column
	X = data[c(-1)]
	# y is the first column
	y = factor(data$X1) 
}else if(token_data_b){
	X = data[c(-1)]
	y = factor(unlist(data[c(1)]))
}else{
	# successor data
	X=lapply(data,unlist)
	X=X[1:length(X)-1]
	X=data.frame(X)
	#y = factor(data$X1) 
	#y = factor(data$classification)
	#y = data[c(1)]
	#y = factor(data[ncol(data)])
	y=factor(unlist(data[[ncol(data)]]))
	#y=unlist(data[[ncol(data)]])
}

#TODO 
#print(head(data))
#print(head(X))
#print(head(y))
#stopifnot(FALSE)

# specify tuning parameters
grid <- expand.grid(cost = c(16))

# train each model in model list
for(i in 1:length(method_list)){
    print(paste("begin", method_list[i]))

    # tuneLength is the number of parameter values to try for each model
    # this assumes the row names are the classifications
    #model_list[[i]] <- train(data, factor(row.names(data)),
    #   method=method_list[i], trControl=control, tuneLength=3)

	model_list[[i]] <- train(X, y, method=method_list[i], trControl=control,tuneLength=TUNE_LENGTH)
				#TODO (pick one)
				#tuneGrid=grid)
}

# print the results for each model
lapply(model_list, print)

stopifnot(FALSE)


# use carret resample to find the best of the best models
# collect resamples given list of models
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
