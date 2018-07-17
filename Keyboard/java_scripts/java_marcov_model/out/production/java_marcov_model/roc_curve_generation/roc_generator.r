#! /usr/bin/Rscript

source("utility.r")

# load libraries
#library(ggplot2)

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")
rate_data <- generate_threshold_data(compare_data, 20000)

#
# ROC curve
#
# plot the data FNP vs FPR
pdf("output/ROC.pdf")

plot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

# tell R I am done plotting
dev.off()

# display any warnings
#warnings()
