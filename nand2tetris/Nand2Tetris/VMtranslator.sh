#/bin/bash
java -classpath bin VMtranslator.VMtranslator $1
file=${1%.vm}
./assembly.sh $file