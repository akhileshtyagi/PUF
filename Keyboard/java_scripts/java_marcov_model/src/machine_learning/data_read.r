#
# goal: read in a data
#

##
# read raw data from folder
# one data set per file
##
read_raw_data <- function(folder_name){
    #data_list <- list()
    data_list <- c()

    for(file_name in list.files(folder_name)){
        print(file_name)
        data <- read.csv(paste0(folder_name, "/", file_name),
            header=FALSE, col.names=c("time", "key", "pressure"))

        print(data)
        append(data_list, data);
    }

    return(data_list)
}

##
# read data about markov properties (chain)
##
read_chain_data <- function(file_name){
    data <- read.csv(paste0(folder_name, "/", file_name),
        header=FALSE, col.names=c("time", "key", "pressure"))
}

##
# test functions
##
test_0 <- function(){
    data <- read_raw_data("../../data_sets")
    print(data)
}

data <- test_0()