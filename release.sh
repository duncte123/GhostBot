#!/bin/sh
git push
git checkout master
git pull
git rebase develop
git push
git checkout develop
