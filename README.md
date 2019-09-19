Functionality :

implemented multi-threaded web server with thread-pooling.

Application details:

- Loads file from server (search file only in one local folder) and displays content in browser
- Uses only two http methods HEAD and GET
- Supports keep alive possibility


Tech stack:

- Java 1.8
- Maven
- Junit


How to use:

- Run : mvn clean install  (will create a jar in target folder)
- Run Jar artifact: java -jar artifact.jar
- In command prompt specify local system folder (for searching files) and server port
- Run in browser localhost:port/filename.txt
