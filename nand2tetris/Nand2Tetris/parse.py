#!/usr/bin/python
import sys, binascii
infile = "aaa.hack"
outfile = "ddd.hack"

f = open(infile,"r")
g = open(outfile,"w+")
code = f.read()
code2 = binascii.b2a_hex(code)

def split_str(s, n):
    length = len(s)
    return [s[i:i+n] for i in range(0, length, n)]

parsed = bin(int(code2, 16))[2:].zfill(16)
parsed2 = split_str(parsed, 16)
firstcode = code2[0:4]
if(firstcode == "0000"): g.write("0000000000000000\n")
for p in parsed2:
    g.write(p+"\n")
    
g.close()


