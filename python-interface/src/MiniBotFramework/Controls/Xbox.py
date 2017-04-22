#Reads xbox inputs and returns desired l/r wheel powers
from Lib.legopi.lib import xbox_read

for event in xbox_read.event_stream(deadzone=12000):

    # Convert input event into a string so we can parse it
    event_triggered = str(event)

    # Extracts the button pressed and value (0 or 1 depending on pressed or unpressed)
    button = event_triggered[event_triggered.find("(")+1:event_triggered.find(",")]
    value = event_triggered[event_triggered.find(",")+1:event_triggered.rfind(",")]

    if (button == "X1" or button == "Y1" or wow):
        wow=False
        if button == "X1":
            lastX = ((int(value)) / 32766)
        if button == "Y1":
            lastY = ((int(value)) / 32766)
        radius = math.sqrt(lastX*lastX + lastY*lastY)
        angle = math.atan2(lastY,lastX)
        if (radius < 0):
            radius = 0
        radius = radius * 100 # Scaling
        if radius > 100:
            radius = 100

        if angle >=0 and angle <= math.pi/2:
            fl = radius
        elif angle > math.pi/2 and angle < math.pi:
            fl = radius * math.cos(angle*2 - math.pi)
        elif angle >= -math.pi and angle <= -math.pi/2:
            fl = -radius
        else:
            fl = radius * math.sin(angle)

        if angle > 0 and angle <= math.pi/2:
            fr = radius * math.sin(angle)
        elif angle >= math.pi/2 and angle <= math.pi:
            fr = radius
        elif angle < -math.pi/2 and angle > -math.pi:
            fr = radius * -math.cos(angle*2 + math.pi)
        else:
            fr = -radius
    if radius > 10:
        return [abs(fl), abs(fr), angle]
    else:
        return []
