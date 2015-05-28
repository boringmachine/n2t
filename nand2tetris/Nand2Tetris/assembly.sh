#/bin/bash
java -classpath bin Assembler.Assembler $1.asm
./bin2ascii.py $1.hack