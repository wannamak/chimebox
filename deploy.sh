#!/bin/sh
scp chimebox.jar chimebox:/home/ubuntu/lib/chimebox.jar
ssh chimebox scp "systemctl restart chimebox"
