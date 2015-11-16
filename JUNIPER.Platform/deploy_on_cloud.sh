#!/bin/bash

#
# This script is used to deploy the application on multiple cloud nodes.
# The ip of nodes are taken from "hosts" file.
#

app_path=`pwd`

echo Deploying on cloud. Make sure the application is compiled

while read host || [ -n "$host" ]
do
    echo Deploying on $host at $app_path
    rsync -rua $app_path/* $host:$app_path
done < hosts
