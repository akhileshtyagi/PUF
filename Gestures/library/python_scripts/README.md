#Scripts for Output Analysis

#### Originally created by: [Ryan Scheel](https://github.com/rascheel)
------------

#### Notes for usage
- Compile using [Python2](https://www.python.org/download/releases/2.7.6/)
- Required dependencies:
  - [pylab](http://wiki.scipy.org/PyLab)

---
###### __init__.py
Allows *.py files in this folder to be used in python scripts contained in other forlders.

###### averageHamming.py
Average hammering distance between the same route done by differnt users/devices.

###### consistencyHammingTest.py
Average hammering distance between runs of the same route, same device, same user

###### genFigures.py
calculates average pressure. draws a graph of the average pressure.

###### normalDistrib.py
I think this calculates the distribution and prints out the points in data sets which fall outside the distribution for a given person.

###### parseEmails.py
parse email files

###### poc.py
prints out all the pressure lists

###### responseGen.py
using "NormalizedStrat4" generates normalized responses for a (user, device, challenge) set. It seems to be working by finding the average pressure and std deviation for a given set. It then generates the response set by checking each point in the set to see whether if falls within some number of standard deviations. If it does, the pressure value is set to the average pressure.

###### test1.py
?

###### util.py
contains the ability to read in raw data. Contains the DataList class.

###### data_mining_scripts
Scripts which analyze the ability of machine learning tools to predict the responce to a challenge given a number of challenge response pairs. The ultimate goal of these scripts is to be able to say "X machine learning tool needs Y challenge-response pairs before it can predict the response to a challenge with Z% accuracy."

###### variability_analysis_scripts
These scripts will analyze the variablility of the mu, sigma model. This will be similar to the way in which the variability analysis was done for the raw data.
