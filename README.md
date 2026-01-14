How to start the tests for this project:
    1. Navigate to the root directory of the project in a shell (bash, powershell, etc.); Opening the project in an IDE with an integrated shell also works
    2. Execute the command "mvn verify"; Alternatively ".\mvnw install" (or ./mvnw install, depending on OS) should also work
    3. All 239 Tests execute

How to start the application:
    1. Create "quarkus-run.jar" using "mvn verify" or ".\mvnw install"; should already be done from the tests
    2. From the root directory of the project execute "java -jar target/quarkus-app/quarkus-run.jar"
    3. The dispatcher service should now be available at http://localhost:8080/ or http://127.0.0.1:8080/

TODO:
Video
Documentation (LateX)
