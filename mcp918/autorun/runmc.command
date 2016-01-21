#!/bin/sh

/usr/local/bin/ant jar
cd jars
java -Djava.library.path=versions/1.8.8/1.8.8-natives/ -jar ../dist/MineBot.jar
