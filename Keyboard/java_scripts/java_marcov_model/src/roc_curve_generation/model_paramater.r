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

library(tools)

# enumerate the model parameters
model_parameter_list <- c("window_size", "token_size",
    "threshold", "user_model_size", "auth_model_size",
    "total_model_size")
#TODO ^

#TODO set n_threshold <- 20000
n_threshold <- 2000
data_directory <- "parameter_compare"

# maximum number of files which will be handled
max_files <- 100000

# fixed value for FPR
#fixed_FPR <- 0.1
fixed_FPR <- 0.01

# read all the csv files in parameter compare folder
files <- list.files(path = data_directory, pattern = ".csv",
    all.files = FALSE, full.names=FALSE)

# split the file name into usable quantities
# stored in file_data by the same name as model_parameter_list
file_data <- data.frame(file_name=files)
for(i in 1:nrow(file_data)){
    # associate model parameters
    name <- read.csv(text=as.character(file_path_sans_ext(file_data$file_name[[i]])),
        header=FALSE, col.names=model_parameter_list)

    for(j in 1:length(model_parameter_list)){
        file_data[i, model_parameter_list[[j]]] <- name[1, model_parameter_list[[j]]]
    }

    # read in the data
    file_data$data[[i]] <- read.csv(paste0(data_directory, "/",
        as.character(file_data$file_name[[i]])))
}


#TODO
file_data$total_model_size<-file_data$user_model_size+file_data$auth_model_size


# figure out which files belong with a given model parameter
# that is, what files vary the model parameter while keeping
# the other parameters constant
#
# for each model parameter in this list,
# create a list of corresponding data files
#
# a file can be added to multiple lists if multiple parameters vary
model_parameter_to_file_map <- data.frame(row.names=model_parameter_list)

# for each model parameter
# create a list of files
list_of_list <- vector("list", length(model_parameter_list))
for(j in 1:length(model_parameter_list)){
    # create a list of size max_files
    list <- vector("list", max_files)

    # decide whether each file should be added to this list
    index <- 1
    for(i in 1:nrow(file_data)){
        #TODO based on what should I add a file to each of the lists
        #TODO right now all files parameters have all files
        # why does it matter what is added to the list?
        # adding all files to every list will result
        # in a range of values at each model parameter value
        # this isn't necessarily bad.
        #
        # it gives me a way of limiting the complexity of computation
        # though if I wanted to do it for a specific set of files
        if(j != 0){
            list[[index]] <- file_data[i, "file_name"]
            index <- index + 1
        }
    }

    # remove all nulls from the list
    list <- list[!sapply(list, is.null)]

    # add the list to the list of lists
    list_of_list[[j]] <- list
}

# add the lists to the map
model_parameter_to_file_map$file_list <- I(list_of_list)

# now, all the data sets in
# model_parameter_to_file_map$file_list contain
# the data files with statistics for a given
# model parameter.
# for each model parameter
#   for each of these file in file_list,
#       I want to compute the rate data (FNR, FPR)
#
# I will add the rate data as a column in file_data
rate_data_list <- vector("list", nrow(file_data))
for(i in 1:nrow(file_data)){
    rate_data_list[[i]] <- generate_threshold_data(file_data$data[[i]], n_threshold)
}

# add the rate data to the file_data data frame
file_data$rate_data <- rate_data_list

# then I can fix FPR for each parameter
# then I can compute FNR corresponding to that FPR
# add this value as a column to the model_parameter_file_map
FNR_list <- vector("list", nrow(file_data))
for(i in 1:nrow(file_data)){
    # minimize_FNR returns the threshold at which the lowest FNR occurrs
    FNR_list[[i]] <- minimize_FNR_value(file_data$rate_data[[i]], as.numeric(fixed_FPR))
}

rate_data_list[[2]]
#head(rate_data_list[[2]])
head(FNR_list[[2]])

# add the rate data to the file_data data frame
file_data$minimum_FNR <- FNR_list

# then I can scale all the model parameters
# scale to the interval [0,1] given [min,max] for each parameter
#
# for each model parameter
for(j in 1:length(model_parameter_list)){
    # get a list of all values of this one parameter
    x <- file_data[, model_parameter_list[[j]]]

    # standardize the data between [0,1]
    standardized_list <- (x-min(x))/(max(x)-min(x))

    # remove NaN's from the list,
    # replace them with 0
    standardized_list[is.nan(standardized_list)] <- 0

    # REPLACE the actual parameter values in file_data
    file_data[, model_parameter_list[[j]]] <- standardized_list
}

# change the row names of file_data to be the file names
rownames(file_data) <- file_data[, "file_name"]

# then I can plot FNR vs. scaled model parameter value
#
# the model_parameter_to_file_map will be used
# to determine which [parameter value, minimum_FNR_value]
# should be plotted for each parameter
#
# for each model parameter
#   create a list of parameter value and minimum_pnr value
parameter_value_list_list <- vector("list", length(model_parameter_list))
minimum_FNR_list_list <- vector("list", length(model_parameter_list))
for(i in 1:length(model_parameter_list)){
    file_list <- unlist(model_parameter_to_file_map[model_parameter_list[[i]], "file_list"][[1]])

    # for each file in the list,
    # add the corresponding: parameter value and
    # minimum_FNR_value to plot_series
    parameter_value_list <- vector("list",  length(file_list))
    minimum_FNR_list <- vector("list",  length(file_list))
    for(j in 1:length(file_list)){
        # grab the values associated with the file
        # from file_data
        parameter_value_list[[j]] <- file_data[file_list[[j]],
            model_parameter_list[[i]]]
        minimum_FNR_list[[j]] <- unlist(file_data[file_list[[j]],
            "minimum_FNR"])
    }

    # place the lists into the data frame
    parameter_value_list_list[[i]] <- parameter_value_list
    minimum_FNR_list_list[[i]] <- minimum_FNR_list
}

# place the data into a data frame
plot_series <- data.frame(parameter_value=I(parameter_value_list_list),
    minimum_FNR=I(minimum_FNR_list_list), row.names=model_parameter_list)

#
# model_parameters vs FNR
# given a fixed FPR
# describes how each of the model parameters affects the authentication accuracy
#
# plot one line for each model parameter
# the values to plot for this line are stored in plot_series
#
pdf("output/model_parameter.pdf")

# create colors, linetypes, for 4 situations
colors <- rainbow(7, start=0.4)
# determines the type of line
linetype <- c(1,2,4,5,6,7)
# determines the symbol used for the line
plotchar <- c(18:23)

# set up the plot ( this doesn't plot anything anyway,
# so the arguments doen't matter. )
plot(c(0.0), c(0.0),
    xlab="Parameters", ylab="FNR", main="Parameters vs. FNR",
    xlim=c(0,1), ylim=c(0,1), type="n")

# plot each of the parameters
for(i in 1:length(model_parameter_list)){
    lines(unlist(plot_series[model_parameter_list[[i]], "parameter_value"]),
        unlist(plot_series[model_parameter_list[[i]], "minimum_FNR"]),
        type="p", lwd=1.5, lty=linetype[i], col=colors[i], pch=plotchar[i])
}

# explain that the FPR is fixed
text(.50, .99, labels=paste("FPR =", fixed_FPR))

# make a legend
legend(0.60, 1.0, model_parameter_list, cex=1.0, col=colors,
    lty=linetype, title="Parameter") #pch=plotchar,

dev.off()

# create a plot which makes a line along the minimums for each list
# 1. get min points for each value of model parameter
parameter_value_list_list <- vector("list", length(model_parameter_list))
minimum_FNR_list_list <- vector("list", length(model_parameter_list))
for(i in 1:length(model_parameter_list)){
    parameter_series_list <- unlist(
        plot_series[model_parameter_list[[i]], "parameter_value"])
    FNR_series_list <- unlist(plot_series[model_parameter_list[[i]], "minimum_FNR"])

    # order the list of unique parameter values so
    # the points will ultimatly appear in order
    unique_value_list <- sort(unique(parameter_series_list))

    # for each unique value,
    # get the minimum
    parameter_value_list <- vector("list", length(unique_value_list))
    minimum_FNR_list <- vector("list", length(unique_value_list))
    for(j in 1:length(unique_value_list)){
        # get the indexes of this parameter value
        value_index_list <- parameter_series_list == unique_value_list[[j]]

        # get the FNR corresponding to this parameter value
        FNR_value_list <- FNR_series_list[value_index_list]

        # find the index of the minimum FNR value
        min_index <- which.min(FNR_value_list)

        # place the values into the list to plot
        # along with the corresponding parameter value
        parameter_value_list[[j]] <- unique_value_list[[j]]
        minimum_FNR_list[[j]] <- FNR_value_list[min_index]
    }

    # place the lists into the data frame
    parameter_value_list_list[[i]] <- parameter_value_list
    minimum_FNR_list_list[[i]] <- minimum_FNR_list
}

# 2. create a series of these points in order
plot_series <- data.frame(parameter_value=I(parameter_value_list_list),
    minimum_FNR=I(minimum_FNR_list_list), row.names=model_parameter_list)

# 3. plot this series of points
pdf("output/model_parameter_minimum.pdf")
# set up the plot ( this doesn't plot anything anyway,
# so the arguments doen't matter. )
plot(c(0.0), c(0.0),
    xlab="Parameters", ylab="FNR", main="Parameters vs. FNR",
    xlim=c(0,1), ylim=c(0,1), type="n")

# plot each of the parameters
for(i in 1:length(model_parameter_list)){
    lines(unlist(plot_series[model_parameter_list[[i]], "parameter_value"]),
        unlist(plot_series[model_parameter_list[[i]], "minimum_FNR"]),
        type="b", lwd=1.5, lty=linetype[i], col=colors[i], pch=plotchar[i])
}

# explain that the FPR is fixed
text(.50, .99, labels=paste("FPR =", fixed_FPR))

# make a legend
legend(0.60, 1.0, model_parameter_list, cex=1.0, col=colors,
    lty=linetype, title="Parameter") #pch=plotchar,

dev.off()


# useful print statements
#unlist(plot_series["token_size", "parameter_value"])
#unlist(plot_series["token_size", "minimum_FNR"])

#unlist(plot_series["window_size", "parameter_value"])
#unlist(plot_series["window_size", "minimum_FNR"])
