if [ -f "hosts" ]; then
    java -cp .:`pwd`/lib/OfflineMPI_0.1.22.jar:`pwd`/lib/juniper-platform.jar:`pwd`/bin mpi.RunOnPlatform 8 `pwd`/application_model_3maps.xml
else
    java -cp .:`pwd`/lib/OfflineMPI_0.1.22.jar:`pwd`/lib/juniper-platform.jar:`pwd`/bin mpi.RunOnPlatform 8 `pwd`/application_model_3maps.xml
fi
