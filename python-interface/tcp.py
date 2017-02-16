from socket import *
serverPort = 3001
serverSocket= socket(AF_INET, SOCK_STREAM)
ip = "0.0.0.0"
serverSocket.bind( (ip, serverPort) )
serverSocket.listen(1)
print('Running')
connectionSocket, addr = serverSocket.accept()
command = ""
while True:
	command += connectionSocket.recv(1024).decode()
        if command.find(">>>") > 0:
                break
	#print command
received=open("received.py",'w')
received.write(command)
received.close()
import received
connectionSocket.close()

"""
while True:
        while True:
                command += connectionSocket.recv(1024).decode()
                if command.find(">>>>") > 0:
                        print command
                        received=open("received.py",'w')
                        received.write(command)
                        received.close()
                        import received
                        break
connectionSocket.close()

"""
