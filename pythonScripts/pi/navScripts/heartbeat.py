def heartbeat():
    maxdiff = 1
    while True:
        g.heartn += 1
        diff = g.heartn - g.heartn_r
        if diff < maxdiff:
            if maxdiff > 2:
                print("heart %d %d: %d" % (g.heartn, g.heartn_r, maxdiff))
            maxdiff = 1
        elif diff > maxdiff:
            maxdiff = diff
            if maxdiff % 50 == 0:
                print("heart %d %d: %d" % (g.heartn, g.heartn_r, maxdiff))

        nav_tc.send_to_ground_control(
            "heart %.3f %d" % (time.time()-g.t0, g.heartn))

        if g.heartn-g.heartn_r > 1:
            tolog("waiting for heart echo %d %d" % (g.heartn, g.heartn_r))

        if g.heartn-g.heartn_r > 3:
            if g.limitspeed0 == "notset":
                tolog("setting speed to 0 during network pause")
                g.limitspeed0 = g.limitspeed
                g.limitspeed = 0.0

        if g.heartn-g.heartn_r < 2:
            if g.limitspeed0 != "notset":
                tolog("restoring speed limit to %s after network pause" % (str(g.limitspeed0)))
                g.limitspeed = g.limitspeed0
                g.limitspeed0 = "notset"

        time.sleep(0.1)

def heartbeat2():
    while True:

        if g.tctime != None:
            tdiff = time.time() - g.tctime
            print(tdiff)
            if tdiff > 0.2:
                if g.limitspeed0 == "notset":
                    tolog("setting speed to 0 during network pause")
                    g.limitspeed0 = g.limitspeed
                    g.limitspeed = 0.0

            else:
                if g.limitspeed0 != "notset":
                    tolog("restoring speed limit to %s after network pause" % (str(g.limitspeed0)))
                    g.limitspeed = g.limitspeed0
                    g.limitspeed0 = "notset"

        time.sleep(0.05)