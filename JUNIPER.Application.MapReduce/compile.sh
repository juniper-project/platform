#!/bin/bash

if [ -d "bin" ]; then
    rm -r bin/*
else
    mkdir bin
fi

files=`find src -type f -name '*.java'`
mpijavac $files -d bin -cp .:external_libs/mpi.jar:external_libs/juniper-platform.jar -Xlint:unchecked