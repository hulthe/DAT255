import nav as n
#import os
from nav import *
#from nav1 import whole4, pause, cont
from driving import stop, drive, steer
from _thread import *

#f = open('/home/pi/dat255/autotest.log', 'a')
#f.write("hej" + '\n')
#f.close()
init()

time.sleep(10)

g.limitspeed = None
g.limitspeed0 = None

#time.sleep(10)

drive(40)
#steer(5)

time.sleep(10)
drive(-1)
