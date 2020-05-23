#!/bin/bash
## 파라미터가 없으면 종료
if [ "$#" -lt 1 ]; then
  echo "$# is Illegal number of parameters."
  echo "Usage: $0 [options]"
  exit 1
fi
args=("$@")
tr -cd '\11\12\15\40-\176' < "${args[0]}" > new.c
gcc new.c -o output
./output > result.txt
result=$(<result.txt)
echo "$result" 
curl --data '{"result":"'"$result"'"}' \
-H "Content-Type: application/json" \
-X POST http://host.docker.internal:8080/docker/playground
