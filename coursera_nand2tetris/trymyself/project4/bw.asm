if kbd > 0
    i = 0
    for i <= 8192
        M[SCREEN + i] = -1
else
    i = 0
    for i <= 8192
        M[SCREEN + i] = 0



(Check)
    @KBD
    D=M
    @BLACK
    D;JGT
    @WHITE
    0;JMP

(BLACK)
    @i
    M=0
    (LOOPBLACK)
        @8192
        D=A 
        @i 
        D=M-D
        @CHECK
        D;JGT


        @i 
        D=M
        @SCREEN 
        A=A+D
        M=-1

        @i
        M=M+1

        @LOOPBLACK
        0;JMP

(WHITE)
    @i
    M=0

    (LOOPWHITE)
    @8192
    D=A
    @i
    D=M-D
    @CHECK
    D;JGT


    @i 
    D=M
    @SCREEN 
    A=A+D
    M=0

    @i
    M=M+1

    @LOOPWHITE
    0;JMP



 @LCL
 D=M+1
 @addr
 M=D+1

 @sp
 M=M-1

 @sp
 D=M

 @addr
 A=M
 M=D
