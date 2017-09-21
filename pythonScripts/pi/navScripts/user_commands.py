from math import pi
from nav_util import start_new_thread
import nav_signal
import time
import nav1
import nav2
import nav_tc


# ============================================================
# User commands

def initpos():
    g.markercnt = 0
    g.angleknown = False
    g.crash = False
    g.crashacc = None
    g.remote_control = False


def reset():
    g.remote_control = False
    g.rc_button = False
    g.warningblinking = False
    nav_signal.setleds(0, 7)
    g.markerno = 0
    g.markercnt = 0
    g.angleknown = False
    g.crash = False
    g.crashacc = None


global perc


def gooval(perc0):
    start_new_thread(goovalaux, (perc0,))


def goovalaux(perc0):
    global perc

    perc = perc0

    pi.driving.drive(0)
    time.sleep(4)
    sp = 15
    pi.driving.drive(sp)

    while True:
        print("1")
        pi.driving.steer(0)
        print("2")
        nav2.goto_1(1.9, 17)
        print("3")
        print("marker %s" % (str(g.lastpos)))
        pi.driving.steer(-100)
        print("4")
        # 250 comes from pi*80 (cm)
        # it's the outer radius, but so is the speed we get
        print("finspeed1 %f dang1 %f ang1 %f" % (g.finspeed, g.dang, g.ang % 360))
        time.sleep(250.0 / g.finspeed * perc)
        print("finspeed2 %f dang2 %f ang2 %f" % (g.finspeed, g.dang, g.ang % 360))
        pi.driving.steer(0)
        print("5")
        nav2.goto_1(0.5, 13)
        print("6")
        pi.driving.steer(-100)
        time.sleep(250.0 / g.finspeed * perc)


def go1():
    g.lev = 0
    print(g.ppx, g.ppy, g.ang % 360)
    g.goodmarkers = [(7, 'all', 0.6), (25, 'all', 0.6), (22, 'all', 0.65), (2, 'all', 0.45)]
    g.goodmarkers = []
    nav1.whole4()


def stop1():
    pi.driving.drive(0)
    time.sleep(3)
    print(g.ppx, g.ppy, g.ang % 360)
    g.goodmarkers = [(7, 'all', 0.6), (25, 'all', 0.6), (22, 'all', 0.65), (2, 'all', 0.45)]
    time.sleep(3)
    print((g.ppx, g.ppy, g.ang % 360))
    g.goodmarkers = []
    pi.driving.drive(20)


def m1():
    g.goodmarkers = [(7, 'all', 0.6), (25, 'all', 0.6), (22, 'all', 0.65), (2, 'all', 0.45)]


def m2():
    g.goodmarkers = [(7, 'all', 0.45), (25, 'all', 0.6), (22, 'all', 0.65), (2, 'all', 0.45)]


def m3(q=0.5):
    g.goodmarkers = None
    g.minquality = q


def wait1():
    nav_tc.send_to_ground_control("waitallcars\n")
    x = g.queue.get()
    g.queue.task_done()


def follow():
    speeds = [0, 7, 11, 15, 19, 23, 27, 37, 41, 45, 49,
              # 93 to 100 haven't been run yet
              53, 57, 73, 77, 81, 85, 89, 93, 97, 100]

    sp = None
    while True:
        oldsp = sp
        x = (g.finspeed - 10) / 2
        sp = x - 10
        if sp < 0:
            sp = 0
        print("%f %f" % (g.finspeed, sp))
        if sp > 25:
            sp = 0
        if sp != oldsp:
            pi.driving.drive(sp)
        time.sleep(0.5)


def auto():
    # I'd like to light LEDs here, but maybe the LEDlight plugin
    # hasn't started yet.
    pi.driving.drive(0)
    while not g.ground_control:
        time.sleep(3)
    pi.driving.steer(70)
    time.sleep(0.5)
    pi.driving.steer(-70)
    m3(0.4)
    while True:
        ang = g.ang % 360
        if ang > 180:
            ang -= 360
        print("pos %f %f %f" % (g.ppx, g.ppy, ang))
        if (abs(g.ppx - 2.5) < 0.5 and
            abs(g.ppy - 14.2) < 0.5 and
                abs(ang) < 30):
            break
        time.sleep(1)

    pi.driving.steer(0)
    m1()
    nav1.whole4()

    while True:
        time.sleep(100)
