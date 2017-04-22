#Given two GPIO motors and desired wheel sppeds for left and right wheel, sets the GPIO signals


class TwoWheelMovement(motorL, motorR, speedL, speedR, angle):
        front_left = int(speedL)
        front_right = int(speedR)

        if angle < 2 * math.pi / 3 and angle > math.pi / 3:
            #move forwards
            motorL.rotate_forward(front_right)
            motorR.rotate_forward(front_left)
        elif angle > -(2 * math.pi / 3) and (angle < -math.pi / 3):
            #move backwards
            pwn20.ChangeDutyCycle(BR)
            pwn19.ChangeDutyCycle(front_right)
            pwn16.ChangeDutyCycle(BL)
            pwn13.ChangeDutyCycle(front_left)
        elif angle < math.pi/2:
            pwn20.ChangeDutyCycle(BR)
            pwn19.ChangeDutyCycle(BR)
            pwn16.ChangeDutyCycle(BL)
            pwn13.ChangeDutyCycle(BL)
        elif angle < math.pi:
            pwn20.ChangeDutyCycle(front_right)
            pwn19.ChangeDutyCycle(front_right)
            pwn16.ChangeDutyCycle(front_left)
            pwn13.ChangeDutyCycle(front_left)
        elif angle < 3 * math.pi / 2:
            pwn20.ChangeDutyCycle(BL)
            pwn19.ChangeDutyCycle(BL)
            pwn16.ChangeDutyCycle(BR)
            pwn13.ChangeDutyCycle(BR)
        else:
            pwn20.ChangeDutyCycle(BL)
            pwn19.ChangeDutyCycle(BL)
            pwn16.ChangeDutyCycle(BR)
            pwn13.ChangeDutyCycle(BR)
    else:
        print("stahp")
        pwn20.ChangeDutyCycle(100)
        pwn19.ChangeDutyCycle(100)
        pwn13.ChangeDutyCycle(0)
        pwn16.ChangeDutyCycle(0)
