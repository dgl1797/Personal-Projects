#!/bin/sh
./mvnw clean package -DskipTests
java -jar target/njtodo-0.0.1.jar
"$@"
/bin/sh # safety keep alive