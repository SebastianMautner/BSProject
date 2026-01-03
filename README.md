Package-structure of the Project

proj (Main folder of the project, including files such as pom.xml)
├── src (main folder with the source code)
|   ├── main
|   |   ├── docker (configs of the docker)
|   |   ├── java\sys\bac (main code of the backend system)
|   |   |   ├── adapters (the ports i think)
|   |   |   |   ├── in\api (the frontend port)
|   |   |   |   |   ├── adapter (different representations of models for the frontend (can be also contain lists, to represent multiple results))
|   |   |   |   |   ├── exceptions (possible exceptions that may occur)
|   |   |   |   |   ├── models (the serializable models to be transmitted, also responsible for defining the link class, that allows you to link other models)
|   |   |   |   |   └── utils (utilities required to implement http and hypermedia)
|   |   |   |   └── out\persistence (the output port to the persistence component)
|   |   |   └── application (business logic)
|   |   |       ├── domain (business logic, also models/ classes required for processing/ answering queries)
|   |   |       |   ├── models (more models?)
|   |   |       |   └── results (the possible results of a query (errors, results with one object, results with multiple, etc.))
|   |   |       └── port (it insists upon itself)
|   |   |           ├── in (the use cases, as interfaces)
|   |   |           └── out (also use cases, but as ports?)
|   |   └── resources (it insists upon itself; especially contains persistence compontent-config)
|   └── test (the junit tests)
|       ├── docker (docker config for tests)
|       └── java\sys\bac (the actual tests)
└── target (the bin files (has a structure, but thats auto generated))

TODO:
1:n (evtl. Verlinkung [wenn Lust und Zeit])
use cases
ReadMe
Video
Tests
Documentation (learn LaTeX)

Use Cases:

Get all devices/orders of a customer with a certain name(Get device ?surname={surname}&?name={name})
Finalize order status (Put order /{id}?status={Completed}&final_cost) Patch? maybe in body?
DELETE canceled orders(DELETE orders?status=Canceled)
Get all devices from a certain brand (Get devices?brand={Brand})
Get orders with device serial number
Get all completed orders

Open Questions:


Asked Questions
What the fuck does he mean with the whole docker bullshit?
Is XML really necessary?
is using localhost for addresses fine, or does it need to be a seperate thing?
Video Contents
Ist Caching einsehbar?