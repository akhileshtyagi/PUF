#
# this file contains various methods for encoding a
# response to train the svm
#

#
# master response encoding function
# will call the current best response encoding
#
response_encoding <- function(response){
    return(response_encoding_dummy(response))
}

#
#
#
response_encoding_0 <- function(response){
    #TODO
    encoding <- NULL

    return(encoding)
}

#
# generates a dummy response encoding
#
response_encoding_dummy <- function(response){
    encoding <-matrix(rnorm(length(response)*2), ncol=2)

    return(encoding)
}