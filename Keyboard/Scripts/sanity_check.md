#Sanity Check
This file goes though some of the files line by line. Manually recording what they do. This sort of check will provide some sort of reasonable assurance that these files are doing what they should do.

Not every line is traced because some of them are self-explainatory.

##myutilities.py
###normalize_raw_element
| line | function |
|:----|:--------|
|   if keycode < 0:| makes the keycode 0 if it is less than 0, this is due to an issue with the key ranges |
|        keycode = 0 | |
|    if pressure < keycode_dist[keycode].get('lower') or pressure > keycode_dist [keycode].get('upper'): |return -1 if the keycode is not within the 2 sigma of the normal distribution for that keycode |
|        return -1 | |
|    pressure = keycode_dist[keycode].get('mean') |gets the average pressue for all points with this keycode |
|    for i, cluster in enumerate(distribution): |for all clusters in the distribution, if the pressue of this point is within the lower, upper bounds (2 sigma) for that keycode, return i. i here is the index of the distribution in which i falls. |
|        if cluster.get('lower') <= pressure < cluster.get('upper'): | |
|            return i | |
|        if pressure < distribution[0].get('lower'): | |
|            return -1 | |
|    return len(distribution) - 1 | |

###match_sequence
TODO
| line | function |
|:-----|:---------|

###touch_probability
| line | function |
|:-----|:---------|
|def touch_probability(hashcode_bin, current_window, link_index): |returns the probability of a thouch occurring after current_window |
    return hashcode_bin.get('chain')[link_index].get('probabilities')[current_window[-1][1]] |the touch whoes probability we are getting is the last one in the window |

###cluster_algorithm
| line | function |
|:-----|:---------|
|def cluster_algorithm(raw_data_file, token): | First, a number of tokens evenly spaced over the entire distribution. These tokens are limited by the min, max of all touch pressures. It also returns the min, max, and average of all touch pressures for each keycode. |
|    with open(raw_data_file, 'rt') as csvfile:
        reader = csv.reader(csvfile)
        key_dist = keycode_distribution(reader) |key_dist now holds the distribution (min,max,average) for each key code |

|    with open(raw_data_file, 'rt') as csvfile:
        reader2 = csv.reader(csvfile)
        max_min = find_max_min(reader2) |min_max now contains the minimum and maximum for the entire distribution. |

 |   variation = float((max_min.get('max') - max_min.get('min')) / token) |variation now contains the size of a single token |

|    i = 0
    distribution = []
    current = max_min.get('min')
    getcontext().prec = PRECISION
    while i < token:
        distribution.append({'lower': current, 'upper': current + variation,
                             'normalized': Decimal(current + current + variation) / Decimal(2)})
        current += variation
        i += 1 |This loop inserts the token ranges into the distribution |

|    return [distribution, key_dist] |return the distribution which now contains the token ranges |

##model_compare.py
