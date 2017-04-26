#!/usr/bin/env python

# =============================================================================
# MasterBroadcast.py
# Created by Celine Choo and Anmol Kabra
# Project SwarmBot -> MiniBot SP17
# Cornell Cup Robotics Team, Cornell University
# =============================================================================
# Connects to the Minions and broadcasts messages using ZeroMQ protocol
# =============================================================================

import sys, os, time, zmq
from threading import Thread
import random
from Queue import Queue

class ZMQExchange:
    def __init__(self):
        """
        Initializes the ZMQ broadcaster
        """
        # constants
        self.__xpub_url = "tcp://192.168.4.53:5555"
        self.__xsub_url = "tcp://192.168.4.53:5556"
        # prefix to the message
        self.messageTopic = "ccrt-minibot-swarmbot-master"
        
        # true depending on the type of device
        self.isBroadcaster = False
        self.isReceiver = False
        self.isMediator = False
        
        # initialize connection
        self.context = zmq.Context()

    def setMediator(self):
        """
        Initializes the Mediator
        """
        self.xpub = self.context.socket(zmq.XPUB)
        self.xpub.bind(self.__xpub_url)
        self.xsub = self.context.socket(zmq.XSUB)
        self.xsub.bind(self.__xsub_url)
        self.poller = zmq.Poller()
        self.poller.register(self.xsub, zmq.POLLIN)
        self.poller.register(self.xpub, zmq.POLLIN)
        self.isMediator = True
        print "successfully set up mediator"

    def mediate(self):
        """
        Mediates the Data Transfer between Master and Minions like a Proxy
        """
        
        while True:
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
        #print "broadcasting " + str(data)
        msg = [self.messageTopic, str(data)]
        self.pub.send_multipart(msg)
        #print "broadcasted"

    def setReceiver(self):
        """
        Initializes the receiver
        """
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
        
    def stopZMQExchange(self):
        """
        Stops the connection and closes the socket
        """
        
        if self.isBroadcaster:
            self.pub.close()
        if self.isMediator:
            self.xpub.close()
            self.xsub.close()
        if self.isReceiver:
            self.sub.close()
        self.context.term()

# =========================================================================
# end Class ZMQExchange
# =========================================================================

def testBroadcast():
    """
    for testing purposes
    """
    z = ZMQExchange()
    z.setBroadcaster()
    try:
        while True:
            r = random.randint(0, 3)
            z.broadcast(r)
            time.sleep(1)
    except KeyboardInterrupt:
        print "bye bye"
    finally:
        z.stopZMQExchange()

def testMediator():
    z = ZMQExchange()
    z.setMediator()
    try:
        z.mediate()
    finally:
        z.stopZMQExchange()

def testReceive():
    z = ZMQExchange()
    z.setReceiver()
    try:
        z.receive()
    finally:
        z.stopZMQExchange()

if __name__ == "__main__":
    inp = int(input("[TEST] Enter 1 for broadcast, 2 for mediator, 3 for receive: "))
    if (inp == 1):
        testBroadcast()
    elif (inp == 2):
        testMediator()
    elif (inp == 3):
        testReceive()
