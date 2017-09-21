import json
import random
import re
import socket
import sys
import time
from math import pi

import queue

import nav_mqtt
import pi.eight
from heartbeat import heartbeat, heartbeat2
import user_commands
import network
from network import initializeCAN, connect_to_ecm

import nav_log
from nav_log import tolog

from nav_util import sign, start_new_thread, start_new_thread_really


class globals:
    pass


g = globals()

heartbeat.g = g
network.g = g
user_commands.g = g


def readvin():
    g.simulate = False

    try:
        f = open("/home/pi/can-utils/java/settings.properties")
        for line0 in f:
            line = line0[:-1]
            m = re.match("VIN=(.*)", line)
            if m:
                return m.group(1)
    except IOError:
        g.simulate = True
        if len(sys.argv) > 1:
            return sys.argv[1]
        else:
            return "car0"

    return None


g.VIN = readvin()
print("VIN %s" % g.VIN)

if not g.simulate:
    import nav_imu

g.standalone = True
g.standalone = False


if not g.simulate:
    import nav_mqtt
import nav_tc

import nav_signal
import nav1
import nav2
import nav_comm
import pi.wm
import pi.driving


if not g.simulate:
    nav_imu.g = g
nav_log.g = g
nav_mqtt.g = g
nav_tc.g = g
nav_signal.g = g
nav_comm.g = g
pi.wm.g = g
nav1.g = g
nav2.g = g
pi.driving.g = g

g.s = None

g.parameter = 164
g.parameter = 100
g.parameter = 152
g.parameter = 120

# --------------------
# Configuration flags:

g.allangles = False

# setting this to True is the same as setting goodmarkers to []?
g.ignoremarkers = False

# g.detectcrashes = False
g.detectcrashes = True

# --------------------
# Configuration parameters:

g.anglefactor = 4.0
g.targetdist = 0.3

g.minquality = 0.65
g.maxmarkerdist = 2.0
# now lane is 1m:
g.maxoffroad = 0.40

g.slightlyoffroad = 0.03

# None to disable:
g.adjdistlimit = 0.4

g.badmarkers = [0]
# 45 means angle when to ignore = 45 +- 45
g.badmarkers = [(47, 'all')]

g.goodmarkers = None
g.goodmarkers = [(7, 'all', 0.6), (25, 'all', 0.6), (22, 'all', 0.65), (2, 'all', 0.45)]

# --------------------
# Flags and variables for reporting by wm:

g.poserror = False
g.maxadjdist = 0

g.paused = False

# --------------------

# can be local to wm.py
g.adjust_t = None

g.shiftx = 0.0

g.markertimesep = 5
g.markertimesep = 2

# nav_imu and wm
g.oldpos = None

# set to 9.0 for wall crashes
g.crashlimit = 9.0

g.speedtime = None

g.speedsign = 1
g.braking = False

g.xydifffactor = 100
g.angdifffactor = 1000

# magnetics
g.mxmin = None
g.mxmax = None
g.mymin = None
g.mymax = None

g.droppedlog = 0

g.currentbox = None

# updated by wm (CAN)
g.inspeed = 0.0
g.finspeed = 0.0
g.leftspeed = 0.0
g.fleftspeed = 0.0
g.inspeed_avg = 0.0
g.odometer = 0
g.fodometer = 0
g.can_steer = 0
g.can_speed = 0
g.can_ultra = 0.0

# set by wm (CAN) and not used now
g.rc_button = False

g.limitspeed = None

# local to 'heartbeat' in nav.py
g.limitspeed0 = "notset"

g.last_send = None

g.remote_control = False

g.obstacle = False

g.targetx = None
g.targety = None

g.logf = None
g.accf = None

# px etc. is the dead reckoning from the IMU acc and gyro
# ppx etc. is the dead reckoning from wheel speed and gyro
g.px = None
g.py = None
g.pz = None

g.ppx = 0
g.ppy = 0
g.ppz = None

g.vx = None
g.vy = None
g.vz = None

g.ang = 0.0

# set by nav_imu
g.crash = False

# local to nav.connect_to_ecm but interesting to look at
g.crashacc = None

# set by wm, used by nav_imu
g.angdiff = 0.0
g.ppxdiff = 0.0
g.ppydiff = 0.0

g.dtlimit = 0.2

# set once by nav.py
g.t0 = None

# nav.py and nav2.py
# used only when simulating
g.speedfactor = 1.0
if g.simulate:
    g.speedfactor = 0.1

# set by nav_mqtt
g.battery = 0.0
g.ultra = 0.0

g.signalling = False

g.ledcmd = None

# between nav1 and nav_tc
g.nextdecisionpoint = 0

g.acc = 0

# nav_tc
g.tctime = None

pi.wm.wminit()
nav1.nav1init()
if not g.simulate:
    nav_imu.imuinit()
if not g.simulate:
    nav_mqtt.mqttinit()
else:
    nav_mqtt.mqttinit()

nav_tc.tcinit()
pi.driving.drivinginit()
nav_signal.signalinit()


def init():
    random.seed(g.VIN)

    if g.VIN == "car5":
        g.mxmin = -99
        g.mxmax = -40
        g.mymin = 100
        g.mymax = 152

    if g.VIN == "car3":
        g.mxmin = -20
        g.mxmax = 39
        g.mymin = 37
        g.mymax = 85

    # fake, just to make them defined
    if g.VIN == "car4":
        g.mxmin = 0
        g.mxmax = 100
        g.mymin = 0
        g.mymax = 100

    canNetwork = "can0"
    canFrameID = 1025
    if not g.simulate:
        g.canSocket = initializeCAN(canNetwork)

    g.angleknown = False

    pi.eight.eightinit()

    g.t0 = time.time()

    nav_signal.setleds(0, 7)

    suffix = ""
    if g.simulate:
        suffix = "-" + g.VIN
    g.logf = open("navlog" + suffix, "w")
    g.accf = open("acclog" + suffix, "w", 1024)

    if not g.standalone:
        start_new_thread(nav_tc.connect_to_ground_control, ())

    g.accf.write(
        "x y vx vy px py x0 y0 vvx vvy ppx ppy ang angvel steering speed inspeed outspeed odometer z0 r rx ry acc "
        "finspeed fodometer t pleftspeed leftspeed fleftspeed realspeed can_ultra droppedlog\n")

    g.accfqsize = 1000
    g.accfq = queue.Queue(g.accfqsize)
    start_new_thread(nav_log.logthread, (g.accfq,))

    g.qlen = 0

    tolog("t0 = %f" % g.t0)

    tolog("init")

    if not g.simulate:
        nav_imu.calibrate_imu()

    if not g.simulate:
        start_new_thread(pi.wm.readmarker, ())
    # if not g.simulate:
    start_new_thread(nav_mqtt.handle_mqtt, ())
    if not g.simulate:
        start_new_thread(pi.wm.readspeed2, ())
    if not g.simulate:
        start_new_thread_really(nav_imu.readgyro, ())
    if not g.simulate:
        start_new_thread(pi.driving.senddrive, ())
    if not g.simulate:
        start_new_thread(keepspeed, ())
    if not g.standalone:
        start_new_thread(heartbeat, ())
    if not g.simulate:
        start_new_thread(connect_to_ecm, ())

    if g.simulate:
        g.steering = 0
        g.finspeed = 0
        start_new_thread(pi.wm.simulatecar, ())


g.senddriveinhibited = False


def inhibitdodrive():
    g.senddriveinhibited = True


def keepspeed():
    outspeedi = 0

    # 0 to 9
    speeds = [0, 7, 11, 15, 19, 23, 27, 37, 41, 45, 49,
              # 93 to 100 haven't been run yet
              53, 57, 73, 77, 81, 85, 89, 93, 97, 100]

    while True:
        time.sleep(0.1)

        if g.outspeedcm is None:
            continue

        spi = outspeedi

        desiredspeed = g.outspeedcm

        if g.limitspeed is not None and desiredspeed > g.limitspeed:
            desiredspeed = g.limitspeed

        if g.user_pause:
            desiredspeed = 0

        if desiredspeed > g.finspeed:
            if spi < len(speeds) - 1:
                spi += 1
        elif desiredspeed < g.finspeed:
            if spi > 0:
                spi -= 1

        desiredspeed_sign = sign(desiredspeed)
        desiredspeed_abs = abs(desiredspeed)
        if True:
            # bypass the control
            spi = int(desiredspeed_abs / 10)
            if spi > len(speeds) - 1:
                spi = len(speeds) - 1
            sleeptime = 1
        else:
            sleeptime = 3

        sp = speeds[spi]
        outspeedi = spi
        # spi/outspeedi don't remember the sign

        sp *= desiredspeed_sign

        if abs(sp) >= 7:
            g.speedtime = time.time()
        else:
            g.speedtime = None

        if g.outspeed == sp and sp != 0:
            continue

        g.outspeed = sp

        if sp != 0 and not g.braking:
            g.speedsign = sign(sp)

        if sp != 0:
            nav_signal.warningblink(False)

        st = g.steering

        tolog("motor %d steer %d" % (sp, st))
        pi.driving.dodrive(sp, st)
        time.sleep(sleeptime)



