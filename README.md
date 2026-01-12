How to start the tests for this project:
    1. Navigate to the root directory of the project in a shell (bash, powershell, etc.); Opening the project in an IDE with an integrated shell also works
    2. Execute the command "mvn verify"; Alternatively ".\mvnw install" (or ./mvnw install, depending on OS) should also work
    3. All 239 Tests execute

How to start the application:
    1. Create "quarkus-run.jar" using "mvn verify" or ".\mvnw install"; should already be done from the tests
    2. From the root directory of the project execute "java -jar target/quarkus-app/quarkus-run.jar"
    3. The dispatcher service should now be available at http://localhost:8080/ or http://127.0.0.1:8080/

TODO:
use cases
ReadMe
Video
Documentation (LateX)

Use Cases:

Get all devices/orders of a customer with a certain name(Get device ?surname={surname}&?name={name})
Finalize order status (Put order /{id}?status={Completed}&final_cost) Patch? maybe in body?
DELETE canceled orders(DELETE orders?status=Canceled)
Get all devices from a certain brand (Get devices?brand={Brand})
Get orders with device serial number
Get all completed orders