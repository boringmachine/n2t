@256
D=A
@SP
M=D
@return-address0
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@SP
AM=M+1
A=A-1
M=D
@ARG
D=M
@SP
AM=M+1
A=A-1
M=D
@THIS
D=M
@SP
AM=M+1
A=A-1
M=D
@THAT
D=M
@SP
AM=M+1
A=A-1
M=D
@5
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Sys.init
0;JMP
(return-address0)
(INF_LOOP)
@INF_LOOP
0;JMP
(Class2.set)
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
A=A-1
M=D
@Class2.vm.0
D=A
@R15
M=D
@SP
AM=M-1
D=M
@R15
A=M
M=D
@1
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
A=A-1
M=D
@Class2.vm.1
D=A
@R15
M=D
@SP
AM=M-1
D=M
@R15
A=M
M=D
@0
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@R14
M=D
@5
D=A
@R14
A=M-D
D=M
@R13
M=D
@SP
AM=M-1
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@R14
AM=M-1
D=M
@THAT
M=D
@R14
AM=M-1
D=M
@THIS
M=D
@R14
AM=M-1
D=M
@ARG
M=D
@R14
AM=M-1
D=M
@LCL
M=D
@R13
A=M
0;JMP
(Class2.get)
@Class2.vm.0
D=M
@SP
AM=M+1
A=A-1
M=D
@Class2.vm.1
D=M
@SP
AM=M+1
A=A-1
M=D
@SP
AM=M-1
D=M
@SP
AM=M-1
A=M
D=A-D
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@R14
M=D
@5
D=A
@R14
A=M-D
D=M
@R13
M=D
@SP
AM=M-1
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@R14
AM=M-1
D=M
@THAT
M=D
@R14
AM=M-1
D=M
@THIS
M=D
@R14
AM=M-1
D=M
@ARG
M=D
@R14
AM=M-1
D=M
@LCL
M=D
@R13
A=M
0;JMP
(Sys.init)
@6
D=A
@SP
AM=M+1
A=A-1
M=D
@8
D=A
@SP
AM=M+1
A=A-1
M=D
@return-address1
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@SP
AM=M+1
A=A-1
M=D
@ARG
D=M
@SP
AM=M+1
A=A-1
M=D
@THIS
D=M
@SP
AM=M+1
A=A-1
M=D
@THAT
D=M
@SP
AM=M+1
A=A-1
M=D
@7
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Class1.set
0;JMP
(return-address1)
@SP
AM=M-1
D=M
@5
M=D
@23
D=A
@SP
AM=M+1
A=A-1
M=D
@15
D=A
@SP
AM=M+1
A=A-1
M=D
@return-address2
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@SP
AM=M+1
A=A-1
M=D
@ARG
D=M
@SP
AM=M+1
A=A-1
M=D
@THIS
D=M
@SP
AM=M+1
A=A-1
M=D
@THAT
D=M
@SP
AM=M+1
A=A-1
M=D
@7
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Class2.set
0;JMP
(return-address2)
@SP
AM=M-1
D=M
@5
M=D
@return-address3
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@SP
AM=M+1
A=A-1
M=D
@ARG
D=M
@SP
AM=M+1
A=A-1
M=D
@THIS
D=M
@SP
AM=M+1
A=A-1
M=D
@THAT
D=M
@SP
AM=M+1
A=A-1
M=D
@5
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Class1.get
0;JMP
(return-address3)
@return-address4
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@SP
AM=M+1
A=A-1
M=D
@ARG
D=M
@SP
AM=M+1
A=A-1
M=D
@THIS
D=M
@SP
AM=M+1
A=A-1
M=D
@THAT
D=M
@SP
AM=M+1
A=A-1
M=D
@5
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Class2.get
0;JMP
(return-address4)
(WHILE)
@WHILE
0;JMP
(Class1.set)
@0
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
A=A-1
M=D
@Class1.vm.0
D=A
@R15
M=D
@SP
AM=M-1
D=M
@R15
A=M
M=D
@1
D=A
@ARG
A=M+D
D=M
@SP
AM=M+1
A=A-1
M=D
@Class1.vm.1
D=A
@R15
M=D
@SP
AM=M-1
D=M
@R15
A=M
M=D
@0
D=A
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@R14
M=D
@5
D=A
@R14
A=M-D
D=M
@R13
M=D
@SP
AM=M-1
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@R14
AM=M-1
D=M
@THAT
M=D
@R14
AM=M-1
D=M
@THIS
M=D
@R14
AM=M-1
D=M
@ARG
M=D
@R14
AM=M-1
D=M
@LCL
M=D
@R13
A=M
0;JMP
(Class1.get)
@Class1.vm.0
D=M
@SP
AM=M+1
A=A-1
M=D
@Class1.vm.1
D=M
@SP
AM=M+1
A=A-1
M=D
@SP
AM=M-1
D=M
@SP
AM=M-1
A=M
D=A-D
@SP
AM=M+1
A=A-1
M=D
@LCL
D=M
@R14
M=D
@5
D=A
@R14
A=M-D
D=M
@R13
M=D
@SP
AM=M-1
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@R14
AM=M-1
D=M
@THAT
M=D
@R14
AM=M-1
D=M
@THIS
M=D
@R14
AM=M-1
D=M
@ARG
M=D
@R14
AM=M-1
D=M
@LCL
M=D
@R13
A=M
0;JMP
