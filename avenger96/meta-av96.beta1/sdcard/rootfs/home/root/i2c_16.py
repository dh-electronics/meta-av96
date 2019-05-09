#!/usr/bin/python3

import sys
import os


def usage( ):
  print( "Usage: i2c_16 <read/write> bus chip-addr byte-addr [value]" )
  sys.exit( 1 )


def read16( bus, chip, addr ):
  os.system( "echo i2cset -y %i 0x%02x 0x%02x 0x%02x" % (bus, chip, (addr >> 8), (addr & 0xff)) )
  os.system( "i2cset -y %i 0x%02x 0x%02x 0x%02x" % (bus, chip, (addr >> 8), (addr & 0xff)) )
  os.system( "echo i2cget -y %i 0x%02x" % (bus, chip) )
  os.system( "i2cget -y %i 0x%02x" % (bus, chip) )


def write16( bus, chip, addr, b_val ):
  os.system( "echo i2cset -y %i 0x%02x 0x%02x 0x%04x w" % (bus, chip, \
             (addr >> 8), (addr & 0xff) | (b_val << 8) ) )
  os.system( "i2cset -y %i 0x%02x 0x%02x 0x%04x w" % (bus, chip, \
             (addr >> 8), (addr & 0xff) | (b_val << 8) ) )


if len( sys.argv ) < 5 :
  usage()


if sys.argv[1] == "read":
  read16( int(sys.argv[2],16), int(sys.argv[3],16), int(sys.argv[4],16) )
elif ( sys.argv[1] == "write" ) and ( len( sys.argv ) == 6 ) :
  write16( int(sys.argv[2],16), int(sys.argv[3],16), int(sys.argv[4],16), int(sys.argv[5],16) )
else :
  usage()

