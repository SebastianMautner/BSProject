# Tests
From the root directory of the project (named 'BSproject'), simply run one of the following two commands in your shell of choice:
```bash
mvn verify
```
or
```bash
./mvnw install
```
Both cause all 239 Tests to be executed. Additionally they create the quarkus-run.jar, which is required to actually launch the application.

## Starting the application
If you haven't done so already, first create the quarkus-run jar file using:
```powershell
mvn verify
```
or
```bash
./mvnw install
```
Now the application can be started with:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```
The dispatcher service of the application should now be available at http://localhost:8080/ or http://127.0.0.1:8080/