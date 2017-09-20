#lumo: Urklipp av importer + funktionen drive i driving.py

import os
import time

import nav_signal

from nav_log import tolog, tolog0
from nav_util import sign

def drive(sp):
    if True:
        if sp != 0 and not g.braking:
            g.speedsign = sign(sp)

        g.outspeedcm = sp*2
        print("outspeedcm = %d" % g.outspeedcm)
    else:

        # do this in readspeed2 instead
        # maybe, but then steer will zero the speed
        g.outspeed = sp

        if abs(sp) >= 7:
            g.speedtime = time.time()
        else:
            g.speedtime = None

        if sp != 0 and not g.braking:
            g.speedsign = sign(sp)

        if sp < 0:
            sp += 256
        st = g.steering
        if st < 0:
            st += 256
        tolog("motor %d steer %d" % (sp, g.steering))
        cmd = "/home/pi/can-utils/cansend can0 '101#%02x%02x'" % (
            sp, st)
        #print (sp, g.steering, cmd)
        os.system(cmd)

