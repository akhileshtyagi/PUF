#!/usr/bin/env python2
# -*- coding: utf-8 -*-
#
#  chi_square_pressure_distribution.py
#  
#  Copyright 2016 Unknown <element@chai_tea>
#  
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA 02110-1301, USA.
#  
#  
import numpy as np
import scipy.stats.mstats as stats
import scipy.stats
import csv

def main():
    print "Beginning Chi Square Test"
    
    data_file_name = "Data/final_raw_data/tester_tim_device_tim.csv"
    data = read_pressure_data(data_file_name)
    
    # create bins to fit items into
    bins = compute_bins(data)
    
    # find observed frequencies
    # frequency is computed for first item in hi
    observed = np.array(compute_observed(data, bins))
    
    # find expected frequencies
    # frequency is based on normal distribution
    expected = np.array(compute_expected(bins, data))
    
    # TODO obviously in error
    print "Observed"
    print observed
    
    print "Expected"
    print expected
    
    # run the chisquare test
    print stats.chisquare(observed, expected)
    
    return 0

# read pressure data from file
def read_pressure_data(data_file_name):
	a = []
	
	with open(data_file_name, 'r') as csvfile:
		reader = csv.reader(csvfile)
		
		rows = 0
		for row in reader:
			#print row[2]
			# append the third element in the row to the array
			a.append(float(row[2]))
			rows += 1
			
	return a

# create bins based on observed data
# ensure that no frequencies are less than 5% for any bin
def compute_bins(data):
	# begin with this many bins.
	# decrease number of bins until every bin is atleast .05 * data_length
	num_bins = 15
	
	# TODO compute bins in a smarter way
	#bins = compute_uniform_bins(data, num_bins)
	bins = compute_mu_bins(data, num_bins)
	
	# while any bin is less than 5 percent
	while less_than_five(bins, data):
		num_bins -= 1
		
		if num_bins == 0:
			quit()
		
		bins = compute_mu_bins(data, num_bins)
	
	return bins
	
# computes evenly distributed bins based on number of bins
# returns the starting value and ending value of all bins
# this computes bins in a uniform way
def compute_uniform_bins(data, num_bins):
	data_max = max(data)
	data_min = min(data)
	bin_size = (data_max - data_min) / num_bins
	bins = []
	
	for i in range(num_bins):
		start_of_bin = data_min + (bin_size * i)
		end_of_bin = data_min + (bin_size * (i + 1))
		bins.append([start_of_bin, end_of_bin])
	
	return bins
	
# computes bins centered around mu for the data set
def compute_mu_bins(data, num_bins):
	data_mu = compute_mu(data)
	
	above_mu = max(data) - data_mu
	below_mu = data_mu - min(data)
	
	dist_from_mu = min([above_mu, below_mu])
	
	bin_size = dist_from_mu / num_bins
	
	data_max = data_mu + dist_from_mu
	data_min = data_mu - dist_from_mu
	bins = []
	
	for i in range(num_bins):
		start_of_bin = data_min + (bin_size * i)
		end_of_bin = data_min + (bin_size * (i + 1))
		bins.append([start_of_bin, end_of_bin])
	
	return bins
	
# return true if any bin is less than 5 percent of total data elements
def less_than_five(bins, data_elements):
	# TODO this never seems to get over .05. perhaps because of 0's ???
	#return False
	
	frequency_observed = compute_observed(data_elements, bins)
	frequency_expected = compute_expected(bins, data_elements)
	
	for i in range(len(frequency_observed)):
		if (frequency_observed[i] / len(data_elements)) < .05:
			return True
	
	for i in range(len(frequency_expected)):
		if (frequency_expected[i] / len(data_elements)) < .05:
			return True
	
	return False

# compute the observed distribution based on data array
# returns absolute frequencies for each bin
def compute_observed(data, bins):
	# create an array initialized to 0 with len(bins) elements
	frequency = [0] * len(bins)
	thrown_out = 0
	
	# for each data item, increment the bin it falls within
	for i in range(len(data)):
		bin_index = compute_bin_index(data[i], bins)
		
		if bin_index == -1:
			thrown_out += 1
		else:
			frequency[bin_index] += 1
		
	# compensate for any pressure values which were thrown out
	multiplier = 1 - (thrown_out / len(data))
	
	return [x * multiplier for x in frequency]
	
# determine the bin index an item falls within
# returns bin index
# returns -1 if item does not fall in any bin
def compute_bin_index(item, bins):
	# go though each bin, determine if item falls within bin
	for i in range(len(bins)):
		if ((item >= bins[i][0]) and (item < bins[i][1])):
			return i
			
	# for the last element, check if it is equal to the end range
	if (item == bins[len(bins)-1][1]):
		return len(bins)-1
			
	# should not happen, indicates error
	# print "error"
	return -1
	
# compute expected distribution based on normal distribution
# returns absolute frequencies for each bin
def compute_expected(bins, data_points):
	frequency = [0] * len(bins)
	mu = compute_mu(data_points)
	sigma = compute_sigma(data_points)
	
	# create normal distribution with mu, sigma
	normal_dist = scipy.stats.norm(mu, sigma)
	
	for i in range(len(bins)):
		# take the cdf of max of bin minus min of bin
		# this gives the probability of fallling inbetween
		frequency[i] = normal_dist.cdf(bins[i][1]) - normal_dist.cdf(bins[i][0])
	
	# NOTE at this point, frequency should hold the probability for each bin
	# multiply each element by the number of data points and return
	return [x * len(data_points) for x in frequency]

# compute the average of data
def compute_mu(data):
	s = 0
	for i in range(len(data)):
		s += data[i]
		
	return s/len(data)
	
# compute the std deviation of a data set
def compute_sigma(data):
	a = np.array(data)
	return np.std(a)

if __name__ == '__main__':
    main();
