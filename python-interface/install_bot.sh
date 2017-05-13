#!/bin/bash
# ============== Functions
print() {
    echo =======================================
    echo " $@ "
    echo =======================================
}
# ==============

print WARNING: THIS WILL MESS STUFF UP IF YOU ALREADY USED IT!

print Setting up your MiniBot, but first a few questions:

print Which base config do you want to extend?
ls src/MiniBotConfig
read config

print What is your bots name?
read name

print Set your SSH password...
sudo passwd root

print Configure pi to be US, not UK. And open SSH, and set hostname, etc. You will have to do this yourself sorry mate.
sudo raspi-config

print Ok time to install...

print Setup MiniBot Config
cat src/MiniBotConfig/$config | sed "s/replacename/$name/g" > src/MiniBotConfig/config.json

print Setup autoexec in init.d
sudo cp ./setup_files/minibotinit.sh /etc/init.d/minibotinit.sh
sudo chmod 755 /etc/init.d/minibotinit.sh
sudo update-rc.d minibotinit.sh defaults

#print Setup hostname and password
#sudo echo $name > /etc/hostname

print Install vim
sudo apt-get install vim

print Install xboxdrv
sudo apt-get install xboxdrv

print Clone Cornell Cup Core Repo
git clone https://github.com/cornell-cup/cs-core.git ~/cs-core

print Run Core Setup
(cd ~/cs-core && ~/cs-core/setup.sh)

# https://confluence.cornell.edu/pages/viewpage.action?spaceKey=CCRT&title=SB+Getting+Started+with+Raspberry+Pi+Zero
print Removing junk
sudo apt-get -y purge wolfram-engine
sudo apt-get -y purge libreoffice*
rm -rf ~/python_games
sudo apt-get clean
sudo apt-get autoremove

print Install lsof
sudo apt-get -y install lsof

print Install freeport
sudo pip install freeport

print Install tmux
sudo apt-get -y install tmux

print Install nmap
sudo apt-get -y install nmap

print Install ZMQ 
sudo apt-get install libtool pkg-config build-essential autoconf automake uuid-dev python-dev
wget https://github.com/zeromq/libzmq/releases/download/v4.2.1/zeromq-4.2.1.tar.gz
tar -xf zeromq-4.2.1.tar.gz
cd zeromq-4.2.1 && ./configure
sudo make install && sudo ldconfig
sudo pip install pyzmq
cd ..
rm zeromq-4.2.1.tar.gz
sudo rm -rf zeromq-4.2.1/ 
sudo apt-get -y clean 
sudo apt-get -y autoremove

print Updating
sudo apt-get -y update
sudo apt-get -y upgrade
sudo apt-get -y dist-upgrade
