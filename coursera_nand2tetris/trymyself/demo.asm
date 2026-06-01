


@0
D=M

@1
D=D+M

@2
M=D

@6
0;JMP


// If else
@R0
D=M

@POSITIVE
D;JGT

@R1
M=0
@END
0;JMP

(POSITIVE)
@R1
M=1

(END)
@END
0;JMP


// Variables
@R1
D=M
@temp
M=D

@R0
D=M
@R1
M=D

@temp
D=M
@R0
M=D

(END)
@END
0;JMP


// Loop
@R0
D=M
@n
M=D
@i
M=1
@sum
M=0

(LOOP)
    @i
    D=M
    @n
    D=D-M
    @STOP
    D;JGT

    @sum
    D=M
    @i
    D=D+M
    @sum
    M=D

    @i
    M=M+1
    @LOOP
    0;JMP

(STOP)
    @sum
    D=M
    @R1
    M=D


// Pointer
@100
D=A
@arr
M=D

@10
D=A 
@n
M=D

@i 
M=0

(LOOP)
    @i 
    D=M
    @n
    D=D-M
    @END
    D;JEQ

    @arr
    D=M
    @i
    A=D+M
    M=-1

    @i 
    M=M+1

    @LOOP
    0;JMP

(END)
    @END
    0;JMP



@SCREEN
D=A 
@addr
M=D

@0
D=M
@n
M=D

@i 
M=0

(LOOP)
    @i 
    D=M
    @n 
    D=D-M
    @END
    D;JGT
    

    @addr
    A=M
    M=-1

    @i 
    M=M+1
    @32
    D=A
    @addr
    M=D+M
    @LOOP
    0;JMP

(END)
    @END
    0;JMP