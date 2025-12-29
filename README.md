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
Customer: some more mult options
Dispatcher Service
Pagination
Exceptions
Docker?
Results + HTTP
Caching
probably more

-------------------------------------------------------------------------------------------------------------------------------------------------

# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
