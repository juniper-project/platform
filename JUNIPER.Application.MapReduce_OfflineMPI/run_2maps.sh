if [ -f "hosts" ]; then
    java -cp .:`pwd`/lib/OfflineMPI_0.1.22.jar:`pwd`/lib/juniper-platform.jar:`pwd`/bin mpi.RunOnPlatform 6 `pwd`/application_model_2maps.xml
else
    java -cp .:`pwd`/lib/OfflineMPI_0.1.22.jar:`pwd`/lib/juniper-platform.jar:`pwd`/bin mpi.RunOnPlatform 6 `pwd`/application_model_2maps.xml
fi
