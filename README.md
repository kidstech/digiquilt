# DigiQuilt

This repository contains two Java applications:

1. `DigiQuilt` - the client application
2. `DigiQuilt Server` - the server application

The project uses an Ant build file and is currently set up to build with Java 21.

## Quick Start

Run from the project root:

```sh
ant clean-classfiles jar-client jar-server
java -jar target/DigiQuiltServer.jar
```

In a second terminal:

```sh
java -jar target/DigiQuilt.jar
```

Coverage commands:

```sh
ant coverage
ant coverage-all-tests-pass
```

## Prerequisites

Before building or running the project, make sure you have:

1. Java 21 installed
2. Apache Ant installed

You can verify that with:

```sh
java -version
ant -version
```

## Change To The Project Root

Run all commands from the top-level project folder:

```sh
cd "<YOUR_TOP_LEVEL_FOLDER_OF_THE_PROJECT>"
```

This matters because the applications expect project-relative folders such as `quilts/` and `serverfolders/` to exist under the current working directory.

## Build Both Applications

To build both jar files:

```sh
ant clean-classfiles jar-client jar-server
```

This produces:

1. `target/DigiQuilt.jar`
2. `target/DigiQuiltServer.jar`

## Run The Server

Start the server first:

```sh
cd "<YOUR_TOP_LEVEL_FOLDER_OF_THE_PROJECT>"
java -jar target/DigiQuiltServer.jar
```

## Run The Client

In a second terminal, start the client:

```sh
cd "<YOUR_TOP_LEVEL_FOLDER_OF_THE_PROJECT>"
java -jar target/DigiQuilt.jar
```

## Recommended Workflow

### Terminal 1

```sh
cd "<YOUR_TOP_LEVEL_FOLDER_OF_THE_PROJECT>"
ant clean-classfiles jar-server
java -jar target/DigiQuiltServer.jar
```

### Terminal 2

```sh
cd "<YOUR_TOP_LEVEL_FOLDER_OF_THE_PROJECT>"
ant clean-classfiles jar-client
java -jar target/DigiQuilt.jar
```

## Useful Ant Targets

Build both jars with one target:

```sh
ant jar-all
```

Compile all source, including tests:

```sh
ant compile-all
```

Generate Javadoc API documentation:

```sh
ant javadoc
```

Javadoc output:

1. `reports/Javadoc/index.html`

Generate coverage reports (even if some tests fail):

```sh
ant coverage
```

Generate coverage reports and fail the build if any test fails:

```sh
ant coverage-all-tests-pass
```

Coverage outputs:

1. HTML report: `reports/Coverage/index.html`
2. XML report (for VS Code Coverage Gutters): `reports/Coverage/jacoco.xml`

## Notes

1. Run the server before starting the client.
2. The client expects the `serverfolders/` directory to be present.
3. The server expects the `serverfolders/` directory to be present.
4. The legacy Mac app-bundling targets exist in `build.xml`, but the jar-based commands above are the most reliable way to build and run the project.
