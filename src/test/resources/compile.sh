#!/bin/bash
gcc a.c -o a
./a > result.txt
result=$(<result.txt)
echo "$result" 
curl --data '{"result":"'"$result"'"}' \
-H "Content-Type: application/json" \
-X POST http://host.docker.internal:8080/docker/playground
