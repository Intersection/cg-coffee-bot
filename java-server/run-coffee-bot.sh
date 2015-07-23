#!/usr/bin/env bash

if screen -S coffee -Q select .; then
  # Running
  exit
fi

screen -S coffee -d -m java -jar /root/coffee.jar
