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
# i'm trying to say something about how a given
# model parameter affects the compare values
source("utility.r")

# enumerate the model parameters
model_parameter_list <- c("window_size", "token_size",
    "threshold", "user_model_size", "auth_model_size")

#TODO set n_threshold <- 20000
n_threshold <- 2000

# read all the csv files in parameter compare folder
#TODO

#TODO somehow I need to figure out how to use this data

# for each model parameter in this list,
# read the corresponding data file
model_parameter_data <- data.frame(model_parameter_list)

for(i in 1:nrow(model_parameter_data)){
    # paste together the name of the file, _compare_data.csv
    model_parameter_data$compare_data[[i]] <- read.csv(
        paste("parameter_compare/",
        model_parameter_data$model_parameter_list[[i]],
        "_compare_data.csv", sep=""))
}

# I want rate data for each of the possible values of
# each model parameter
#
# extract a list of possible values of each model parameter
for(i in 1:nrow(model_parameter_data)){
    # paste together the name of the file, _compare_data.csv
    model_parameter_data$possible_value[[i]] <- unique(
        model_parameter_data$compare_data[[i]]$model_parameter_value)
}

#TODO the next step is to create code which outputs the
#TODO appropriate compare_data.csv files
#TODO basically the same as now but with a model_parameter_value column

head(model_parameter_data$compare_data[[1]])
head(model_parameter_data$possible_value[[1]])

# stop execution here!
stopifnot(FALSE)

# generate threshold data for each model_parameter_value
#TODO
for(i in 1:nrow(model_parameter_data)){
    # paste together the name of the file, _compare_data.csv
    model_parameter_data$rate_data[[i]] <- generate_threshold_data(
        model_parameter_data$compare_data[[i]], n_threshold)
}






#TODO adjust up to 20000
rate_data <- generate_threshold_data(compare_data, 2000)

# compute the best threshold
# to generate authentication accuracy statistics
#
# lowest FNR at a FPR of .1
FPR_threshold <- minimize_FNR(rate_data, as.numeric(0.001))

# compute the authentication accuracy | threshold
FPR_threshold
#TODO

#
# model_parameters vs authentication accuracy
# describes how each of the model parameters affects the authentication accuracy
#
#TODO add more information to compare_data.csv which can be used for this
#TODO or perhaps generate a new csv file for each model parameter with:
#TODO [positive, compare_value, model parameter value]
#TODO for each model parameter:
#TODO   vary the model parameter while holding the others constant
#
# plot one line for each model parameter
pdf("output/model_parameter.pdf")

# create colors, linetypes, for 4 situations
colors <- rainbow(6, start=0.4)
# determines the type of line
linetype <- c(1,2,4,5)
# determines the symbol used for the line
plotchar <- c(18:22)

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
legend(0.45, 1.0, model_parameter_list, cex=1.0, col=colors,
    lty=linetype, title="Situation") #pch=plotchar,

dev.off()