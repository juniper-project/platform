if [ -f "hosts" ]; then
    mpirun -machinefile hosts --oversubscribe --map-by node -np 8 java -jar `pwd`/target/mapreduce-1.0-jar-with-dependencies.jar `pwd`/application_model_3maps.xml
else
    mpirun -np 8 java -jar `pwd`/target/mapreduce-1.0-jar-with-dependencies.jar `pwd`/application_model_3maps.xml
fi