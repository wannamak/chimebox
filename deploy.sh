#!/bin/sh
ant
scp chimebox.jar 192.168.1.220:/home/ubuntu/lib/chimebox.jar
ssh 192.168.1.220 "sudo systemctl restart chimebox"
