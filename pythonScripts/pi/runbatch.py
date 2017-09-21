from nav1 import whole4

from pi.navScripts.nav import *

init()
n = int(sys.argv[2])
time.sleep(1.0*n)
wm.putcar(2.5, 14, 0)
whole4()
