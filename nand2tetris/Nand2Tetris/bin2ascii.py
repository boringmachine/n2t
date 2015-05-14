#!/usr/bin/python
import sys, binascii
infile = "test.hack"
outfile = "test1.hack"

f = open(infile,"r")
g = open(outfile,"w+")
code = f.read()
code2 = binascii.b2a_hex(code)

def split_str(s, n):
    length = len(s)
    return [s[i:i+n] for i in range(0, length, n)]

parsed = ''.join(bin(int(c, 16))[2:].zfill(4) for c in code2)
parsed2 = split_str(parsed, 16)
for p in parsed2:
    g.write(p+"\n")    
g.close()
print(parsed2);

