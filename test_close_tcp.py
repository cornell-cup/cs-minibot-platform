from socket import *
import multiprocessing, time, signal, os, sys, threading, socket

#From https//github.com/zephod/legopi
# from lib.legopi.lib import xbox_read


from multiprocessing import Process
from threading import Thread
from time import sleep
serverPort = 10003
serverSocket = socket.socket(AF_INET, SOCK_STREAM)
serverSocket.close()
