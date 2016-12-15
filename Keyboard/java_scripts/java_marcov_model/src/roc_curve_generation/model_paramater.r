#
# generate graph for how the 'performance'
# of the system depends on each of the model
# parameters
#
# general idea:
#   1. fix a threshold value given a desired FPR
#   2. evaluate how FNR preforms when a given model parameter is changed
#   3. plot one line for each model parameter
#   3.1 plot is model parameter value normalized to
#       [0,1] based on [min, max] for that parameter vs.
#       FNR
#
source(utility.r)

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")
rate_data <- generate_threshold_data(compare_data, 20000)

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
# model_parameters vs authentication accuracy
# describes how each of the model parameters affects the authentication accuracy
#
#TODO add more information to compare_data.csv which can be used for this
pdf("output/model_parameter.pdf", width = "6", height = "6")

plot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1))

dev.off()