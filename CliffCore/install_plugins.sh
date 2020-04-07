#!/bin/bash

printf "Cleaning old plugins folder . . .\n"
rm -r ./plugins
mkdir plugins

printf "Building Maven project . . .\n"
cd ..
mvn clean package

printf "Copying new plugin JARs . . .\n"
cp -a CliffNetworking/target/CliffNetworking-* CliffCore/plugins

printf "Done"