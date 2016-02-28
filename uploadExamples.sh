#!/bin/sh
for file in `ls ../demo-data/*.yaml`
do
    echo $file
    java -jar build/libs/pivio.jar  -server http://192.168.99.100:9123/document -file $file
done
