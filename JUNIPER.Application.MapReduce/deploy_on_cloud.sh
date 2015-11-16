#!/bin/bash
app_path=`pwd`

echo Make sure the application is compiled with ant

cat hosts |
while read -r host ; do
    echo Deploying on $host at $app_path
    rsync -ru $app_path/* $host:$app_path
done