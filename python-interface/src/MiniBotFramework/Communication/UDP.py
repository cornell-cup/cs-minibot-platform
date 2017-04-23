# UDP code taken from < https://pymotw.com/2/socket/udp.html >
def udpBeacon():
	# Create a UDP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    my_ip = str(socket.gethostbyname(socket.gethostname()))
    spliced_subnet = my_ip[:my_ip.rfind('.')] + ".255"

	# Define broadcasting address and message
    server_address = (spliced_subnet, 5001)
    message = 'Hello, I am a minibot!'

	# Send message and resend every 9 seconds
    while True:
        try:
		    # Send data
            print('sending broadcast: "%s"' % message)
            sent = sock.sendto(message, server_address)
        except Exception as err:
            print(err)
        time.sleep(9)
