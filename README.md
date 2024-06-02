To run the server:

javac -cp ".:app/libs/json-20240205.jar" -sourcepath app/src/main/java -d out app/src/main/java/com/example/bnb/AccommodationServer.java
java -cp ".:out:app/libs/json-20240205.jar" com.example.bnb.AccommodationServer 


To run the Worker server:

javac -cp ".:app/libs/json-20240205.jar" -sourcepath app/src/main/java -d out app/src/main/java/com/example/bnb/WorkerServer.java 
java -cp ".:out:app/libs/json-20240205.jar" com.example.bnb.WorkerServer <port> 
