#!/usr/bin/python
from socket import *
import multiprocessing, time, signal, os, sys, threading, socket
from threading import Thread


PORT = 10000
IP = "127.0.0.1"

class TCP(object):

    tcp = None

    def __init__(self):
        self.server_socket = socket.socket(AF_INET, SOCK_STREAM)
        self.server_socket.bind( (IP, PORT) )
        self.server_socket.listen(1)
        self.thread_tcp = Thread(target = run)
        self.thread_tcp.start()
        self.command = ""
        TCP.tcp = self

    def set_command(self, command):
        self.command = command

    def get_command(self):
        temp = self.command
        self.command = ""
        return temp

def run():
    while TCP.tcp is None:
        time.sleep(1)
    while True:
        print("Waiting for connection")
        connectionSocket, addr = TCP.tcp.server_socket.accept()
        print("Connection accepted")
        while True:
            command = ""
            while True:
                command += connectionSocket.recv(1024).decode()
                if command.find(">>>>") > 0:
                    TCP.tcp.set_command(command)
                    command = ""
