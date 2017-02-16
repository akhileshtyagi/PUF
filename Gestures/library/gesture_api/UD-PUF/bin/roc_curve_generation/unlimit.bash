#!/usr/bin/env bash

#
# this script will prevent
# Cstack_ overflow
#

# print Cstack limit
ulimit -s

#ulimit -s unlimited
ulimit -s 16384

# print Cstack limit
ulimit -s