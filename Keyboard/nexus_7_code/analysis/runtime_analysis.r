library(ggplot2)

# create a data frame with the given compare values
speed_0 <- read.table("../speed_test_results/nexus_speed_test_1.txt",
    header=TRUE)
speed_1 <- read.table("../speed_test_results/nexus_speed_test_2.txt",
    header=TRUE)
speed_2 <- read.table("../speed_test_results/nexus_speed_test_3.txt",
    header=TRUE)

# average the time values data sets
speed <- as.data.frame(matrix(0,nrow=nrow(speed_0)))
speed$base_size <- speed_0$base_size
speed$auth_size <- speed_0$auth_size
speed$time_taken <- (speed_0$time_taken + speed_1$time_taken + speed_2$time_taken) / 3

# combine base size and auth size into 1 values
speed$total_size <- speed$base_size+speed$auth_size

#
# runtime on nexus 7
#
pdf("nexus_7_runtimes.pdf", width = "6", height = "6")

qplot(speed$total_size, speed$time_taken,
    xlab="Number of Touches", ylab="Time (ms)",
    main="Authentication Time (ms) vs. Number of Touches")
    #xlim=c(0,1), ylim=c(0,1))

dev.off()

speed

# display any warnings
warnings()
