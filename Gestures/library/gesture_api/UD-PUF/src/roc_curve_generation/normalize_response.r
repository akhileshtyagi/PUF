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
# NA is returned in the condition a normalized
# response cannot be formed
#
normalize_response <- function(response_matrix, response_index){
    #TODO switch functions
    return(normalize_response_null(response_matrix, response_index))
    #return(normalize_response_1(response_matrix, response_index))
    #return(normalize_response_0(response_matrix, response_index))
    #return(normalize_response_dummy(response_matrix, response_index))
}

#
# use the full length of the longest response
# fill in 0's for missing values in shorter responses
#
normalize_response_null <- function(response_matrix, response_index){
    list <- vector("list", 3)

    # print(max(unlist(lapply(response_matrix[,1], length))))
    # print(head(response_matrix))
    # # print(response_matrix)
    # stopifnot(F)

    list_size <- max(unlist(lapply(response_matrix, length)))
    for(k in 1:3){
        nr_vector <- vector("numeric", list_size)

        # create vector of list_size from the response
        # if the response has fewer than list_size elements,
        # fill in 0's for the missing values
        r_value_list <- response_matrix[response_index, k][[1]]
        for(i in 1:length(r_value_list)){
            nr_vector[i] <- r_value_list[i]
        }

        list[[k]] <- nr_vector
    }

    # list has three elements
    # each element in the list is a vector
    # print(list_size)
    # print(list[])
    # stopifnot(F)

    return(list)
}

#
# return the first N elements from each response
# and the last N elements from eah response
#
normalize_response_1 <- function(response_matrix, response_index){
    #print(head(response_matrix[response_index,1][[1]][1:5]))
    #n_feature <- ncol(response_matrix)
    N <- 5
    list <- vector("list", 3)

    #print(n_feature)
    #print(head(response_matrix))
    #stopifnot(F)

    #print(response_index)
    #print(head(response_matrix))
    #print(response_matrix[response_index, ])

    for(k in 1:3){
        list_size <- length(response_matrix[response_index, k][[1]])

        if(list_size >= (2*N)){
            nr_vector <- response_matrix[response_index, k][[1]][c(
                1:N, (list_size-N+1):list_size)]
        }else{
            #TODO is this a vector filled with 0?
            nr_vector <- vector("numeric", 2*N)
        }

        list[[k]] <- nr_vector
    }

    # list has three elements
    # each element in the list is a vector
    # print(list[])
    # stopifnot(F)

    return(list)
}

#
# 1. create N sampling points along x or y axis
#       (whichever axis has greatest max-min)
# 2. At each sampling point,
#       project pressure value downward,
#       compute the value at the sampling point
#       based on left and right neighbors
#
# all returned lists must be the same size
# and they will be because of the sampling points
#
#TODO this needs to be redone,
#TODO the sampling points need to be the same for
#TODO each response belonging to a particular class
#
#TODO causes 103 NA rows removed instead of 3
normalize_response_0 <- function(response_matrix, response_index){
    n_sampling_points <- 32
    list <- vector("list", 3)

    # acquire the series for this response from the matrix
    x_series <- response_matrix[response_index,1][[1]]
    y_series <- response_matrix[response_index,2][[1]]
    p_series <- response_matrix[response_index,3][[1]]

    # if any of x_series, y_series, p_series are NA, return list with NA
    if(is.na(x_series) || is.na(y_series) || is.na(p_series)){
        list[[1]] <- rep(NA,n_sampling_points)
        list[[3]] <- rep(NA,n_sampling_points)
        list[[2]] <- rep(NA,n_sampling_points)

        return(list)
    }

    #TODO perhaps this isn't a good idea?
    #TODO is some information lost?
    # if x-coverages < y-coverage
    if((max(x_series) - min(x_series)) < (max(y_series) - min(y_series))){
        # swap x_series, y_series
        temp_series <- x_series
        x_series <- y_series
        y_series <- temp_series
    }
    # everything can now be done with respect to x_series
    # covering a larger distance

    # compute the sampling points in x
    list[[1]] <- seq(
        from = min(x_series),
        to = max(x_series),
        length.out = n_sampling_points
    )

    # set all y to 0
    list[[2]] <- rep(0, n_sampling_points)

    # compute the normlized pressure at each sampling point
    nr_vector <- vector("numeric", 3)

    # first point gets pressure value of first point
    nr_vector[1] <- p_series[1]

    # for the middle n-2 points
    for(i in 2:(n_sampling_points-1)){
        # list[[1]][i] is the current sampling point x_value
        #
        # get the left neighbor index of this sampling point in x_series
        left_index <- sum((x_series <= list[[1]][i]) == TRUE)

        # compute the slope of the pressure between
        # left and right neighbors
        left_pressure <- p_series[left_index]
        right_pressure <- p_series[left_index+1]
        left_right_distance <- abs(x_series[left_index + 1] - x_series[left_index])

        # will be negative if right pressure is lower than left pressure
        slope <- (right_pressure - left_pressure) / left_right_distance

        # infinite slope => left, right neighbor have same x
        # this means te pressure should really just be the
        # value at the point, setting slope to 0 accomplishes this
        print(left_pressure)
        print(right_pressure)
        print(left_right_distance)
        print(slope)
        if(is.infinite(slope)){
            slope <- 0
        }

        # given the slope, compute the pressure value
        # at the sampling point
        #TODO sample_left_distance should actually never be negative
        sample_left_distance <- abs(list[[1]][i] - x_series[left_index])
        sample_pressure <- p_series[left_index] + (slope * sample_left_distance)

        nr_vector[i] <- sample_pressure
    }

    #last point gets presssure value of last point
    nr_vector[n_sampling_points] <- p_series[length(p_series)]

    list[[3]] <- nr_vector

    # print(x_series)
    # print(y_series)
    # print(p_series)
    # print(list[])
    # stopifnot(F)

    return(list)
}

#
# return the first 5 elements from each response
# NOTE: this is for testing purposes
#
# results are a three element list
# if the list cannot be formed,
# return NA
#
normalize_response_dummy <- function(response_matrix, response_index){
    #print(head(response_matrix[response_index,1][[1]][1:5]))
    list <- vector("list", 3)

    #print(response_index)
    #print(head(response_matrix))
    #print(response_matrix[response_index, ])

    for(k in 1:3){
        nr_vector <- response_matrix[response_index, k][[1]][1:5]

        # if part of the vector is NA, return NA
        #if(any(is.na(nr_vector))){
        #    return(NA)
        #}

        list[[k]] <- nr_vector
    }

    #print(head(response_matrix[response_index,]))
    #print(head(list))
    #stopifnot(F)

    # list has three elements
    # each element in the list is a vector
    print(list[])
    stopifnot(F)

    return(list)
}