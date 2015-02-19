__author__ = 'Ian Richardson - iantrich@gmail.com'

import find_best_fit
import user_auth
import user_class

# 1. Find best fit for user profile
# 2. Authenticate user based on profile
# 3. Match user to unknown profile


def print_options():
    print("Select an option\n"
          "1. Find the best omega, window and token size for a user compared to another sample\n"
          "2. Authenticate a user based on new raw data\n"
          "3. Find the likely profile matches for a set of raw data\n"
          "4. Exit\n")

print_options()
selection = raw_input("Choice: ")

while selection != '4':
    if selection == '1':
        find_best_fit.find_fit()
    elif selection == '2':
        user_auth.authenticate()
    elif selection == '3':
        user_class.match_class()
    print_options()
    selection = raw_input("Choice: ")

exit()