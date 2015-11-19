if [ -f "hosts" ]; then
    mpirun -machinefile hosts --oversubscribe -np 6 java -jar `pwd`/target/mapreduce-1.0-jar-with-dependencies.jar `pwd`/application_model_2maps.xml
else
    mpirun -np 6 java -jar `pwd`/target/mapreduce-1.0-jar-with-dependencies.jar `pwd`/application_model_2maps.xml
fi