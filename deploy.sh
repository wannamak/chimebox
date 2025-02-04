#!/bin/sh
set -e
ant
scp chimebox.jar chimebox:/home/ubuntu/lib/chimebox.jar
ssh chimebox "sudo systemctl restart chimebox"
