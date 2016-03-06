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
    expected = np.array(compute_expected(bins, np.sum(observed)))
    
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
	# TODO ensure observed and expected frequencies are over 5%
	return []

# compute the observed distribution based on data array
# returns absolute frequencies for each bin
def compute_observed(data, bins):
	# TODO
	return []
	
# compute expected distribution based on normal distribution
# returns absolute frequencies for each bin
def compute_expected(bins, data_points):
	# TODO
	return []

if __name__ == '__main__':
    main();
