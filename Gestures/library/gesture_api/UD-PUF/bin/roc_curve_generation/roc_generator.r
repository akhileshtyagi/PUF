#! /usr/bin/R

source("utility.r")

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")

# create a data frame of thresholds
threshold <- seq(0, 1.0, by=0.005)
rate_data <- data.frame(threshold)

# for each threshold
for(i in 1:nrow(rate_data)){
    # compute FNR
    rate_data[i, "FNR"] <- FNR(rate_data$threshold[i], compare_data)

    # compute FPR
    rate_data[i, "FPR"] <- FPR(rate_data$threshold[i], compare_data)
}

# plot the data FNP vs FPR
pdf("output/ROC.pdf", width = "6", height = "6")

plot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1), type="b")

# tell R I am done plotting
dev.off()

# display any warnings
warnings()

#rate_data
#compare_data