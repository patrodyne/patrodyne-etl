#!/bin/sh
# -DskipTests=true - compile but do not run test classes.
# -Dmaven.test.skip=true - do not compile and do not run test classes.
# mvn -Dmaven.test.skip=true clean package
mvn -DskipTests=false clean package
