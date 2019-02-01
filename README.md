# Revolut Backend Test
## Requirements
Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

Explicit requirements:
1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 – keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require
a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

## Technology stack
I usualy use vert.x stack and other open-source solutions(e.g. swagger for documentation), but considering requirement #4 I tried to implement the task using a minimum of dependencies.
I assume that purpose of the task is to demonsrate core knowledge and skills(e.g. concurrency), therefore I used these technologies:
* Java 8
* Netty
* Jackson
* Maven
* TestNG + REST-assured for tests.

## Assumtions
Solution may contain some assumptions which are not crucial in test task, but are important in production-ready solutions
* No OpenAPI specification
* No support of path-params
* Low unit-test coverage
* No config
* Poor logs
* No DI

and some others.

## Project building
`mvn clean package`

## Project running
`java -jar backend-test-1.0-SNAPSHOT-uber.jar`
will run server on `localhost:8080`

