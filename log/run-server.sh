#! /bin/bash

nohup java -jar glickr-server.jar 192.168.0.2 3300 40 >> server.log 2 >& 1  &
