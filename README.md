Package-structure of the Project (WIP)

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
Order: everything
Devices: everything
Customer: JPA Adapter, some more mult options
Dispatcher Service
Pagination
Exceptions
the actual server
Quarkus
Docker
Results + HTTP
Caching
probably more