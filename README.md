# Cloud storage
Client server application created using Netty framework and JavaFX.  
The application provides user authentication, the ability to store files on a remote server, upload files to the server and download files from the server.  
Also, it supports big files transfer.  
## Launch
1. `git clone -b develop https://github.com/central-university-dev/cloud-storage-part-1-hw-3-Adelbas.git`
2. Go to project folder
3. To run server type `./gradlew :server:run`
4. To run client type `./gradlew :client:run`
5. To run tests type `./gradlew :server:test`
## Modules
- Server module provides server functionality.
- Client module provides JavaFX application that connect to the server.
- Common-commands module contains commands for communication between server and the client and command's decoder and encoder.