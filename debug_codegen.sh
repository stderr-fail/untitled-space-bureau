#!/bin/bash

export JAVA_HOME=$JAVA_HOME21

exec ./gradlew :orbital-lines-stuff:build --rerun -Dorg.gradle.debug=true --no-daemon \
    -Pkotlin.compiler.execution.strategy=in-process

#exec ./gradlew :orbital-lines-stuff:kspKotlin --rerun -Dorg.gradle.debug=true --no-daemon \
#    -Pkotlin.compiler.execution.strategy=in-process
