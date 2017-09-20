from Tkinter import *

import thread
import time
import math

totalcars = 0
colours = ["blue", "red", "green", "yellow"]

cars = dict()

d = dict()

occupied = dict()
waiting = dict()

show_markpos = False
show_markpos1 = False

currentcar = None
currentcarcircle = None

currentmark = None
currentmarkpos = None

global markedpoint
markedpoint = None

global event_nr

event_nr = 0

areaobjs = []

pathlist = []
global currentpath

obstacles = dict()

class Car:
    def __init__(self):
        
        ns = []
        for ci in cars:
            c = cars[ci]
            ns.append(c.n)
        i = 0
        while i in ns:
            i += 1
        self.n = i

        self.oldposlist = []

        self.objs = []

        self.currentpos = None
        self.info = None
        self.colour = colours[self.n]
        self.battery_seen = 0
        self.heart_seen = time.time()

        self.alive = True

        self.waitingat = None

        cars[self.n] = self

        self.windows = []

        v = StringVar()
        v.set("hej")
        tx = Label(w, textvariable=v, bg="white", fg="black")
        ww = w.create_window(graphw+20, 100*i+50, window=tx)
        v.set("hopp")
        self.v = v
        self.windows.append(ww)

        v2 = StringVar()
        v2.set("hej")
        tx2 = Label(w, textvariable=v2, bg="white", fg="black")
        ww = w.create_window(graphw+20, 100*i+70, window=tx2)
        v2.set("hopp")
        self.v2 = v2
        self.windows.append(ww)

        v3 = StringVar()
        v3.set("hej")
        tx3 = Label(w, textvariable=v3, bg="white", fg=colours[i])
        ww = w.create_window(graphw+20, 100*i+90, window=tx3)
        v3.set("hopp")
        self.v3 = v3
        self.windows.append(ww)

        v4 = StringVar()
        v4.set("hej")
        tx4 = Label(w, textvariable=v4, bg="white", fg="black")
        ww = w.create_window(graphw+20, 100*i+110, window=tx4)
        v4.set("unknown")
        self.v4 = v4
        self.windows.append(ww)

path1 = [('go', 7, 1.8, 16.2),
         ('speak', "#"),
         ('go', 7, 2.3, 17.5),
         ('go', 7, 1.4, 19),
         ('speak', 'loading'),
         ('stop',),
         ('speak', 'loading done'),
         ('go', 7, 0.6, 18),
         ('go', 7, 0.8, 14.0),
         ('stop', 0.5),
         ('go', -10, 2.5, 14.6),
         ('speak', 'dumping'),
         ('stop',),
         ('speak', 'dumping done')]

def draw_path(p):
    first = True

    for cmd in p:
        if cmd[0] == 'go':
            x = cmd[2]
            y = cmd[3]

            if not first:
                l = addline2(w, x1, y1, x, y)
                pathlist.append(l)
            else:
                (x0, y0) = (x, y)
                first = False
            (x1, y1) = (x, y)

    l = addline2(w, x1, y1, x0, y0)
    pathlist.append(l)

def list_obstacles():
    print obstacles.keys()

def add_obstacle(x, y):
    px = xcoord(x)
    py = ycoord(y)
    w.create_oval(px-1, py-1, px+1, py+1)

def save_obstacle():
    if currentmark:
        (x, y) = currentmarkpos
        obstacles[currentmarkpos] = True
        add_obstacle(x, y)

def update_carpos1(x, y, ang, c):
    global event_nr
    event_nr += 1
    d[event_nr] = ("pos", x, y, ang, c)
    w.event_generate("<<CarPos>>", when="tail", x=event_nr)

def set_markerpos(x, y, c):
    global event_nr
    event_nr += 1
    d[event_nr] = ("mpos", x, y, c)
    w.event_generate("<<CarPos>>", when="tail", x=event_nr)

def set_badmarkerpos(x, y, c):
    global event_nr
    event_nr += 1
    d[event_nr] = ("badmarker", x, y, c)
    w.event_generate("<<CarPos>>", when="tail", x=event_nr)

def update_mark(x, y):
    global event_nr
    event_nr += 1
    d[event_nr] = ("mark", x, y)
    w.event_generate("<<CarPos>>", when="tail", x=event_nr)

def esend_continue(c):
    c.conn.send("continue")

scale = 30

xoffset = 0
yoffset = 0

# minx and miny should also be involved here, but they are 0 right now
def coordx(x):
    return float(x-10-xoffset)/scale

def coordy(y):
    return float(winh-y-10-yoffset)/scale

def xcoord(x):
    return scale*x + 10 + xoffset

def ycoord(y):
    return winh - (scale*y + 10 + yoffset)


def addline(w, x1, y1, x2, y2):
    px1 = xcoord(x1)
    px2 = xcoord(x2)
    py1 = ycoord(y1)
    py2 = ycoord(y2)
    return w.create_line(px1, py1, px2, py2)

def addline2(w, x1, y1, x2, y2):
    px1 = xcoord(x1)
    px2 = xcoord(x2)
    py1 = ycoord(y1)
    py2 = ycoord(y2)
    return w.create_line(px1, py1, px2, py2, fill="#bbffbb", width=8)

def addcircle(w, x0, y0, r, colour="black"):
    x1 = x0 - r*math.sqrt(2)
    x2 = x0 + r*math.sqrt(2)
    y1 = y0 - r*math.sqrt(2)
    y2 = y0 + r*math.sqrt(2)
    px1 = xcoord(x1)
    px2 = xcoord(x2)
    py1 = ycoord(y1)
    py2 = ycoord(y2)
    return w.create_oval(px1, py1, px2, py2, outline=colour)

def draw_lines(w, lst, closed):
    global areaobjs

    if lst == []:
        return
    (x1, y1) = lst[0]
    for (x2, y2) in lst[1:]:
        l = addline(w, x1, y1, x2, y2)
        areaobjs.append(l)
        (x1, y1) = (x2, y2)
    if closed:
        l = addline(w,
                    lst[-1][0], lst[-1][1],
                    lst[0][0], lst[0][1])
        areaobjs.append(l)

x2 = 3.0
y2 = 19.7

area1 = [(7, 1),
         (7, 4.3),
         (x2, 4.3),
         (x2, 0),
         (0, 0),
         (0, y2),
         (x2, y2),
         (x2, 19.0),
         (7, 19.0),
         (7, 20.0)]

area2 = [(x2, 17.2),
         (7, 17.2),
         (7, 13.8),
         (3.4, 13.8),
         (3.4, 13.5),
         (x2, 13.5)]

area3 = [(x2, 12.0),
         (7, 12.0),
         (7, 6.0),
         (x2, 6.0)]

area = [(area1, False),
        (area2, True),
        (area3, True)]

def draw_area(w):
    for (a, closed) in area:
        draw_lines(w, a, closed)
    d = 0.72-0.28
    # flower pot:
    #addcircle(w, 3-(0.72+0.28)/2, 9.5, d/2)
    if False:
        addcircle(w, 2.0, 16.6, 0.6)
        addcircle(w, 1.8, 16.0, 0.6)
        addcircle(w, 1.8, 15.6, 0.6)

    if False:
        addcircle(w, 1.7, 17.05, 0.04)
        addcircle(w, 1.48, 16.6, 0.04)
        addcircle(w, 1.35, 16.1, 0.04)
        addcircle(w, 1.48, 15.1, 0.04)

    draw_path(currentpath)

def send_go(carno):
    c = cars[carno-1]
    if currentmark:
        (x, y) = currentmarkpos
        c.conn.send("go %f %f" % (x, y))

def send_path(carno, p):
    c = cars[carno-1]
    c.conn.send("path " + str(p))

def toggle_show_markpos():
    global show_markpos
    show_markpos = not show_markpos
    print("toggle show_markpos -> %s" % str(show_markpos))

def toggle_show_markpos1():
    global show_markpos1
    show_markpos1 = not show_markpos1
    print("toggle show_markpos1 -> %s" % str(show_markpos1))

def clear():
    for ci in cars:
        c = cars[ci]
        for win in c.objs:
            w.delete(win)
        # can objects be put in the list while we are doing this?
        c.objs = []

def redraw_area():
    global areaobjs

    for obj in areaobjs:
        w.delete(obj)
    areaobjs = []

    for obj in pathlist:
        w.delete(obj)
    areaobjs = []

    clear()
    draw_area(w)

def zoom(amount):
    global scale
    global xoffset, yoffset

    p = math.pow(1.5, amount)
    scale *= p

    yoffset = winh-uppermargin-p*(winh-uppermargin-yoffset)

    redraw_area()

def shift(x, y):
    global xoffset, yoffset

    xoffset += x*50
    yoffset += y*50
    redraw_area()

def key_event(event):
    global currentcar

    if event.keysym == "Left":
        shift(-1, 0)
    elif event.keysym == "Right":
        shift(1, 0)
    elif event.keysym == "Up":
        shift(0, 1)
    elif event.keysym == "Down":
        shift(0, -1)
    elif event.char == 'q':
        exit(0)
    elif event.char == 'c':
        clear()
    elif event.char == '+':
        zoom(1)
    elif event.char == '-':
        zoom(-1)
    elif event.char == 'r':
        remove_markedpoint()
    elif event.char == 'n':
        new_pathpoint()
    elif event.char == 'g':
        send_go(currentcar)
    elif event.char == 'm':
        toggle_show_markpos()
    elif event.char == 'M':
        toggle_show_markpos1()
    elif event.char == 'p':
        send_path(currentcar, currentpath)
    elif event.char == 'o':
        save_obstacle()
    elif event.char == 'O':
        list_obstacles()
    elif event.char == '1':
        select_car(1)
    elif event.char == '2':
        select_car(2)
    else:
        print "unknown command (%s) (%s)" % (event.char, event.keysym)

def select_car(i):
    global currentcar, currentcarcircle

    if i == currentcar:
        return

    if currentcarcircle != None:
        w.delete(currentcarcircle)

    currentcar = i
    cx = graphw + 20
    cy = 20
    currentcarcircle = w.create_oval(cx-10, cy-10, cx+10, cy+10,
                                     fill=colours[i-1],
                                     outline=colours[i-1])

def button1_event(event):
    (px, py) = (event.x, event.y)
    print (coordx(px), coordy(py))
    update_mark(coordx(px), coordy(py))

def dist(x1, y1, x2, y2):
    return math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))

def button2_event(event):
    global markedpoint

    (px, py) = (event.x, event.y)
    (x, y) = (coordx(px), coordy(py))
    d = 100000
    cmd1 = None
    i = 0
    for cmd in currentpath:
        if cmd[0] == 'go':
            x1 = cmd[2]
            y1 = cmd[3]
            if d > dist(x, y, x1, y1):
                d = dist(x, y, x1, y1)
                markedpoint = i
        i += 1
    print "markedpoint %d" % markedpoint

def button3_event(event):
    global markedpoint

    (px, py) = (event.x, event.y)
    (x, y) = (coordx(px), coordy(py))
    if markedpoint == None:
        return

    cmd1 = currentpath[markedpoint]
    cmd2 = ('go', cmd1[1], x, y)
    currentpath[markedpoint] = cmd2
    markedpoint = None
    redraw_area()    

def new_pathpoint():
    global markedpoint

    # NOT READY

    i = 0
    last = None
    for cmd in currentpath:
        if cmd[0] == 'go':
            x1 = cmd[2]
            y1 = cmd[3]
            if d > dist(x, y, x1, y1):
                d = dist(x, y, x1, y1)
                markedpoint = i
        i += 1

    sp = 7

    cmd = ('go', sp, x, y)
    currentpath[markedpoint:markedpoint] = [cmd]

    markedpoint = None
    redraw_area()

def remove_markedpoint():
    global markedpoint

    currentpath[markedpoint:markedpoint+1] = []

    markedpoint = None
    redraw_area()

def double_event(event):
    print event
    exit(0)

def carpos_event(event):
    global currentmark, currentmarkpos

    i = event.x
    ev = d[i]
    del d[i]
    
    if ev[0] == "pos":
        (type, x0, y0, ang, c) = ev
        dx1 = 0.2*math.cos(math.pi/180*ang) + 0.3*math.sin(math.pi/180*ang)
        dy1 = -0.2*math.sin(math.pi/180*ang) + 0.3*math.cos(math.pi/180*ang)

        dx2 = -0.2*math.cos(math.pi/180*ang) + 0.3*math.sin(math.pi/180*ang)
        dy2 = 0.2*math.sin(math.pi/180*ang) + 0.3*math.cos(math.pi/180*ang)

        x01 = x0+dx1
        y01 = y0+dy1

        x02 = x0+dx2
        y02 = y0+dy2

        x03 = x0-dx1
        y03 = y0-dy1

        x04 = x0-dx2
        y04 = y0-dy2

        x = xcoord(x0)
        y = ycoord(y0)
        x1 = xcoord(x01)
        y1 = ycoord(y01)
        x2 = xcoord(x02)
        y2 = ycoord(y02)
        x3 = xcoord(x03)
        y3 = ycoord(y03)
        x4 = xcoord(x04)
        y4 = ycoord(y04)
        r1 = w.create_line(x1, y1, x2, y2, fill=c.colour)
        r2 = w.create_line(x2, y2, x3, y3, fill=c.colour)
        r3 = w.create_line(x3, y3, x4, y4, fill=c.colour)
        r4 = w.create_line(x4, y4, x1, y1, fill=c.colour)
        r5 = w.create_line((x2+x3)/2,(y2+y3)/2,(x1+x2)/2,(y1+y2)/2, fill=c.colour)
        r6 = w.create_line((x1+x4)/2,(y1+y4)/2,(x1+x2)/2,(y1+y2)/2, fill=c.colour)
        if c.currentpos != None:
            (or1, or2, or3, or4, or5, or6) = c.currentpos
            w.delete(or1)
            w.delete(or2)
            w.delete(or3)
            w.delete(or4)
            w.delete(or5)
            w.delete(or6)
        c.currentpos = (r1, r2, r3, r4, r5, r6)
#        r1 = w.create_line(x-2, y-2, x+2, y+2)
#        r2 = w.create_line(x-2, y+2, x+2, y-2)
        win = w.create_line(x, y, x+1, y, fill=c.colour)
        c.objs.append(win)

    elif ev[0] == "mpos":
        (type, x, y, c) = ev
        win = addcircle(w, x, y, 2.0/scale, "blue")
        c.objs.append(win)

    elif ev[0] == "badmarker":
        (type, x, y, c) = ev
        win = addcircle(w, x, y, 2.0/scale, "red")
        c.objs.append(win)

    elif ev[0] == "mark":
        (type, x0, y0) = ev
        x = xcoord(x0)
        y = ycoord(y0)
        r1 = w.create_line(x-2, y-2, x+2, y+2)
        r2 = w.create_line(x-2, y+2, x+2, y-2)
        if currentmark != None:
            (or1, or2) = currentmark
            w.delete(or1)
            w.delete(or2)
        currentmark = (r1, r2)
        currentmarkpos = (x0, y0)
    else:
        print "unknown event %s" % ev

minx = None
maxx = None
miny = None
maxy = None

for (a, closed) in area:
    for (x, y) in a:
        if minx == None or minx > x:
            minx = x
        if maxx == None or maxx < x:
            maxx = x
        if miny == None or miny > y:
            miny = y
        if maxy == None or maxy < y:
            maxy = y


uppermargin = 20

winh = scale*(maxy-miny) + uppermargin
graphw = scale*(maxx-minx) + 20
winw = graphw + 200

w = Canvas(width=winw, height=winh, bg='white')
w.pack(expand=YES, fill=BOTH)


currentpath = path1
draw_area(w)


#widget = Label(w, text='AAA', fg='white', bg='black')
#widget.pack()
#w.create_window(100, 100, window=widget)

w.bind("<Button-1>", button1_event)
w.bind("<Button-2>", button2_event)
w.bind("<Button-3>", button3_event)
w.bind("<Key>", key_event)
w.bind("<<CarPos>>", carpos_event)
#w.bind("<Double-1>", double_event)

#start_obstacles = [(0.6, 0.6666666666666666), (2.5, 3.966666666666667), (2.3, 3.7666666666666666), (2.1, 13.566666666666666)]
start_obstacles = []

for point in start_obstacles:
    obstacles[point] = True
    (x, y) = point
    add_obstacle(x, y)
    

w.focus_set()

def linesplit(socket):
    buffer = socket.recv(4096)
    buffering = True
    while buffering:
        if "\n" in buffer:
            (line, buffer) = buffer.split("\n", 1)
            yield line + "\n"
        else:
            more = socket.recv(4096)
            if not more:
                buffering = False
            else:
                buffer += more
    if buffer:
        yield buffer


import socket, time

HOST = ''                 # Symbolic name meaning all available interfaces
PORT = 50008              # Arbitrary non-privileged port

def handlebattery(c):
    while True:
        if time.time() > c.battery_seen + 20:
            c.v4.set("battery unknown")
        time.sleep(1)

def deletecar(c):
    c.alive = False

    if c.currentpos != None:
        (or1, or2, or3, or4, or5, or6) = c.currentpos
        w.delete(or1)
        w.delete(or2)
        w.delete(or3)
        w.delete(or4)
        w.delete(or5)
        w.delete(or6)
    for win in c.windows:
        w.delete(win)
    del cars[c.n]


def handleheart(c, conn):
    while True:
        if time.time() > c.heart_seen + 60:
            print("timed out: " + c.info)
            deletecar(c)
            return
        time.sleep(5)


def handlerun(conn, addr):
    dataf = linesplit(conn)
    print 'Connected %s (at %f)' % (addr, time.time())

    c = Car()

    c.conn = conn

    thread.start_new_thread(handlebattery, (c,))
    thread.start_new_thread(handleheart, (c, conn))

    # in case the car waited for us to start
    esend_continue(c)

    for data in dataf:

        if not c.alive:
            conn.close()
            return

        if data[-1] == "\n":
            data = data[:-1]

        #print "received (%s)" % data
        l = data.split(" ")
        # mpos = from marker; d = from dead reckoning
        if l[0] == "mpos" or l[0] == "dpos":
            x = float(l[1])
            y = float(l[2])
            ang = float(l[3])
            time1 = float(l[4])
            c.v2.set("time %.2f" % time1)
            if l[0] == "dpos" or l[0] == "mpos" and show_markpos:
                update_carpos1(x, y, ang, c)
            if l[0] == "mpos" and show_markpos1:
                set_markerpos(x, y, c)
        elif l[0] == "badmarker":
            x = float(l[1])
            y = float(l[2])
            set_badmarkerpos(x, y, c)
        elif l[0] == "odometer":
            odo = int(l[1])
            c.v.set("%d pulses = %.2f m" % (odo, float(odo)/5*math.pi*10.2/100))
        elif l[0] == "info":
            car = l[1]
            c.v3.set("car %s" % car)
            c.info = car
            print("car %s" % car)
        elif l[0] == "heart":
            c.heart_seen = time.time()
        elif l[0] == "stopat":
            i = int(l[1])
            i -= 1
            print "%s stopped at %d" % (c.info, i)
            if i == 4 or i == 11 or i == 8:
                if i == 4:
                    j = 8
                elif i == 8:
                    j = 11
                elif i == 11:
                    j = 4

                print "occupied: %s" % str(occupied)
                print "waiting: %s" % str(waiting)
                for ci in cars:
                    carx = cars[ci]
                    print "%s %s" % (carx.info, str(carx.waitingat))

                if j not in occupied:
                    print " %d not occupied" % j
                    if i in occupied:
                        c1 = occupied[i]
                        print " %d was occupied by %s" % (i, c1.info)
                        del occupied[i]
                        if i in waiting:
                            print " %d was waited for" % i
                            c2 = waiting[i]
                            print " %s waited for %d" % (c2.info, i)
                            print " continuing %s" % c2.info
                            occupied[i] = c2
                            if c2.waitingat in occupied:
                                del occupied[c2.waitingat]
                            c2.waitingat = None
                            esend_continue(c2)
                            del waiting[i]
                    print " continuing %s" % c.info
                    esend_continue(c)
                    occupied[j] = c
                else:
                    print " %d occupied, waiting" % j
                    waiting[j] = c
                    print " waitingat %d" % i
                    c.waitingat = i
            else:
                esend_continue(c)

        elif l[0] == "battery":
            b = float(l[1])
            c.battery_seen = time.time()
            c.v4.set("battery %.3f" % b)
        else:
            print "received (%s)" % data

    conn.close()
    print("connection closed %d %s" % (c.n, c.info))
    deletecar(c)

def run():
    totdata = ""

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind((HOST, PORT))
    s.listen(1)
    while True:
        print 'Listening (at %f)' % time.time()
        (conn, addr) = s.accept()
        conn.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
        thread.start_new_thread(handlerun, (conn, addr))

thread.start_new_thread(run, ())

select_car(1)

mainloop()
