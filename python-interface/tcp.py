from socket import *
serverPort = 3001
serverSocket= socket(AF_INET, SOCK_STREAM)
serverSocket.bind( ('', serverPort) )
serverSocket.listen(1)
print('Running')
while True:
	connectionSocket, addr = serverSocket.accept()
	message = connectionSocket.recv(1024).decode()
	received=open("received.py",'w')
	received.write(message)
	received.close()
	import received
	connectionSocket.close()
