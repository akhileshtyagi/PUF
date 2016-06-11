#! /bin/Rscript

# read the data file
data = read.csv("tester_tim_device_tim.csv", stringsAsFactors=FALSE)
#data = read.csv("tester_ian_device_ian.csv", stringsAsFactors=FALSE)

# output the pressure data to the screen
# need to unlist it before it can be converted to a numeric vector
# i need a numeric vector so it can be passed into the sharpie test
data_words = as.numeric(unlist(data[3]))

# get the first 5000 pressure values from data_words
# sharpie test has a limit of 5000
#pressure <- data_words[5001:10000]
pressure <- data_words[10001:15000]

pressure
#data[3]


## Generate two data sets
## First Normal
words1 = rnorm(5000);

## Have a look at the densities
plot(density(words1));
plot(density(pressure));

## Perform the test
shapiro.test(words1);
shapiro.test(pressure);

## Plot using a qqplot
qqnorm(words1);qqline(words1, col = 2)
qqnorm(pressure);qqline(pressure, col = 2)
