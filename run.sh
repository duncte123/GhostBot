#!/bin/bash
file=$(find | grep '^\./GhostBot-' | head -1)
command="java -jar $file"

echo "Runing $command"
${command}