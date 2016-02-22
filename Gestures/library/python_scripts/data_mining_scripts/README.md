#Data Mining and Machine Learning Analysis
The purpose of these scripts is to attempt to use machine learning algorithems to predict the next output given a few sets of input, output

###Tools Utilized
summary of available tools 	http://www.xavierdupre.fr/blog/2013-09-15_nojs.html
scikit-learn				http://scikit-learn.org
							https://github.com/jakevdp/sklearn_scipy2013

###Notes:
classification = separating apples from oranges
regression = fitting a continuious model to data

plt.scatter(x,y)
from mpl_toolkits.mplot3d import Axes3D

define estimator
call fit
call predict

fit("2-d array", "1-d array")

[n\_samples x n\_features]
samples are the objects and features are the number of features each object has.
For example, say your trying to classify flowers by pedels. Flowers would be the samples, and pedels would be features.

fit(data, target/label)

data is the n-samples x n-features array.
target is the true classification of each sample

machine learning is a decision rule with tuneable parameters.

x is data, y is target

knn = known nearest neighbor. Looks at the closest 

unsupervised learning = you have x without y
reproducing future data based on past input would be an unsupervised learning problem.

sklearn.cross_validation import train_test_split could be useful for 

---
###Script Files Detail
Describes the purpose of each file. Also describes the input, output used; how to interpret the output is also described.

######challenge_response.py
Defined in this file is a class which contains information about the challenge response pairs. This class knows how to read the data sets, and put them into a usable format.
More simply put this class will contain the raw data, and the mu-sigma model built from the raw data. A set of these will be given to the data mining class for testing.

######main.py
Provides the ability to:
1) run all machine learning tests
2) run each of the machine learning tests individually

######trainer.py
Handles the training of the machine learning models. Offers methods to run different types of machine learning tests, and returns their results.
