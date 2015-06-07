#/bin/bash
file=${1%.jack}.xml
java -classpath bin Compiler.CompilationEngine $1 $file

