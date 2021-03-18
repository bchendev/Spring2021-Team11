#!/bin/bash

echo
echo "#########################################################"
echo "###  Running Java Formatter (scripts/format-java.sh)  ###"
echo "#########################################################"
echo

# This command does an in place format of all java files. Run this command from
# the top level directory of this repo and it will search for all java files.
# The downside of running the grep command below is that it will find all java
# files even if you didn't actually touch them. There is probably a command
# that let's you grab only the files that you've changed but I haven't figured
# out a good command to use yet.
java -jar tools/google-java-format-1.9-all-deps.jar --replace \
  $(git ls-files|grep \.java$)