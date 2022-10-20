#!/bin/sh
scp chimebox.jar chimebox:/home/ubuntu/lib/chimebox.jar
ssh chimebox "systemctl restart chimebox"
