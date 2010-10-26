#!/bin/bash
if [ -z $1 ]
then
   echo Usage:
   echo      $0 [source_file] [destination_file]
   exit 1;
fi
if [ -e ../lib/compile ]
then
   for JAR in ../lib/compile/*
   do
      CLASSPATH=$CLASSPATH:$JAR
   done
fi
if [ -e ../lib/test ]
then
   for JAR in ../lib/test/*
   do
      CLASSPATH=$CLASSPATH:$JAR
   done
fi
for JAR in ../lib/*
do
   CLASSPATH=$CLASSPATH:$JAR
done
CLASSPATH=../jbosscache-core.jar:$CLASSPATH
echo classpath is $CLASSPATH
java -classpath $CLASSPATH -Dsource=$1 -Ddestination=$2 org.jboss.cache.config.parsing.ConfigFilesConvertor