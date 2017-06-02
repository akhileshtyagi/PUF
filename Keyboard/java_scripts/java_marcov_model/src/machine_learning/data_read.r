library(plyr)

#
# goal: read in a data
#
MAX_DATA_POINT <- 1000 

##
# read raw data from folder
# one data set per file
#
# each row in the matrix returned is one file
##
read_raw_data <- function(folder_name){
	#data_list <- list()
	data_list <- c()
	#data_list <- data.frame()

	rows <- 0;
	for(file_name in list.files(folder_name)){
		print(file_name)
		data <- read.csv(paste0(folder_name, "/", file_name),
				 header=FALSE, col.names=c("time", "key", "pressure"))

		# hold the file name with the data
		data[,"class"] <- file_name

		#print(head(data))
		#data_list <- append(data_list, data);
		#data_list <- list(data_list, data);
		#data_list[file_name,] <- data;

		# limit the maximum number of data points per data file
		data <- data[1:min(nrow(data), MAX_DATA_POINT),]

		# append data to the list
		data_list <- c(data_list, c(data));

		rows <- rows + 1
	}

	# each row in matrix is the data from a file
	data_list <- matrix(data_list, nrow=rows, byrow=TRUE)

	# remove time from returned data
	return(data_list[,c(2,3,4)])
}

##
# expand the raw data 
# 
# each row in the returned data frame is
# [key, pressure, classification]
##
expand_raw_data <- function(data){
	data <- data.frame(
			   "classification" = unlist(data[,ncol(data)]),
			   #"response" = lapply(data[,c(1:(ncol(data)-1))], unlist))
			   "key" = unlist(data[,1]),
			   "pressure" = unlist(data[,2]))

	return(data)


	class_list <- c()
	for(i in 1:nrow(data)){
		class_list <- c(class_list, data[i,ncol(data)][[1]][[1]])
	}

	data <- data.frame(
			   "classification" = class_list,
			   "response" = data[,c(1:(ncol(data)-1))])

}

##
# read data about markov properties (chain)
#
# each row in the matrix returned is one file
##
read_chain_data <- function(folder_name){
	#data_list <- list()
	data_list <- c()
	#data_list <- data.frame()

	rows <- 0;
	for(file_name in list.files(folder_name)){
		print(file_name)
		data <- read.csv(paste0(folder_name, "/", file_name), header=T)

		# hold the file name with the data
		data[,"class"] <- file_name

		#print(head(data))
		#data_list <- append(data_list, data);
		#data_list <- list(data_list, data);
		#data_list[file_name,] <- data;
		data_list <- c(data_list, c(data));

		rows <- rows + 1
	}

	# each row in matrix is the data from a file
	data_list <- matrix(data_list, nrow=rows, byrow=TRUE)

	return(data_list)
}

read_chain <- function(folder_name){
	data_list <- c()

	all_data_frame = data.frame()

	rows <- 0;
	for(file_name in list.files(folder_name)){
		rows <- rows + 1
		
		print(file_name)
		data <- read.csv(paste0(folder_name, "/", file_name), header=T)

		#TODO get the basename of the file (remove everything after .csv)
		base_name <- file_name
		
		# convert the classification into one large column
		data <-data.frame(lapply(unlist(data[,1:(ncol(data)-1)]), function(x) t(data.frame(x))))

		# hold the file name with the data
		data[rows,"classification"] <- base_name

		#TODO how to make sure that a given [Ngram, key, probability, weight]
		#TODO line up to be the same ngram, probability, key for differnet files
		#TODO it might be easier to do this in java!!!
		#TODO this would require that I output all files with the same n-grams
		#TODO any n-gram which does not occur for a particular chain gets [ngram, -1, 0, 0]

		# add the data to the all_data_frame
		#all_data_frame <- rbind.fill(all_data_frame, data)
		all_data_frame <- rbind(all_data_frame, data)

		print(head(all_data_frame))
		stopifnot(F)

		#print(head(data))
		#data_list <- append(data_list, data);
		#data_list <- list(data_list, data);
		#data_list[file_name,] <- data;
		
		#data_list <- c(data_list, c(data));
	}

	print(head(all_data_frame))
	stopifnot(F)

	# each row in matrix is the data from a file
	data_list <- matrix(data_list, nrow=rows, byrow=TRUE)

	return(data_list)

	####	
	
	# read the raw data
	data <- read_chain_data(folder_name)

	# create a list of all windows,
	# these become the features

	#TODO		
	#print(unlist(data[,ncol(data)]))

	#TODO this kindof did what I want, It make 400835 columns	
	# http://stackoverflow.com/questions/13567754/converting-a-list-to-one-row-data-frame
	data <-data.frame(lapply(unlist(data[,1:(ncol(data)-1)]), function(x) t(data.frame(x))))
	
	# create the data frame of all users
	#data <- data.frame("classification" = unlist(data[,ncol(data)]), unlist(data[,1:(ncol(data)-1)]))

	#data <- data.frame("classification" = unlist(data[,ncol(data)]), data[,1:(ncol(data)-1)])

#	data <- data.frame(
#			   "classification" = unlist(data[,ncol(data)]),
#			   "ngram_0" = unlist(data[,1]),
#			   "ngram_1" = unlist(data[,2]),
#			   "key" = unlist(data[,3]),
#			   "p_key" = unlist(data[,4]),
#			   "ngram_weight" = unlist(data[,5]))

	#TODO!!

	return(data)
}

#TODO test methods
data <- read_chain("chain_data/2_2_1000_800_800")
print(nrow(data))
print(ncol(data))
print(head(data))

##
# convert raw_data into features for classification
#
# for raw_data: I could do something as simple as the average pressure at each key becomes a feature
# each key, k, is a feature having value "average probability on key k"
#
# input:	is a matrix( list("key"), list("pressure"), list("classification") )
#
# output:	does not change list("classification")
##
#TODO ensure the mean is correct
convert_feature_raw_data <- function(data){
	#print("INPUT:")
	#print(head(data))

	# obtain a unique key list for each of the possible keys

	unique_key_list <- c()
	for(i in 1:nrow(data)){
		key_list = c(unique_key_list, unlist(data[i,1]))
		unique_key_list <- unique(key_list)
	}

	# for all rows in the matrix
	for(i in 1:nrow(data)){
		#TODO this does not work!!!!!
		#key_list = lapply(data[i,1], as.numeric)
		#key_list = unlist(data[i,1])
		pressure_list = unlist(data[i,2])

		# get all the unique keys for this row of the input
		#unique_key_list <- unique(key_list)
		#unique_key_list <- key_list[which(!duplicated(key_list))]

		mean_list <- c()

		# compute the average pressure for each unique key
		for(j in 1:length(unique_key_list)){
			# determine on which rows this key can be found
			indices <- which(key_list %in% c(unique_key_list[[j]]))
			#indices <- key_list == unique_key_list[[j]] 

			#TODO evaluate whether this makes sense
			# if mean is NA, replace with 0 
			mean_0 <- mean(pressure_list[indices])
			mean_0 <- ifelse(is.na(mean_0), 0, mean_0)

			mean_list <- c(mean_list, mean_0)
		}

		#print(mean_list)

		# assign unique_key_list and mean_list back to their respective row in the matrix
		data[i,1] <- list(unique_key_list)
		data[i,2] <- list(mean_list)
	}

	#print(unique_key_list)
	#print(class(unique_key_list))

	#print("OUTPUT:")
	#print(head(data))

	#print(class(key_list))

	return(data)
}

##
# convert chain_data into features for classification
#
# for chain_data each, window w || successor s, is a feature
# having value equal to "probability of s after w"
##
convert_feature_chain_data <- function(data){
	#print("INPUT:")
	#print(data)

	#TODO this needs to be modified every time I change the input file type...
	#TODO is there any way I could make this agnostic to the input file type?
	#TODO
	data <- data.frame(
			   "classification" = unlist(data[,ncol(data)]),
			   "ngram_0" = unlist(data[,1]),
			   "ngram_1" = unlist(data[,2]),
			   "key" = unlist(data[,3]),
			   "p_key" = unlist(data[,4]),
			   "ngram_weight" = unlist(data[,5]))

	#print("OUTPUT:")
	#print(data)

	return(data)
}

##
# format the data
# such that it is dimensioned
# (x = classification, y = feature list, z = feature values).
#
# input matrix is ([file size], 4)
# where column [,4] is the classification of the data
#
# output is a data.frame() where each row is 1 file
# each column contains a list with one feature
##
#TODO watch out for (factor vs numeric differences)
format_data <- function(data){
	# redimensions:
	# col 4 => x
	# col (1,2,3) => y
	#
	# extract the classifications
	class_list <- c()
	for(i in 1:nrow(data)){
		class_list <- c(class_list, data[i,ncol(data)][[1]][[1]])
	}

	data <- data.frame(
			   "classification" = class_list,
			   "response" = I(data[,c(1:(ncol(data)-1))]))

	# # convert data to a data.frame with
	# data <- data.frame(
	#     I(data[,c(1:(ncol(data)-1))]),
	#     row.names=class_list)

	# unlist the columns
	# make them vectors
	# value_list <- list
	# for(row in 1:nrow(data)){
	#     for(col in 1:ncol(data)){
	#         #data[row, col] <- c(unlist(data[row, col]))
	#         value_list <- list(value_list, c(unlist(data[row, col])))
	#         #print(length(c(unlist(data[row, col]))))
	#     }
	# }

	#data <- as.data.frame(I(as.matrix(value_list, dimnames=list(class_list, c()))),
	#    row.names=class_list)

	#head(head(print(value_list)))

	# convert data to a data.frame with
	#data <- data.frame(value_list, row.names=class_list)

	#unlist(data["t_yunxi_d_n80.csv","X4"])

	return(data)
}

##
# test functions
##
test_print <- function(data){
	print(nrow(data))
	print(length(data))
	print(class(data))
	print(row.names(data))
	print(class(row.names(data)))
	print(as.factor(row.names(data)))
	print(is.atomic(as.factor(row.names(data))))
	print(class(data[1,1]))
	print(ncol(data))
	print(is.atomic(data$response.1))
	print(is.atomic(data$response.2))
	print(class(data$response.1))
	print(class(data$response.2))
	print(head(data))
}

test_0 <- function(){
	data <- read_raw_data("../../data_sets")
	test_print(data)
}

test_1 <- function(){
	data <- read_chain_data("chain_data/1_2_1000_100000_100000")
	test_print(data)
}

test_2 <- function(){
	#data <- format_data(data <- read_raw_data("../../data_sets"))
	data <- format_data(read_chain_data("chain_data/1_2_1000_100000_100000"))
	test_print(data)
	return(data)
}

test_3 <- function(){
	data <- format_data(read_raw_data("../../data_sets"))
	test_print(data)
}

#test_3()

#test_1()
#data <- test_2()
#print(is.atomic(list(1,2,3)))
#print(is.atomic(c(1,2,3)))

#head(data["t_yunxi_d_n80.csv","X1"][[1]])

#TODO figure out how to get data into the correct form for the classifier

#TODO what am I even trying to classify?
# raw_data: given a chunck of raw data, did it come from a given user or not
# chain_data: given a set of (window, successor, p(successor))
#   determine if  it came from a given user or not

#TODO I could try clustering.... this might make more sense4
