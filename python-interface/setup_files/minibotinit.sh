#!/bin/bash
echo "**** Starting Startup Script ****"
sleep 1
echo "**** Starting Xbox Driver ****"
sudo xboxdrv --wid 0 -l 3 --detach-kernel-driver --silent &
sleep 3
sudo killall xboxdrv
sleep 5
cd /home/pi/cs-minibot-platform/python-interface/src
echo "**** Starting Python Script ****"
sudo python -u main.py &
cd -
echo "**** Python Started :) ****"
sleep 1
echo "**** Have fun! ****"

