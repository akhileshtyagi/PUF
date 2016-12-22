#
# this file contains methods of response normalization
#
# used by response encoding functions
#

###
# FUNCTIONS
###

#
# master normalization method
# a response is a row in the
# response encoding matrix
#
# return a row in the matrix corresponding to the
# normalized response
#
normalize_response <- function(response_matrix, response_index){
    #return(normalize_response_0(response_matrix, response_index))
    return(normalize_response_dummy(response_matrix, response_index))
}

normalize_response_0 <- function(response_matrix, response_index){
    #TODO
    #return()
    return(response_matrix[response_index,])
}

#
# return the first 5 elements from each response
# NOTE: this is for testing purposes
#
# results are a three element list
#
normalize_response_dummy <- function(response_matrix, response_index){
    #print(head(response_matrix[response_index,1][[1]][1:5]))
    list <- vector("list", 3)

    for(k in 1:3){
        #print(response_matrix[[response_index]])
        list[[k]] <- response_matrix[response_index, k][[1]][1:5]
    }

    #print(head(response_matrix[response_index,]))
    #print(head(list))
    #stopifnot(F)

    # list has three elements
    # each element in the list is a vector
    return(list)
}