@R0
D=M
@n
M=D
@i 
M=0
@R2 
M=0

(LOOP)
    @i  
    D=M
    @n
    D=D-M
    @END
    D;JGT

    @R1
    D=M
    @R2
    M=D+M
    @i 
    M=M+1
    @LOOP
    0;JMP

(END)
0;JMP