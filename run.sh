#!/usr/bin/env bash
#find . -type f -regex "\\.\/GhostBot-[0-9]+\\.[0-9]+\\.[0-9]+_[a-zA-Z0-9]{6,9}-all\\.jar"

#find | grep '^\./GhostBot-[0-9]+\.[0-9]+\.[0-9]+_[a-zA-Z0-9]{6,9}-all\.jar'
file=$(find | grep '^\./GhostBot-' | head -1)
command="java -jar $file"

echo "Runing $command"
${command}