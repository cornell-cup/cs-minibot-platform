#!/usr/bin/env python

# =============================================================================
# MasterBroadcast.py
# Project SwarmBot -> MiniBot SP17
# Cornell Cup Robotics Team, Cornell University
# =============================================================================
# Connects to the Minions and broadcasts messages using ZeroMQ protocol
# IMPORTANT: Always set the Mediator first
# =============================================================================

import sys, os, time, zmq
from threading import Thread
import random
from Queue import Queue
import socket
import fcntl
import struct

# ============================================================================
# start Class ZMQExchange
# ============================================================================
class ZMQExchange:
    def __init__(self):
        """
        Initializes the ZMQ broadcaster
        """
        # default constants
        self.__xpub_port = "5555"
        self.__xsub_port = "5556"

        self.setMediatorIP("127.0.0.1")

        # prefix to the message, for security version
        self.messageTopic = "ccrt-minibot-swarmbot-master"
        
        # true depending on the type of device
        self.isBroadcaster = False
        self.isReceiver = False
        self.isMediator = False
        
        # initialize connection
        self.context = zmq.Context()

    def getIP(self, ifname):
        """
        Returns the IP of the device
        """
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return socket.inet_ntoa(fcntl.ioctl(
            s.fileno(),
            0x8915,  # SIOCGIFADDR
            struct.pack('256s', ifname[:15])
            )[20:24])

    def setMediatorIP(self, IP):
        self.__xpub_url = "tcp://" + IP + ":" + self.__xpub_port
        self.__xsub_url = "tcp://" + IP + ":" + self.__xsub_port

    def setMediator(self):
        """
        Initializes the Mediator
        """

        # set the ip and xpub/xsub URLs
        self.setMediatorIP(self.getIP('wlan0'))

        # set up the mediator
        self.xpub = self.context.socket(zmq.XPUB)
        self.xpub.bind(self.__xpub_url)
        self.xsub = self.context.socket(zmq.XSUB)
        self.xsub.bind(self.__xsub_url)
        self.poller = zmq.Poller()
        self.poller.register(self.xsub, zmq.POLLIN)
        self.poller.register(self.xpub, zmq.POLLIN)
        self.isMediator = True
        print "Successfully set up mediator"

    def mediate(self):
        """
        Mediates the Data Transfer between Master and Minions like a Proxy
        """
        
        while True:
            if self.isMediator:
                try:
                    # poll the proxy URLs to see what messages are waiting
                    # if any, forward them
                    events = dict(self.poller.poll(1000))
                    #print "mediating..."
        
                    if self.xpub in events:
                        # message received from Minions on successful subscription
                        message = self.xpub.recv_multipart()
                        #print("%r" % message[0])
                        self.xsub.send_multipart(message)
        
                    if self.xsub in events:
                        # message from Master to be delivered to the Minions
                        message = self.xsub.recv_multipart()
                        #print("publishing message: %r" % message)
                        self.xpub.send_multipart(message)
                except KeyboardInterrupt:
                    print "Mediator ending"
                    break
            else:
                break
    def setBroadcaster(self):
        """
        Initializes the broadcaster
        """
        self.pub = self.context.socket(zmq.PUB)
        # broadcaster pub connects to the receiver subs' proxy url
        self.pub.connect(self.__xsub_url)
        self.isBroadcaster = True
        print "Successfully set up broadcaster"

    def broadcast(self, data):
        """
        Broadcasts the message to the subscribers

        :param data A tuple. data[0] is the left wheel speed, 
            data[1] is the right wheel speed
        """
        
        # send the message
        if self.isBroadcaster:
            #print "broadcasting", str(data)
            msg = [self.messageTopic, str(data)]
            self.pub.send_multipart(msg)
            #print "broadcasted"
        
    def setReceiver(self, mediatorIP = None):
        """
        Initializes the receiver
        :param mediatorIP is the mediator's IP. The Minion should use mediatorIP
        """

        if mediatorIP is not None:
            self.setMediatorIP(mediatorIP)
        
        self.sub = self.context.socket(zmq.SUB)
        
        # receiver sub connects to the broadcaster pubs' proxy url
        self.sub.connect(self.__xpub_url)
        # only accept messages that start with self.messagetopic
        self.sub.setsockopt(zmq.SUBSCRIBE, self.messageTopic)
        self.isReceiver = True
        print "Successfully set up receiver"

    def receive(self, receivedQueue = None):
        """
        Receives the data and translates into command
        :param receivedQueue A Queue for putting in values which other threads
            can access. If None, the method just prints it
        """
        oldData = "empty"
        while True:
            if self.isReceiver:
                try:
                    # wait infinitely to receive the message
                    if self.sub.poll(timeout=0):
                        data = self.sub.recv_multipart()
                        #print "received ", data
                        
                        if oldData != data:
                            # parse the data into lWheel and rWheel and send it as a
                            # tuple
                            start = data[1].find("(")
                            comma = data[1].find(",")
                            end = data[1].find(")")
                            lWheel = int(data[1][start + 1:comma])
                            rWheel = int(data[1].strip()[comma + 1:end])
                            info = (lWheel, rWheel)
            
                            # do something, send commands
                            if receivedQueue is not None:
                                receivedQueue.put(info)
                            else:
                                #print "received ", info
                                pass
                            oldData = data            
                except KeyboardInterrupt:
                    print "Receiver stopping"
                    break
            else:
                break

    def stopZMQExchange(self):
        """
        Stops the connection and closes the socket
        """
        
        if self.isBroadcaster:
            self.pub.close()
            self.isBroadcaster = False
        if self.isMediator:
            self.xpub.close()
            self.xsub.close()
            self.isMediator = False
        if self.isReceiver:
            self.sub.close()
            self.isReceiver = False
        self.context.term()

# =========================================================================
# end Class ZMQExchange
# =========================================================================