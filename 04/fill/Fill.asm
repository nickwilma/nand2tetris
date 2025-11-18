// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
(KEYBOARD)
    @KBD
    D = M
    @WHITE
    D;JEQ
    @BLACK
    0;JMP

// black
(BLACK)
    @SCREEN
    D = A
    @10
    M = D - 1

    (LOOPBLACK)
        @10
        MD = M + 1
        A = D
        M = -1

        // jump out loop if max is reached
        @24575
        D = A
        @10
        D = D - M
        @LOOPBLACK
        D;JNE
        @KEYBOARD
        0;JMP

// white
(WHITE)
    @SCREEN
    D = A
    @11
    M = D - 1

    (LOOPWHITE)
        @11
        MD = M + 1
        A = D
        M = 0

        // jump out loop if max is reached
        @24575
        D = A
        @11
        D = D - M
        @LOOPWHITE
        D;JNE
        @KEYBOARD
        0;JMP