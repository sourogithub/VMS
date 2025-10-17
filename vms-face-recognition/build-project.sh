# there is a specific build order

cd Vms-Common-Data
mvn clean 
mvn install
cd ..

cd Vms-Common-Utility
mvn clean 
mvn install
cd ..

cd Vms-Codec-Engine
mvn clean 
mvn install
cd ..

cd Vms-DB-Manager
mvn clean 
mvn install
cd ..

cd Vms-Event-Handler
mvn clean 
mvn install
cd ..

cd Vms-Image-Probe
mvn clean 
mvn install
cd ..

cd Vms-Media-Server
mvn clean 
mvn install
cd ..

cd Vms-Session-Manager
mvn clean 
mvn install
cd ..

cd Vms-Storage-Manager
mvn clean 
mvn install
cd ..

cd Vms-Streaming-Server
mvn clean 
mvn install
cd ..

cd Vms-View-Component
mvn clean 
mvn install
cd ..

cd Vms-Desktop-Client
mvn clean 
mvn install
cd ..

cd Vms-Watchdog
mvn clean 
mvn install
cd ..
