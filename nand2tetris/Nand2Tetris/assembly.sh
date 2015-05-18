#/bin/bash
java -classpath bin Assembler.Assembler Asm/$1.asm
./bin2ascii.py Hack/$1.hack
