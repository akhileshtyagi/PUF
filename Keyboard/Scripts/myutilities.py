__author__ = 'Ian Richardson - iantrich@gmail.com'

import os
import inspect
import csv
from decimal import *


def get_current_dir():
    return os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))


def list_dirs(path):
    dirs_list = []
    for dirs in os.listdir(path):
        dirs_list.append(dirs)
    return dirs_list


def list_files(path):
    files_list = []
    for files in os.listdir(path):
        files_list.append(files)
    return files_list


def print_selections(selections):
    for i, selection in enumerate(selections):
        print(str(i) + '. ' + str(selection) + '\n')


def grab_valid_input(options):
    choice = int(raw_input())
    while 0 > choice > len(options):
        choice = int(raw_input())
    return choice


def create_dir_path(path):
    # Make directories user/device/date/window and each token within that token
    if not os.path.exists(path):
        os.makedirs(path)


def normalize_raw_element(element, distribution):
    for cluster in distribution:
        if cluster.get('lower') <= element < cluster.get('upper'):
            return cluster.get('normalized')
    if element < distribution[0].get('lower'):
        return distribution[0].get('lower')
    return distribution[len(distribution) - 1].get('upper')


def hash_function(current_window):
    hashcode = float(0.0)
    for i, touch in enumerate(current_window):
        if i == len(current_window) - 1:
            break
        hashcode = hashcode * 31 + float(touch[1])
    return hashcode


def match_sequence(hashcode_bin, current_window):
    for i, v in enumerate(hashcode_bin.get('chain')):
        if current_window is v.get('sequence'):
            return i
    return -1


def match_next_touch(hashcode_bin, link_index, current_window):
    for i, v in enumerate(hashcode_bin.get('chain')[link_index].get('probabilities')):
        if v[0] == current_window[-1]:
            return i
    return -1


# Given the index of the node in the bin, increment the value of current_window[len(current_window) - 1]
# Assign this back to the node
# Assign this back to the bin
# Assign this back to the table
# Return the table
def increment_probability(hashcode, hashcode_bin, link_index, touch_index, table):
    # TODO Don't think increment is working properly
    hashcode_bin.get('chain')[link_index].get('probabilities')[touch_index][1] += 1
    hashcode_bin.get('chain')[link_index].update({'total': hashcode_bin[link_index].get('total') + 1})
    table[hashcode] = hashcode_bin
    return table


def add_touch(hashcode, hashcode_bin, link_index, current_window, table):
    hashcode_bin.get('chain')[link_index].get('probabilities').append(current_window[-1], 1)
    hashcode_bin.get('chain')[link_index].update({'total': hashcode_bin[link_index].get('total') + 1})
    table[hashcode] = hashcode_bin
    return table


# Add link to bin with {'sequence': current_window[0...n-1] and current_window[n-1]: 1}
# Assign this to the bin
# Assign this back to the table
# Return the table
def add_link(hashcode, hashcode_bin, current_window, table):
    hashcode_bin.get('chain').append({'sequence': current_window[:-1],
                                      'probabilities': [[current_window[-1], 1]],
                                      'total': 1})
    table[hashcode] = hashcode_bin
    return table


# Add a bin with {'chain': [{'sequence': current_window[0...n-1] and current_window[n-1]: 1}]}
# Assign this back to table
# Return the table
def add_key(hashcode, current_window, table):
    table[hashcode] = {'chain': [{'sequence': current_window[:-1],
                                  'probabilities': [[current_window[-1], 1]],
                                  'total': 1}]}
    return table


# TODO This as well as increment don't seem to be working properly
def touch_probability(hashcode_bin, current_window, link_index):
    touch_index = match_next_touch(hashcode_bin, link_index, current_window)
    # Next touch not found
    if touch_index == -1:
        return 0.01
    # Touch found
    else:
        return hashcode_bin.get('chain')[link_index].get('probabilities')[touch_index][1]


def convert_table_to_probabilities(table):
    getcontext().prec = 4
    for key, val in table.items():
        for i, val2 in enumerate(val.get('chain')):
            for j, val3 in enumerate(val2.get('probabilities')):
                val2.get('probabilities')[j][1] = Decimal(val3[1]) / Decimal(val2.get('total'))
    return table


def find_max_min(read):
    minimum = 1.0
    maximum = 0.0
    for r in read:
        if float(r[3]) > maximum:
            maximum = float(r[3])
        if float(r[3]) < minimum:
            minimum = float(r[3])
    return {'max': maximum, 'min': minimum}


# Generate a clustering distribution and return as tuple ranges
def cluster_algorithm(raw_data_file, token):
    with open(raw_data_file, 'rt') as csvfile:
        reader = csv.reader(csvfile)
        max_min = find_max_min(reader)

    variation = float((max_min.get('max') - max_min.get('min')) / token)

    i = 0
    distribution = []
    current = max_min.get('min')
    getcontext().prec = 4
    while i < token:
        distribution.append({'lower': current, 'upper': current + variation,
                             'normalized': Decimal(current + current + variation) / Decimal(2)})
        current += variation
        i += 1

    return distribution


def build_lookup(raw_data_file, table, distribution, window, threshold, match_user):
    normalized = []
    current_window = []
    getcontext().prec = 4
    probability = Decimal(1.0)

    with open(raw_data_file, 'rt') as csvfile:
        reader2 = csv.reader(csvfile)

        # Normalize data based on found distribution
        for row in reader2:
            normalized_item = normalize_raw_element(float(row[3]), distribution)
            normalized.append([row[0], normalized_item])

        # Analyze touches
        for touch in normalized:
            # Check if the touch is within a valid window based on the time threshold between touch events
            if len(current_window) > 0 and long(touch[0]) - long(current_window[-1][0]) > threshold:
                current_window = []
            else:
                current_window.append(touch)

            # Once the window size is filled and a next touch is captured add it to the Markov Model
            if len(current_window) == window + 1:
                # Hash the touch pressures
                hashcode = hash_function(current_window)
                if hashcode in table:
                    # Found the hashcode in our table
                    hashcode_bin = table.get(hashcode)
                    # Check if the exact sequence is in the found bin
                    link_index = match_sequence(hashcode_bin, current_window)
                    if match_user:
                        probability *= Decimal(touch_probability(hashcode_bin, current_window, link_index))
                    else:
                        if link_index == -1:
                            # Sequence not found; Add a new link with the sequence and next touch
                            table = add_link(hashcode, hashcode_bin, current_window, table)
                        else:
                            # Sequence found, check if next touch has been seen before
                            touch_index = match_next_touch(hashcode_bin, link_index, current_window)
                            if touch_index == -1:
                                # Next touch not seen before, add it
                                table = add_touch(hashcode, hashcode_bin, link_index, current_window, table)
                            else:
                                # Next touch seen before, increment it
                                table = increment_probability(hashcode, hashcode_bin, link_index, touch_index, table)
                else:
                    if match_user:
                        probability *= 0.01
                    else:
                        # Hashcode not found; Add a new bin with a link to that sequence and initial touch event
                        table = add_key(hashcode, current_window, table)
                # Pop off the oldest touch
                current_window.pop(0)
    if match_user:
        return probability
    else:
        return table