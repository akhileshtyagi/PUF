__author__ = 'Ian Richardson'

# Build training table
# Convert to probabilities
# Build a auth table of size n
# Find the difference between the two tables
# Be sure to take into account any sequence/touches not seen in the auth table
# Will probably want to remove a touch when seen from the training table to make it easiest
import myutilities
import csv
from decimal import *

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

base_file_name = available_base_sets[selected_base_set].split("_")

base_file_desc = {
    'date': base_file_name[0],
    'user': base_file_name[1],
    'device': base_file_name[2]
}

output_path = myutilities.get_current_dir() + '/Data/User Profiles/' + base_file_desc.get(
    'user') + '/' + base_file_desc.get(
    'device') + '/' + base_file_desc.get('date') + '/'

myutilities.create_dir_path(output_path)

myutilities.print_selections(available_users)
print('Select the raw data user:\n')
# Grab selected user
selected_raw_user = myutilities.grab_valid_input(available_users)

# List available devices for the selected user
available_raw_devices = myutilities.list_dirs(raw_data_dir + available_users[selected_raw_user])
myutilities.print_selections(available_raw_devices)
print('Select the raw data device:\n')
# Grab selected device
selected_raw_device = myutilities.grab_valid_input(available_raw_devices)

# List available raw data sets for the selected user and device
available_raw_sets = myutilities.list_files(
    raw_data_dir + available_users[selected_raw_user] + '/' + available_raw_devices[selected_raw_device])
myutilities.print_selections(available_raw_sets)
print('Select the raw data set:\n')
# Grab selected data set
selected_raw_set = myutilities.grab_valid_input(available_raw_sets)

raw_data_path = raw_data_dir + available_users[selected_raw_user] + '/' + available_raw_devices[
    selected_raw_device] + '/' + available_raw_sets[selected_raw_set]


base_table = {}
probabilities = []
max_probs = []

n = 1000

window_sizes = [3, 4, 5, 6, 7, 8, 9, 10]
token_sizes = [5, 6, 7, 8, 9, 10, 20, 30, 40, 50]
time_thresholds = [1000, 1500, 2000]


# Generate lookup table for each combination of window size and token size
for win_i, window in enumerate(window_sizes):
    for tok_i, token in enumerate(token_sizes):
        for time_i, threshold in enumerate(time_thresholds):
            print 'Window: ' + str(window_sizes[win_i]) + ', Token: ' + str(token_sizes[tok_i]) + ', Threshold: ' + str(time_thresholds[time_i])

            # Use a clustering algorithm to find the distribution of touches
            distribution = myutilities.cluster_algorithm(base_file_path, token)

            # Build raw lookup table
            base_table = {}
            base_table = myutilities.build_lookup(base_file_path, base_table, distribution, window, threshold, token,
                                                  False)

            # Get probabilities
            base_table = myutilities.convert_table_to_probabilities(base_table)

            # Get distance between base and auth models
            probability = myutilities.build_auth_table(raw_data_path, base_table, distribution, window, threshold, token, n)
            probabilities.append(probability)
            m = Decimal(0.0)
            for prob in probability:
                if prob > m:
                    m = prob

            print 'Max probability: ' + str(m[0] * 100) + '%'
            max_probs.append(m)

print max_probs