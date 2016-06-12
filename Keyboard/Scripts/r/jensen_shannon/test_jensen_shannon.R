#! /bin/Rscript

# the purpose of this is to display the Jensen Shannon divergence
# of the keyboard test data sets
#
#NOTE all data sets must be the same length

# information about this document can be found:
# http://stackoverflow.com/questions/11226627/jensen-shannon-divergence-in-r
# http://enterotype.embl.de/enterotypes.html
# http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0061217

#Uncomment next two lines if R packages are already installed
#install.packages("cluster")
#install.packages("clusterSim")
library(cluster)
library(clusterSim)

# contains dudi.pca
library(ade4)

# define Jensen shannon distribution function
dist.JSD <- function(inMatrix, pseudocount=0.000001, ...) {
  KLD <- function(x,y) sum(x *log(x/y))
  JSD<- function(x,y) sqrt(0.5 * KLD(x, (x+y)/2) + 0.5 * KLD(y, (x+y)/2))
  matrixColSize <- length(colnames(inMatrix))
  matrixRowSize <- length(rownames(inMatrix))
  colnames <- colnames(inMatrix)
  resultsMatrix <- matrix(0, matrixColSize, matrixColSize)
  
  inMatrix = apply(inMatrix,1:2,function(x) ifelse (x==0,pseudocount,x))
  
  for(i in 1:matrixColSize) {
    for(j in 1:matrixColSize) { 
      resultsMatrix[i,j]=JSD(as.vector(inMatrix[,i]),
                             as.vector(inMatrix[,j]))
    }
  }
  colnames -> colnames(resultsMatrix) -> rownames(resultsMatrix)
  as.dist(resultsMatrix)->resultsMatrix
  attr(resultsMatrix, "method") <- "dist"
  return(resultsMatrix) 
}

# set working directory to data directory
setwd("./data")

# read in the data
# each file in the data directory is one data set
#data_files = list.files(pattern="*.csv")

temp = list.files(pattern="*.csv")

# extract the data from the files in the temp list
data_files = lapply(setNames(temp, make.names(gsub("*.csv$", "", temp))), 
	function(i){
		read.csv(i, header=FALSE)
	})
	
# set working directory to ouputs directory
setwd("../output")

# build a data from from the pressure values from each file for each data file
#data <- data.frame()
data <- data.frame(matrix(NA, nrow = 4512, ncol = length(temp)))
for(i in 1:length(temp)){
	# acquire the pressure list from the data frame
	pressure_vector <- as.numeric(unlist(unname(data_files[i])[[1]][3]))
	
	# construct data frame for dist.JSD
	data[, paste('X', i, sep="")] <- pressure_vector
}

data.dist=dist.JSD(data)

# print out which data file is equal X1 .. X4
for(i in 1:length(temp)){
	# print a single data file and what it corresponds to
	column = paste('X', i, sep="")
	data_set = temp[i]
	
	description = paste(column, data_set, sep = ": ")
	
	# finally print the description
	print(description)
}

# print out the jensen shannon divergence
data.dist

#TODO get the clustering analysis to work
q()

########################

# this is the origional thing used to get data
# it returns a data frame
#data=read.table("MetaHIT_SangerSamples.genus.txt", header=T, row.names=1, dec=".", sep="\t")
#data=data[-1,]

########################
pam.clustering=function(x,k) { # x is a distance matrix and k the number of clusters
  require(cluster)
  cluster = as.vector(pam(as.dist(x), k, diss=TRUE)$clustering)
  return(cluster)
}

data.cluster=pam.clustering(data.dist, k=3)

require(clusterSim)
nclusters = index.G1(t(data), data.cluster, d = data.dist, centrotypes = "medoids")

nclusters=NULL

for (k in 1:20) { 
  if (k==1) {
    nclusters[k]=NA 
  } else {
    data.cluster_temp=pam.clustering(data.dist, k)
    nclusters[k]=index.G1(t(data),data.cluster_temp,  d = data.dist,
                          centrotypes = "medoids")
  }
}

plot(nclusters, type="h", xlab="k clusters", ylab="CH index",main="Optimal number of clusters")

obs.silhouette=mean(silhouette(data.cluster, data.dist)[,3])
cat(obs.silhouette) #0.1899451

#data=noise.removal(data, percent=0.01)

## plot 1
obs.pca=dudi.pca(data.frame(t(data)), scannf=F, nf=10)
obs.bet=bca(obs.pca, fac=as.factor(data.cluster), scannf=F, nf=k-1) 
dev.new()
s.class(obs.bet$ls, fac=as.factor(data.cluster), grid=F,sub="Between-class analysis")

#plot 2
obs.pcoa=dudi.pco(data.dist, scannf=F, nf=3)
dev.new()
s.class(obs.pcoa$li, fac=as.factor(data.cluster), grid=F,sub="Principal coordiante analysis")
