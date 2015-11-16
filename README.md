# JUNIPER Platform

This package contains a release of a software framework for developing and executing JUNIPER applications.
The package includes:

- A Platform - a run-time environment for executing JUNIPER applications (../JUNIPER.Platform/)
- A test JUNIPER application (../JUNIPER.Application.MapReduce/) that counts words in the given input text file (../JUNIPER.Application.MapReduce/input), following the famous MapReduce example of Hadoop 


## Prerequisites
- A Java RTE (e.g. https://www.java.com/en/download/)
- Open MPI (can be obtained from https://github.com/open-mpi/)
- Apache Ant build tool (optionally)

## Building
In order to build the test application, switch to its folder and run "ant" without parameters.
If ant is not available, you can alternatively execute "compile.sh" script.

## Running
The test application provides two examples of the application execution configuration - for 2 and 3 initial "maps".
They are provided in the corresponding *.xml files. For running them, please execute either of two run_*.sh scripts.
For a more complete description of the application model specification, please refer to JUNIPER deliverable D5.6 (available at www.juniper-project.eu).

## Development of new applications
We recommend using the Eclipse project supplied with the included MapReduce application.

## Support and Contact
You may contact us at cheptsov*at*hlrs.de for any further information on the JUNIPER platform or if any support is needed.