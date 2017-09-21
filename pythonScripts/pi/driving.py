import os
import time

from navScripts.nav_log import tolog
from navScripts.nav_util import sign

from pi.navScripts import nav_signal


def dodrive(sp, st):
    g.send_sp = sp
    g.send_st = st

# error here: we normalize the argument, but not the other value

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
        os.system(cmd)

def steer(st):
    g.steering = st
    if g.simulate:
        return
    sp = g.outspeed
    dodrive(sp, st)


def stop(txt = ""):
    g.steering = 0
    g.outspeed = 0.0

    g.speedtime = None

    tolog("(%s) motor %d steer %d" % (txt, g.outspeed, g.steering))
    dodrive(0, 0)

#============================================================

def senddrive():

    first0done = False

    old_sp = 0
    old_st = 0
    while True:
        time.sleep(0.00001)

        if g.send_sp != None:

            g.send_sp = int(g.send_sp)
            g.send_st = int(g.send_st)

            sp = g.send_sp
            if sp < 0:
                sp += 256
            st = g.send_st
            if st < 0:
                st += 256

            if not g.senddriveinhibited:
                if (sp == 0 and not first0done) or g.last_send != (sp, st):
                    cmd = "/home/pi/can-utils/cansend can0 '101#%02x%02x'" % (
                        sp, st)
                    g.last_send = (sp, st)
                    os.system(cmd)
                    if sp == 0:
                        first0done = True
        if g.ledcmd:
            (mask, code) = g.ledcmd
            cmd = "/home/pi/can-utils/cansend can0 '461#060000006D3%d3%d00'" % (
                mask, code)
            os.system(cmd)
            g.ledcmd = None



# do something with dang - it is the most reliable variable
def braketest(v0, v1):
    steer(-100)
    nav_signal.setleds(0, 7)
    drive(v0/3)
    time.sleep(2)
    drive(v0*2/3)
    time.sleep(2)
    drive(v0)
    time.sleep(10)
    while True:
        time.sleep(0.0001)
        if g.ang%360 < 5:
            nav_signal.setleds(0, 0)
            o0 = g.odometer
            a0 = g.ang
            drive(v1)
            break
    time.sleep(5)
    o1 = g.odometer
    a1 = g.ang
    print((o1-o0, a1-a0))

def drivinginit():
    g.send_sp = None
    g.send_st = None

