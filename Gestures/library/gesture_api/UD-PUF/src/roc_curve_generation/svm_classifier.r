#
# This class will attempt to use SMV
# classifier on raw data and generate plots accordingly
#

# libraries
library(e1071)

# import utility functions
source("utility.r")

# read the raw data file
raw_data <- read_raw_data()

# use svm to classify data
# combine user, device, challenge together
#TODO response should pick out actual properties of response from the data
data <- data.frame(
    "classification" = paste(raw_data$user, raw_data$device, raw_data$challenge, sep="_"),
    "response" = matrix(rnorm(1208), ncol=2),
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
for(i in 1:nrow(data)){
    # row names are classifications, value column holds the integer mapping
    data$classification[[i]] <- classification_map[data$classification[[i]], "value"]
}

head(data)
model <- svm(data$classification~., data=data, type="C", kernel="linear")

# print useful thing about smv classifier
#summary(model)

# plot the svm classifier
pdf("output/svm_classifier.pdf")
plot(model, data)
dev.off()

# display an ROC curve for the classifier
#TODO

#stopifnot(FALSE)