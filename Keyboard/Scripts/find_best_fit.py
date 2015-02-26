__author__ = 'Ian Richardson - iantrich@gmail.com'
# Use this script to also model the ideal token/window/threshold values
# It will be similar to the User Classification/Authentication scripts but will be looking
# for values that maximize reproducability and minimize false positives

# Iterate through token/window/threshold values for a given user profile and user dataset to find
# the one that has the highest probability of matching, yet minimizes all other possible user profile matches as much
# as possible

# Calculate probability P(m, L) for the given user dataset for each user/token/window combination

# Iterate through possible threshold values and find values that allow the wanted user profile to pass
# but other to not for each probability
# If none are found, allow one other user profile to pass, and so on until we have at least one successful set of values

# Depending on how well this data looks, might have to go back and access the token/window values for more precision

import myutilities
import build_lookup_table


def find_fit():
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

    myutilities.create_dir_path(
        myutilities.get_current_dir() + '/Data/User Profiles/' + base_file_desc.get('user') + '/' + base_file_desc.get(
            'device') + '/' + base_file_desc.get('date') + '/')

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

    base_lookup_tables = build_lookup_table.build_table(base_file_path)

    all_probabilities = []

    for i, lookup in enumerate(base_lookup_tables):
        base_table = lookup.get('table')
        base_distribution = lookup.get('distribution')
        base_window = lookup.get('window')
        base_token = lookup.get('token')
        base_threshold = lookup.get('threshold')

        probability = myutilities.build_lookup(raw_data_path,
                                               base_table,
                                               base_distribution,
                                               base_window,
                                               base_threshold,
                                               base_token,
                                               True)

        all_probabilities.append([probability, base_window, base_token, base_threshold])
        print "Find best fit progress: " + str(i / len(base_lookup_tables) * 100) + '%'

    # TODO Print the probabilities and such to a csv
    # Find which combination gives the largest probability
    best_fit = [0, 0, 0, 0]
    for item in all_probabilities:
        print item
        if item[0] > best_fit[0]:
            best_fit = item

    print("Best results of probability " + str(best_fit[0]) + "are with window size: " +
          str(best_fit[1]) + " and token size: " + str(best_fit[2])) + " and threshold: " + str(best_fit[3])
