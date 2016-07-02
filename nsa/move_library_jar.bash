#! /bin/bash

# the purpose of this script is to copy the interface library
# to thel libs folders of the projects

# KeyboardAuthenticationService
cp KeyboardAuthenticationInterface/out/artifacts/KeyboardAuthenticationInterface/KeyboardAuthenticationInterface.jar KeyboardAuthenticationService/app/libs/KeyboardAuthenticationInterface.jar

# KeyboardAuthenticationTest
cp KeyboardAuthenticationInterface/out/artifacts/KeyboardAuthenticationInterface/KeyboardAuthenticationInterface.jar TestAuthenticationService/app/libs/KeyboardAuthenticationInterface.jar
