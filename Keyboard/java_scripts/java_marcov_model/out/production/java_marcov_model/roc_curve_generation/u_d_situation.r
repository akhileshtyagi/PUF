#
# generatate graphs for the 4 situations
#
source("utility.r")

# create a data frame with the given compare values
compare_data <- read.csv("compare_data.csv")

# rate_data$[user][device]
n_threshold <- 20000 #TODO put up to 20000
x <- c(1:(n_threshold+1))
rate_data <- data.frame(x)

# we want to compare each case against same_user_same_device
# therefore, each set will include same_user_same_device data
rate_data$ss <- generate_threshold_data(compare_data, n_threshold)
rate_data$sd <- generate_threshold_data(
    compare_data_user_device(compare_data, TRUE, FALSE), n_threshold)
rate_data$ds <- generate_threshold_data(
    compare_data_user_device(compare_data, FALSE, TRUE), n_threshold)
rate_data$dd <- generate_threshold_data(
    compare_data_user_device(compare_data, FALSE, FALSE), n_threshold)

#
# understand the 4 situations
# same_user_same_device
# same_user_different_device
# different_user_same_device
# different_user_different_device
#
# how good is this system in each case
#
# needed to add to compare_data.csv
#   user, device
#
# plot a line on the ROC curve for each case
# in each case plot the power of telling
# a given situation vs. all other possibilities
# the other option would be to take each pair and compare them
pdf("output/u_d_situation.pdf", width = "6", height = "6")

# create colors, linetypes, for 4 situations
colors <- rainbow(6, start=0.4)
#colors <- heat.colors(8)
# determines the type of line
linetype <- c(1,2,4,5)
# determines the symbol used for the line
plotchar <- c(18:22)
# determines the name displayed in the legend
series_name <- c("same user | same device",
    "same user | different device", "different user | same device",
    "different user | different device")

# set up the plot
plot(rate_data$FNR, rate_data$FPR,
    xlab="FPR", ylab="FNR", main="ROC Curve",
    xlim=c(0,1), ylim=c(0,1), type="n")

# plot each of the situations
for(i in 2:ncol(rate_data)){
    lines(rate_data[[i]]$FNR, rate_data[[i]]$FPR, type="l", lwd=1.5,
        lty=linetype[i-1], col=colors[i-1], pch=plotchar[i-1])
}

# make a legend
legend(0.45, 1.0, series_name, cex=1.0, col=colors,
    lty=linetype, title="Situation") #pch=plotchar,

dev.off()
