__author__ = 'Ian Richardson - iantrich@gmail.com'

import os
import copy
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
    for i, cluster in enumerate(distribution):
        if cluster.get('lower') <= element < cluster.get('upper'):
            return i
    if element < distribution[0].get('lower'):
        return 0
    return len(distribution) - 1


def hash_function(current_window):
    hashcode = float(0.0)
    for i, touch in enumerate(current_window):
        if i == len(current_window) - 1:
            break
        hashcode = hashcode * 31 + touch[1]
    return hashcode


def match_sequence(hashcode_bin, current_window):
    if hashcode_bin is None:
        return -1
    current_sequence = []
    for touch in current_window:
        current_sequence.append(touch[1])
    for i, v in enumerate(hashcode_bin.get('chain')):
        for i2, v2 in enumerate(v.get('sequence')):
            if current_sequence[i2] != v2:
                break
            if i2 == len(v.get('sequence')) - 1:
                return i
    return -1


# Given the index of the node in the bin, increment the value of current_window[len(current_window) - 1]
def increment_probability(hashcode, hashcode_bin, link_index, current_window, table):
    hashcode_bin.get('chain')[link_index].get('probabilities')[current_window[-1][1]] += 1
    hashcode_bin.get('chain')[link_index]['total'] = hashcode_bin.get('chain')[link_index].get('total') + 1
    table[hashcode] = hashcode_bin
    return table


# Add link to bin with {'sequence': current_window[0...n-1] and current_window[n-1]: 1}
def add_link(hashcode, hashcode_bin, current_window, table, token):
    lst = [0] * token
    lst[current_window[-1][1]] = 1
    # Sequence shouldn't include timestamps
    sequence = []
    for touch in current_window:
        sequence.append(touch[1])
    hashcode_bin.get('chain').append({'sequence': sequence[:-1],
                                      'probabilities': lst,
                                      'total': 1})
    table[hashcode] = hashcode_bin
    return table


# Add a bin with {'chain': [{'sequence': current_window[0...n-1] and current_window[n-1]: 1}]}
def add_key(hashcode, current_window, table, token):
    lst = [0] * token
    lst[current_window[-1][1]] = 1
    # Sequence shouldn't include timestamps
    sequence = []
    for touch in current_window:
        sequence.append(touch[1])
    table[hashcode] = {'chain': [{'sequence': sequence[:-1],
                                  'probabilities': lst,
                                  'total': 1}]}
    return table


def touch_probability(hashcode_bin, current_window, link_index):
    return hashcode_bin.get('chain')[link_index].get('probabilities')[current_window[-1][1]]


def convert_table_to_probabilities(table):
    getcontext().prec = 4
    for key, val in table.items():
        for i, val2 in enumerate(val.get('chain')):
            for j, val3 in enumerate(val2.get('probabilities')):
                val2.get('probabilities')[j] = Decimal(val3) / Decimal(val2.get('total'))
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


def build_lookup(raw_data_file, table, distribution, window, threshold, token, match_user):
    normalized = []
    current_window = []
    getcontext().prec = 4
    probability = Decimal(0.0)

    with open(raw_data_file, 'rt') as csvfile:
        reader2 = csv.reader(csvfile)

        # Normalize data based on found distribution
        for row in reader2:
            normalized_item = normalize_raw_element(float(row[3]), distribution)
            normalized.append([row[0], int(normalized_item)])

        # Analyze touches
        for touch in normalized:
            if len(current_window) > 0 and long(touch[0]) - long(current_window[-1][0]) >= threshold:
                current_window = []
                current_window.append(touch)
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
                            probability += touch_probability(hashcode_bin, current_window, link_index)
                        else:
                            if link_index == -1:
                                # Sequence not found; Add a new link with the sequence and next touch
                                table = add_link(hashcode, hashcode_bin, current_window, table, token)
                            else:
                                # Sequence found, increment next touch
                                table = increment_probability(hashcode, hashcode_bin, link_index, current_window, table)
                    else:
                        if not match_user:
                            # Hashcode not found; Add a new bin with a link to that sequence and initial touch event
                            table = add_key(hashcode, current_window, table, token)
                    # Pop off the oldest touch
                    current_window.pop(0)
    if match_user:
        if len(normalized) > 0:
            return probability / len(normalized)
        else:
            return 0
    else:
        return table


def build_auth_table(raw_data_file, base_table, distribution, window, threshold, token, n):
    normalized = []
    current_window = []
    previous_window = []
    probabilities = []
    getcontext().prec = 4
    table = {}
    base_n = 0

    with open(raw_data_file, 'rt') as csvfile:
        reader2 = csv.reader(csvfile)

        # Normalize data based on found distribution
        for row in reader2:
            normalized_item = normalize_raw_element(float(row[3]), distribution)
            normalized.append([row[0], int(normalized_item)])

        # Analyze touches
        for i, touch in enumerate(normalized):
            # Do initial comparison of tables
            if i == n:
                ret = compare(base_table, table)
                probability = ret[0]
                base_n = ret[1]
                probabilities.append(probability)

            if len(current_window) > 0 and long(touch[0]) - long(current_window[-1][0]) >= threshold:
                current_window = []
                current_window.append(touch)
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
                        if link_index == -1:
                            # Sequence not found; Add a new link with the sequence and next touch
                            table = add_link(hashcode, hashcode_bin, current_window, table, token)
                        else:
                            # Sequence found, increment next touch
                            table = increment_probability(hashcode, hashcode_bin, link_index, current_window, table)
                    else:
                        # Hashcode not found; Add a new bin with a link to that sequence and initial touch event
                        table = add_key(hashcode, current_window, table, token)
                    # Pop off the oldest touch
                    previous_window = copy.deepcopy(current_window)
                    current_window.pop(0)
                    if i > n:
                        probability -= get_oldest(table, base_table, normalized, window, base_n, i)
                        table = remove_oldest(table, normalized, window, i)
                        probability += get_newest(table, base_table, normalized, window, base_n, i, previous_window)
                        previous_window = []
                        probabilities.append(probability)
                if len(previous_window) < window + 1 and i > n:
                        table = remove_oldest(table, normalized, window, i)

    return probabilities


def compare(base, auth):
    getcontext().prec = 4
    # Sum of probabilities
    s = 0
    # Number of sequences in base
    n = 0

    for key in base.keys():
        if key in auth:
            for chain in base.get(key).get('chain'):
                # Find if each sequence in base chain is in auth chain
                chain_index = find_sequence(chain.get('sequence'), auth.get(key).get('chain'))
                if chain_index != -1:
                    auth_seq_tot = auth.get(key).get('chain')[chain_index].get('total')
                    # Compare each touch probability
                    for i, prob in enumerate(chain.get('probabilities')):
                        auth_touch_prob = auth.get(key).get('chain')[chain_index].get('probabilities')[i]
                        auth_prob = Decimal(auth_touch_prob) / Decimal(auth_seq_tot)
                        # Difference of base to auth probabilities
                        s += prob - auth_prob
                else:
                    s += 1
        else:
            s += len(base.get(key).get('chain'))

        n += len(base.get(key).get('chain'))

    # Handle keys in auth that are not in base
    for key in auth.keys():
        if key in base:
            chain_index = find_sequence(chain.get('sequence'), base.get(key).get('chain'))
            if chain_index == -1:
                s -= 1
                n += 1
        else:
            n += len(auth.get(key).get('chain'))
            s -= len(auth.get(key).get('chain'))

    return [1 - abs(Decimal(s) / Decimal(n)), n]


def get_oldest(auth, base, norm, window, n, i):
    getcontext().prec = 4
    current = norm[i - 1001:i - 1001 + window + 1]
    if len(current) < window + 1:
        return 0
    auth_hashcode_bin = auth.get(hash_function(current))
    auth_link_index = match_sequence(auth_hashcode_bin, current)
    auth_prob = Decimal(auth_hashcode_bin.get('chain')[auth_link_index].get('probabilities')[current[-1][1]]) / Decimal(
        auth_hashcode_bin.get('chain')[auth_link_index]['total'])
    base_hashcode_bin = base.get(hash_function(current))
    base_link_index = match_sequence(base_hashcode_bin, current)
    if base_link_index != -1:
        base_prob = Decimal(
            base_hashcode_bin.get('chain')[base_link_index].get('probabilities')[current[-1][1]])
        return 1 - abs((base_prob - auth_prob) / Decimal(n))
    else:
        return 1 - abs((0 - auth_prob) / Decimal(n))


def get_newest(auth, base, norm, window, n, i, current):
    getcontext().prec = 4
    if len(current) < window + 1:
        return 0
    print i
    auth_hashcode_bin = auth.get(hash_function(current))
    auth_link_index = match_sequence(auth_hashcode_bin, current)
    auth_prob = Decimal(auth_hashcode_bin.get('chain')[auth_link_index].get('probabilities')[current[-1][1]]) / Decimal(
        auth_hashcode_bin.get('chain')[auth_link_index]['total'])
    base_hashcode_bin = base.get(hash_function(current))
    base_link_index = match_sequence(base_hashcode_bin, current)
    if base_link_index != -1:
        base_prob = Decimal(
            base_hashcode_bin.get('chain')[base_link_index].get('probabilities')[current[-1][1]])
        return 1 - abs((base_prob - auth_prob) / Decimal(n))
    else:
        return 1 - abs((0 - auth_prob) / Decimal(n))


def remove_oldest(table, norm, window, i):
    current = norm[i - 1001:i - 1001 + window + 1]
    hashcode = hash_function(current)
    hashcode_bin = table.get(hashcode)
    link_index = match_sequence(hashcode_bin, current)
    if link_index == -1:
        return table
    if hashcode_bin.get('chain')[link_index]['total'] == 1:
        # Remove link if no more touches present
        del hashcode_bin.get('chain')[link_index]
    else:
        hashcode_bin.get('chain')[link_index].get('probabilities')[current[-1][1]] -= 1
        hashcode_bin.get('chain')[link_index]['total'] = hashcode_bin.get('chain')[link_index].get('total') - 1

    table[hashcode] = hashcode_bin
    return table


def find_sequence(base_sequence, auth_chain):
    for i, link in enumerate(auth_chain):
        if base_sequence == link.get('sequence'):
            return i
    return -1