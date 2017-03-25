import socket
import sys
import time

HOST = "localhost"
PORT = 11111

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

class BaseMiniBot:
    """
    Abstract class defining the base functions of the MiniBot. More customized MiniBots may
    subclass this.
    """
    def __init__(self):
        server_address = (HOST, PORT)
        sock.connect(server_address)

    def move_forward(self, power):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        message = "FORWARD:" + str(power) + '\n'
        sock.sendall(message)
        return

    def move_backward(self, power):
        """
        Moves the bot backward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        # print("Unimplemented: Moving backward "+str(power))
        message = "BACKWARD:" + str(power) + '\n'
        sock.sendall(message)
        return

    def turn_clockwise(self, power):
        """
        Moves the bot clockwise  at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        # print("Unimplemented: Turning clockwise "+str(power))
        message = "RIGHT:" + str(power) + '\n'
        sock.sendall(message)
        return

    def turn_counter_clockwise(self, power):
        """
        Moves the bot counter-clockwise at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        # print("Unimplemented: Turning counter clockwise "+str(power))
        message = "LEFT:" + str(power) + '\n'
        sock.sendall(message)
        return

    def set_wheel_power(self, front_left, front_right, back_left, back_right):
        """
        Sets the power of the bot's wheels as a percentage from -100 to 100. If a wheel
        specified does not exist, the power for that wheel is ignored.
        :param front_left power to deliver to the front_left wheel
        :param front_right power to deliver to the front_right wheel
        :param back_left power to deliver to the back_left wheel
        :param back_right power to deliver to the back_right wheel
        :return True if the action is supported
        """
        # print("Unimplemented: Setting wheel power to %d,%d,%d,%d" % (front_left, front_right, back_left, back_right))
        message = "WHEELS:" + str(front_left) + ',' + str(front_right) + ',' + str(back_left) + ',' \
                  + str(back_right) + '\n';
        sock.sendall(message)
        return

    def wait(self, t):
        """
        Waits for a duration in seconds.
        :param t The duration in seconds
        """
        message = "WAIT:" + str(t) + '\n'
        sock.sendall(message)
        time.sleep(t)
        return

    def stop(self):
        """
        Waits for a duration in seconds.
        :param t The duration in seconds
        """
        message = "STOP:0" + '\n'
        sock.sendall(message)
        return

    # def getSensorData(self):
    #     message = "GET:1" + '\n'
    #     sock.sendall(message)
    #     with open('output.txt', 'a') as file:
    #         file.write("Got: " + sock.recv(1024) + "\n")
    #     file.close()
    #     return

    def register_sensor(self, name):
        message = "REGISTER:" + name + '\n'
        sock.sendall(message)
        return

    def kill(self):
        """
        Kills TCP connection
        """
        message = "KILL:-1" + '\n'
        sock.sendall(message)
        sock.close()
        return


class Sensor:
    def __init__(self, bot, name):
        self.name = name
        bot.register_sensor(name)

    def read(self):
        return "Invalid: Abstract Sensor Class Reading"


class GPIOSensor(Sensor):
    def __init__(self, bot, name, pin_number):
        Sensor.__init__(self, bot, name)
        self.pin_number = pin_number

    def readAll(self):
        """
        Get Sensor Data
        """
        message = "GET:ALL" + '\n'
        sock.sendall(message)
        with open('output.txt', 'a') as file:
            file.write("Got: " + sock.recv(1024) + "\n")
        file.close()
        return

    def read(self, name):
        """
        Get Sensor Data
        """
        message = "GET:" + name + '\n'
        sock.sendall(message)
        with open('output.txt', 'a') as file:
            file.write("Got: " + sock.recv(1024) + "\n")
        file.close()
        return

bot = BaseMiniBot()
bot.turn_clockwise(50)
bot.wait(2)
bot.move_forward(100)