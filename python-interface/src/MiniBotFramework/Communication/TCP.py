#!/usr/bin/python
from socket import *
import multiprocessing, time, signal, os, sys, threading, socket
from threading import Thread


PORT = 10000
IP = ""

class TCP(object):

    tcp = None

    def __init__(self):
        self.server_socket = socket.socket(AF_INET, SOCK_STREAM)
        self.server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
        connectionSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print("Connection accepted")
        active=True
        while active:
            command = ""
            while active:
                try:
                    lastLen = len(command)
                    command += connectionSocket.recv(1024).decode()
                    if lastLen == len(command):
                        print("Connection Lost")
                        active = False
                        lastLen = -1
                        break
                except socket.error, e:
                    print("Connection Lost")
                    active = False
                    break
                end_index = command.find(">>>>")
                # In case of command overload
                while end_index > 0:
                    TCP.tcp.set_command(command[0:end_index+4])
                    command = command[end_index+4:]
                    end_index = command.find(">>>>")
