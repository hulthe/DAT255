import nav_signal
import pi
import nav_comm
import json
import time
import nav_tc
import socket


def connect_to_ecm():
    stopped = False

    s = nav_comm.open_socket2()

    ecmt0 = time.time()

    counter = 0
    g.crashacc = None

    while True:
        if g.crashacc:
            if not stopped:
                counter = 9
                print("crash: %f" % g.crash)
                g.remote_control = True
                pi.driving.drive(0)
                nav_signal.warningblink(True)
                stopped = True
                s112 = nav_comm.open_socket3()
                sstr = ('accident %f %f %f %s\n' % (
                    g.ppx, g.ppy, g.crashacc, g.VIN)).encode("ascii")
                print(sstr)
                s112.send(sstr)
                print(sstr)
                resp = s112.recv(1024)
                print(resp)
                resp = resp.decode("ascii")
                print(resp)
                if True:
                    resp = json.loads(resp)
                    print(resp)
                    say = resp['speak']
                else:
                    say = resp
                print(say)
                speak(say)
                s112.close()

        if True:
            t = time.time()
            if t - ecmt0 > 1.0:
                g.crashacc = 0.0
                if g.crash:
                    g.crashacc = g.crash
                    nav_tc.send_to_ground_control(
                        "message crash %f" % g.crashacc)
                s.send(('{"crash":%d, "x":%f, "y":%f, "crashacc":%f}\n' % (
                    counter, g.ppx, g.ppy, g.crashacc)).encode("ascii"))
                ecmt0 = t
        time.sleep(0.05)
        if counter != 9:
            counter = (counter + 1) % 8


g.heartn = -1
g.heartn_r = -1

g.canSocket = None


def initializeCAN(network):
    """
    Initializes the CAN network, and returns the resulting socket.
    """
    # create a raw socket and bind it to the given CAN interface
    s = socket.socket(socket.AF_CAN, socket.SOCK_RAW, socket.CAN_RAW)
    s.bind((network,))
    return s