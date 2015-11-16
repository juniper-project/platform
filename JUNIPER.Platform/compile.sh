#!/bin/bash

if [ -d "bin" ]; then
    rm -r bin/*
else
    mkdir bin
fi

files=`find src -type f -name '*.java'`
mpijavac $files -d bin -cp .:external_libs/mpi.jar -Xlint:unchecked
cd bin
jar cfv juniper-platform.jar .
cd ..