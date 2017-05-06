def distance(p1, p2):
    """ Returns distance between two 3-tuples. 
    Used for evaluating color """
    return math.sqrt((p1[0]-p2[0])**2 + (p1[1]-p2[1])**2 + (p1[2]-p2[2])**2)

def normalize(vector):
    """ Returns a 3-element vector as a unit vector. """
    sum = vector[0] + vector[1] + vector[2]
    return (vector[0]/(sum+0.0), vector[1]/(sum+0.0), vector[2]/(sum+0.0))

def average(lis):
    """ Returns the average of a list."""
    return sum(lis)/(len(lis)+0.0)

def maxmin(lis):
    return (max(lis), min(lis))