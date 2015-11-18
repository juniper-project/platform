#!/bin/bash

if [ -d "bin" ]; then
	rm -r bin/*
else
	mkdir bin
fi

files=`find src -type f -name '*.java'`
javac $files -d bin -cp .:`pwd`/lib/OfflineMPI_0.1.22.jar:`pwd`/lib/juniper-platform.jar:`pwd`/bin -Xlint:unchecked
