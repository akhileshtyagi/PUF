#
# this file contains various methods for encoding a
# response to train the svm
#
source("utility.r")
source("normalize_response.r")

library("e1071")

# useful resources
# http://stackoverflow.com/questions/31755233/r-fill-multidimensional-array-by-row

###
# FUNCTIONS
###

#
# master response encoding function
# will call the current best response encoding
#
response_encoding <- function(response){
    #print(sapply(response$response, `[[`, "x"))
    #stopifnot(F)

    # i want x,y,pressure to be in separate columns
    response <- array(c(sapply(response$response, `[[`, "x"),
        sapply(response$response, `[[`, "y"),
        sapply(response$response, `[[`, "pressure")),
        dim = c(length(response$response), 3))

    return(response_encoding_1(response))
    #return(response_encoding_0(response))
    #return(response_encoding_dummy(response))
}

#
# normalize the responses to
# be all the same length
# create a 3-D matrix
#
# this will work so long as normalize_response
# is always the same length
#
response_encoding_1 <- function(response){
    # list of normalized responses
    normalized_response_list <- vector("list", nrow(response))

    # for each row of the matrix
    for(i in 1:nrow(response)){
        normalized_response_list[[i]] <- normalize_response(response, i)
    }

    # create a 3D matrix
    encoding <- aperm(array(
           data = c(
           sapply(normalized_response_list, `[[`, 1),
           sapply(normalized_response_list, `[[`, 2),
           sapply(normalized_response_list, `[[`, 3)
           ),
           dim = c(length(normalized_response_list[[1]][[1]]), nrow(response), 3)))

    # switch the 2nd and 3rd dimensions
    encoding <- aperm(encoding, c(2,1,3))

    return(encoding)
}

#
# encode the response as mean(x), mean(y), mean(pressure)
#
# NOTE: this is a dumb way to do it
# essentially this is just for testing
#
response_encoding_0 <- function(response){
    # response comes in as a 2 dimensional matrix
    #       [,1]    [,2]    [,3]
    # [1,]  List(x) List(y) List(pressure)
    # [2,]  List(x) List(y) List(pressure)
    #
    # perhaps I could make all the lists the same
    # size
    #
    # collapse each featue down to a single value based on the mean
    #
    # initialize encoding
    encoding <- array(0, dim= c(nrow(response), 3))

    # compute the mean of each element in response matrix
    for(i in 1:nrow(response)){
        for(j in 1:ncol(response)){
            #print(mean(response[i,j][[1]]))
            encoding[i,j] <- mean(response[i,j][[1]])
        }
    }

    #print(head(encoding))
    #stopifnot(F)

    return(encoding)
}

#
# generates a dummy response encoding
#
response_encoding_dummy <- function(response){
    encoding <-matrix(rnorm(length(response)*2), ncol=2)

    return(encoding)
}

###
# TEST METHODS
###

#
# train an svm,
# this is for testing
#
train_svm_test <- function(encoded_response){
    # generate a dummy set of classifications
    dummy_class <- c(rep(-1, length(encoded_response)/2),
        rep(1, length(encoded_response) - length(encoded_response)/2))

    # create data frame
    data <- data.frame(
            "classification" = dummy_class,
            "response" = encoded_response,
            stringsAsFactors=FALSE)

    # encode each of the classifications as an integer,
    # otherwise apparently they will get cast to floats
    # in unpredictable ways
    #
    # first, make a list of all the unique classifications
    # the rows are named the classification value
    classification_map <- data.frame(row.names = unique(data$classification))

    # second, assign an integer value to each unique classification
    classification_map$value <- as.factor(c(1:nrow(classification_map)))

    # third, replace the data classifications with integers
    classification_list <- vector("integer", nrow(data))
    for(i in 1:nrow(data)){
        # row names are classifications, value column holds the integer mapping
        #data$classification[[i]] <- classification_map[data$classification[[i]], "value"]
        classification_list[[i]] <- classification_map[data$classification[[i]], "value"]
    }

    # put the classifications into data
    data <- subset(data, select=-classification)
    data$classification <- classification_list

    # change the type of classification to factor
    data$classification <- as.factor(data$classification)

    # create an svm model
    model <- svm(data$classification~., data=data, type="C", kernel="linear")

    #NOTE: this is not needed because cross-validation is already doing this
    # use the model to predict on the test data
    #prediction <- predict(model, predict_data)
    #table(predict=prediction, truth=predict_data$classification)

    # plot the model
    pdf("output/test_svm.pdf")

    plot(model, data, response.1~response.2)
    plot(model, data, response.1~response.3)
    plot(model, data, response.2~response.3)

    dev.off()
}

#
# test script
#
# set options to print callstack
#options(error=function()traceback(2))

#raw_data <- read_raw_data()
#encoded_response <- response_encoding(raw_data)
#train_svm_test(encoded_response)