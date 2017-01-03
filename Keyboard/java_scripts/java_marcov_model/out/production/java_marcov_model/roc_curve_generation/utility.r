#
# this file provides functions used by all classes
#

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

    #print(compare_data$positive)

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
# given rate_data, fixed FPR
# determine the minimum FNR value
#
# same as minimize_FNR, but gives the FNR value
# associated with the threshold
#
minimize_FNR_value <- function(rate_data, fpr){
    minimizing_threshold <- minimize_FNR(rate_data, fpr)

    # find the index of the threshold
    index <- match(minimizing_threshold, rate_data$threshold)

    return(rate_data[index, "FNR"])
}

#
# compute authentication accuracy given a
# set of [should be positive/negative, compare_value]
# and threshold
# return the accuracy or (times correct / total authentications)
#
#TODO I don't know if this is correct
authentication_accuracy <- function(data, threshold){
    times_correct <-
        sum(data$positive == 1 & data$compare_value >= threshold) +
        sum(data$positive == 0 & data$compare_value < threshold)

    return(times_correct / nrow(data))
}

#
# generate threhold, FNR, FPR for compare_data
#
generate_threshold_data <- function(compare_data, n_thresholds){
    # create a data frame of thresholds 0.00005
    threshold <- seq(0, 1.0, by=(1/n_thresholds))
    rate_data <- data.frame(threshold)

    # for each threshold
    for(i in 1:nrow(rate_data)){
        # compute FNR
        rate_data[i, "FNR"] <- FNR(rate_data$threshold[i], compare_data)

        # compute FPR
        rate_data[i, "FPR"] <- FPR(rate_data$threshold[i], compare_data)
    }

    return(rate_data)
}

#
# create a set of compare data for each of the cases
#
# given: same_user?, same_device? as true or false
# and the origional list of compare data
#
# return a data frame [positive][compare_value]
# for the user devcie combination specified
#
compare_data_user_device <- function(compare_data, is_same_user, is_same_device){
    match_vector <- (
        (compare_data$same_user == is_same_user) &
        (compare_data$same_device == is_same_device) |
            ((compare_data$same_user == 1) &
            (compare_data$same_device == 1))
        )

    return(compare_data[match_vector, ])
}