#!/bin/bash

MESSAGE=$1
if [ $# -lt 1 ]; then
  MESSAGE="updated"
fi
if [ $# -gt 1 ]; then
  echo "Provide exactly one argument. "
  exit
fi
git pull
find . -name .DS_Store -print0 | xargs -0 rm -f --
git add --all .
git commit -m "$MESSAGE"
git push
echo "------------"
echo "Commit ID: "
git rev-parse HEAD
echo "-------------"
time=$(date +"%r")
echo "Current time: $time"
