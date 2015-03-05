__author__ = 'iantrich'

# Test the lookup table build process and verify that probabilities are correct

import myutilities
import csv

# Raw Data to generate Lookup Tables From
# Raw Data directory
raw_data_dir = myutilities.get_current_dir() + '/Data/Raw Data/'

# List available users with raw data sets
available_users = myutilities.list_dirs(raw_data_dir)
myutilities.print_selections(available_users)
print('Select a user as base:')
# Grab selected user
selected_base_user = myutilities.grab_valid_input(available_users)

# List available devices for the selected user
available_base_devices = myutilities.list_dirs(raw_data_dir + available_users[selected_base_user])
myutilities.print_selections(available_base_devices)
print('Select a device as base:')
# Grab selected device
selected_base_device = myutilities.grab_valid_input(available_base_devices)

# List available raw data sets for the selected user and device
available_base_sets = myutilities.list_files(
    raw_data_dir + available_users[selected_base_user] + '/' + available_base_devices[selected_base_device])
myutilities.print_selections(available_base_sets)
print('Select a data set as base:')
# Grab selected data set
selected_base_set = myutilities.grab_valid_input(available_base_sets)

base_file_path = raw_data_dir + available_users[selected_base_user] + '/' + available_base_devices[
    selected_base_device] + '/' + available_base_sets[selected_base_set]

table = {}

token = 10
window = 5
threshold = 5000

# Use a clustering algorithm to find the distribution of touches
distribution = myutilities.cluster_algorithm(base_file_path, token)

# Build raw lookup table
table = myutilities.build_lookup(base_file_path, table, distribution, window, threshold, token, False)

with open('test_raw_lookup.csv', 'w') as csvfile:
    w = csv.writer(csvfile)

    for k, v in table.items():
        for i, e in enumerate(v.get('chain')):
            for k2, v2 in e.items():
                w.writerow([k, i, k2, v2])

# Go through table and convert values to probabilities
table = myutilities.convert_table_to_probabilities(table)

with open('test_prob_lookup.csv', 'w') as csvfile2:
    w = csv.writer(csvfile2)

    for k, v in table.items():
        for i, e in enumerate(v.get('chain')):
            for k2, v2 in e.items():
                w.writerow([k, i, k2, v2])
