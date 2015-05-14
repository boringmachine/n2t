#/bin/bash
java -classpath bin Assembler.Assembler Asm/$1.asm
./bin2ascii.py $1.hack
