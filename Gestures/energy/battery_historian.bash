#! /bin/bash


# start a chrome browser to access Battery Historian
chromium http://localhost:9999

# start battery historian
systemctl start docker
sudo docker run -p 9999:9999 gcr.io/android-battery-historian/stable:3.0 --port 9999
