__author__ = 'Ian'

import myutilities
import os
import csv

profiles = myutilities.get_current_dir() + '/Data/User Profiles/'

m = ['2000', '4000', '6000', '8000']
n = ['50', '75', '100']
window_sizes = [3, 4, 5, 6]
token_sizes = [5, 6, 7, 8, 9, 10, 20, 30, 40, 50]
time_thresholds = [1000, 1500, 2000]

for root, dirs, files in os.walk(profiles):
    for name in dirs:
        print name
        for root2, dirs2, files2 in os.walk(os.path.join(root, name)):
            for name2 in dirs2:
                print name2
                for root3, dirs3, files3 in os.walk(os.path.join(root2, name2)):
                    names = []
                    combined = [[] for i in range(len(window_sizes) * len(token_sizes) * len(time_thresholds))]

                    for name3 in files3:
                        # Iterate through each file and put each into a list within a list
                        with open(os.path.join(os.path.join(root2, name2), name3), 'rt') as csvfile:
                            # TODO Need to order these properly
                            reader = csv.reader(csvfile)
                            names.append(name3)
                            for i, row in enumerate(reader):
                                combined[i].append(row[0])
                                combined[i].append(row[1])
                    # Then print them all to a csv with the names on top and sizes on the left
                    with open(os.path.join(root2, name2) + 'combined' + '.csv', 'wb') as csvfile:
                        writer = csv.writer(csvfile)

                        fileNames = ['W', 'K', 'T']
                        for fname in names:
                            fileNames.append(fname)
                            fileNames.append('')

                        writer.writerow(fileNames)

                        maxMinLine = [''] * 3
                        for i in range(len(files3)):
                            maxMinLine.append('Max')
                            maxMinLine.append('Min')

                        writer.writerow(maxMinLine)

                        w = 0
                        k = 0
                        t = 0
                        for item in combined:
                            line = [str(window_sizes[w]), str(token_sizes[k]), str(time_thresholds[t])] + item
                            writer.writerow(line)
                            t += 1
                            if t == len(time_thresholds):
                                k += 1
                                t = 0
                            if k == len(token_sizes):
                                w += 1
                                k = 0
                                t = 0
