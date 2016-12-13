#! /usr/bin/R

# install libraries
#install.packages("ggplot2")
#install.packages("car")

# load libraries
library(ggplot2)
#library(car)

#
# given a threshold, compare_data
# compute the FNR -- false negative rate
# false negatives / positives
#
FNR <- function(threshold, compare_data){
    # count the number of false negatives
    number_false_negatives <- sum(
        (compare_data$compare_value < threshold) & (compare_data$positive == 1))

    # count the number of negatives in the data set
    number_positives <- sum(compare_data$positive == 1)

    return(number_false_negatives / number_positives)
}

#
# given a threshold, compare_data
# compute the FPR -- false positive rate
# false positives / negatives
#
FPR <- function(threshold, compare_data){
    # count the number of false positives
    number_false_positives <- sum(
        (compare_data$compare_value >= threshold) & (compare_data$positive == 0))

    # count the number of negatives in the data set
    number_negatives <- sum(compare_data$positive == 0)

    return(number_false_positives / number_negatives)
}

#
# given a fixed FPR,
# determine threshold which minimizes FNR
# return that threshold
#
minimize_FNR <- function(rate_data, fpr){
    # for all FPR <= fpr find smallest FNR
    # FNR will be biggest when threshold == 1
    #which happens at nrow(rate_data) index
    threshold_index <- as.integer(nrow(rate_data))

    for(i in 1:nrow(rate_data)){
        # for each FPR <= fpr
        if(rate_data[i,"FPR"] <= fpr){
            # is the fnr at this index smaller than the
            # current smallest one?
            if(rate_data[i,"FNR"] < rate_data[threshold_index,"FNR"]){
                # fnr is smaller at index i
                # compared to index threshold_index
                threshold_index <- as.integer(i)
            }
        }
    }

    return(rate_data[threshold_index, "threshold"])
}

#
# compute authentication accuracy given a
# set of [should be positive/negative, compare_value]
# and threshold
# return the accuracy or (times correct / total authentications)
#
authentication_accuracy <- function(data, threshold){
    times_correct <-
        sum(data$positive == 1 & data$compare_value >= threshold) +
        sum(data$positive == 0 & data$compare_value < threshold)

    return(times_correct / nrow(data))
}

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")

# create a data frame of thresholds
threshold <- seq(0, 1.0, by=0.00005)
rate_data <- data.frame(threshold)

# for each threshold
for(i in 1:nrow(rate_data)){
    # compute FNR
    rate_data[i, "FNR"] <- FNR(rate_data$threshold[i], compare_data)

    # compute FPR
    rate_data[i, "FPR"] <- FPR(rate_data$threshold[i], compare_data)
}

# compute the best threshold
# to generate authentication accuracy statistics
#
# lowest FNR at a FPR of .1
FPR_0_1_threshold <- minimize_FNR(rate_data, as.numeric(0.001))

# compute the authentication accuracy | threshold
FPR_0_1_threshold
authentication_accuracy(compare_data, FPR_0_1_threshold)
#TODO

#
# ROC curve
#
# plot the data FNP vs FPR
pdf("ROC.pdf", width = "6", height = "6")

qplot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

# tell R I am done plotting
dev.off()

#
# model_parameters vs authentication accuracy
# describes how each of the model parameters affects the authentication accuracy
#
#TODO add more information to compare_data.csv which can be used for this
pdf("authentication_accuracy_vs_total_interactions.pdf", width = "6", height = "6")

qplot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

dev.off()

#
# understand the 4 situations
# same_user_same_device
# same_user_different_device
# different_user_same_device
# different_user_different_device
#
# how good is this system in each case
#
#TODO add more information to compare_data.csv which can be used for this
pdf("authentication_accuracy_vs_total_interactions.pdf", width = "6", height = "6")

qplot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

dev.off()

# display any warnings
warnings()