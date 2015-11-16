if [ -f "hosts" ]; then
    mpirun -machinefile hosts --oversubscribe -np 6 java -cp `pwd`:`pwd`/bin:`pwd`/external_libs/mpi.jar:`pwd`/external_libs/juniper-platform.jar eu.juniper.platform.Rte `pwd`/application_model_2maps.xml
else
    mpirun -np 6 java -cp `pwd`:`pwd`/bin:`pwd`/external_libs/mpi.jar:`pwd`/external_libs/juniper-platform.jar eu.juniper.platform.Rte `pwd`/application_model_2maps.xml
fi