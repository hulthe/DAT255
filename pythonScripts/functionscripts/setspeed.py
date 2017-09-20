#lumo: Skapar speed som (andra) input (eftersom funktionens namn räknas som första) och kastar in den i speed. Allt innan speed är kopierat från run.py

import sys
import nav as n
from nav import *
from nav1 import whole4, pause, cont
from driving import stop, drive, steer
init()

speed = sys.argv[1]
drive(speed)
