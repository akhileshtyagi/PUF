##
# the purpose of this file is to compare
# different machine learning classifiers
# and determine which is the best for solving
# this problem
##
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
##

##
# raw data
##
# [key, pressure] [classification]
#raw_data <- read_raw_data("../../data_sets")
#data <- expand_raw_data(raw_data)

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
data <- read_chain("chain_data/2_2_1000_800_800")

# way 1 of giving it to the classifier.... 20% accuracy
#raw_data <- read_chain_data("chain_data/2_2_1000_10000_10000")
#data <- convert_feature_chain_data(raw_data)

#data <- format_data(raw_data)

#print(head(data))
#stopifnot(F)

# remove NA from the data
before_removal <- nrow(data)
data <- na.omit(data)
print(paste("NA rows removed:", before_removal - nrow(data)))

# define the machine learning methods to use
method_list_test <- c("svmRadial")
method_list_good <- c("svmLinear2", "svmRadial", "svmPoly", "ranger", "nb")
method_list <- method_list_test #TODO change to _good

# make a list for models
model_list <- vector("list", length(method_list))
names(model_list) <- method_list

# prepare training scheme(S)
# 3 repeats of 10 fold crossvalidation
control <- trainControl(method="repeatedcv", number=10, repeats=REPEATS, timingSamps=20)

# extract featues and classification
X = data[c(-1)]
y = factor(data$classification)

#TODO 
#print(head(data))
#print(head(X))
#print(head(y))
#stopifnot(FALSE)

# specify tuning parameters
grid <-  expand.grid(cost = c(16))

# train each model in model list
for(i in 1:length(method_list)){
    print(paste("begin", method_list[i]))

    # tuneLength is the number of parameter values to try for each model
    # this assumes the row names are the classifications
    #model_list[[i]] <- train(data, factor(row.names(data)),
    #   method=method_list[i], trControl=control, tuneLength=3)

	model_list[[i]] <- train(X, y, method=method_list[i], trControl=control,
				#TODO (pick one)
				#tuneLength=TUNE_LENGTH)
				tuneGrid=grid)
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
