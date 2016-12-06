Chess Tournament
================
A chess tournament manager

How to run
-----------
**Run tests:** ./gradlew check

**Build application:** ./gradlew shadowJar


> monrad.yaml contains all properties for the application. Guess it should be part of .gitignore, but 
I haven't bothered yet :-)
It is currently set up to use an in-memory-database.

**Run application:** java -jar build/libs/monrad-1.0-SNAPSHOT-all.jar server monrad.yaml

