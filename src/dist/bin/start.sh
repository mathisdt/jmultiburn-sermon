#!/bin/sh
JAR_DIR=$(dirname $(readlink -f $0)
BASE_DIR=$(readlink -f $JAR_DIR/..)
java -Dbase.dir="$BASE_DIR" -jar "$JAR_DIR/jmultiburn-sermon.jar" $*
