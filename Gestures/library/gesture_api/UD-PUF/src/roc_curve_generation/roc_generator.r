#! /usr/bin/R

# install libraries
#install(ggplot2)
#install.packages(car)

# load libraries
#library(ggplot2)
library(car)

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

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")

# create a data frame of thresholds
threshold <- seq(0, 1.0, by=0.0005)
rate_data <- data.frame(threshold)

# for each threshold
for(i in 1:nrow(rate_data)){
    # compute FNR
    rate_data[i, "FNR"] <- FNR(rate_data$threshold[i], compare_data)

    # compute FPR
    rate_data[i, "FPR"] <- FPR(rate_data$threshold[i], compare_data)
}

# plot the data FNP vs FPR
pdf("ROC.pdf", width = "6", height = "6")

plot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

# tell R I am done plotting
dev.off()

# display any warnings
warnings()

#rate_data
#compare_data