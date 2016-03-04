#!/bin/bash

cd mcp918
ant jar
cd ..
java -jar proguard.jar @proguard.pro -verbose | grep -v "Maybe this is library method"
rm mcp918/dist/MineBot.jar
mv proguarded/MineBot.jar mcp918/dist/
cd mcp918/jars
java -Djava.library.path=versions/1.8.8/1.8.8-natives/ -jar ../dist/MineBot.jar

