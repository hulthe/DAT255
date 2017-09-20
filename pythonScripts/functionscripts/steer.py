#lumo: Urklipp av importer + funktionen steer i driving.py

import os
import time

import nav_signal

from nav_log import tolog, tolog0
from nav_util import sign

def steer(st):
    g.steering = st
    if g.simulate:
        return

    sp = g.outspeed
#    if st < 0:
#        st += 256
#    if sp < 0:
#        sp += 256

#    tolog("motor %d steer %d" % (g.outspeed, st))
#    cmd = "/home/pi/can-utils/cansend can0 '101#%02x%02x'" % (
#        sp, st)
    #print (g.outspeed, st, cmd)
    dodrive(sp, st)
#    os.system(cmd)
#    tolog("motor %d steer %d" % (g.outspeed, st))
