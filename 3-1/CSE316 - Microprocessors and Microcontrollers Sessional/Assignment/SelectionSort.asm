.MODEL SMALL 

.STACK 100h

.DATA
    ERROR_MSG DB 'INVALID INPUT',0DH,0AH,'$'
    TERMINATE_MSG DB 'PROGRAMME TERMINATED',0Dh,0Ah,'$'
    ERROR_FLAG DB 00h
    INPUT_END_FLAG DB 00h
    COUNT DB 00h
    
    ARRAY DW 16 DUP(?)


.CODE

INPUT_ PROC
        ; takes input and convert to binary
        ; input : none 
        ; output stored in AX
        
        ; save registers 
        PUSH BX
        PUSH CX
        PUSH DX
    
    _BEGIN:
         ;initialize variables
        XOR BX,BX  ;total
        XOR CX,CX  ;sign
               
        ;read character 
        MOV AH,1
        INT 21h
        
        CMP AL,'x'
        JE _INPUT_END
        CMP AL,'-'
        JE _MINUS
        CMP AL,'+'
        JE _PLUS
        JMP _REP
        
    _MINUS:
        MOV CX,1
    _PLUS:
        INT 21h
    _REP:
        ; check for valid character 0-9
        CMP AL,'0'
        JNGE _ERROR
        CMP AL,'9'
        JNLE _ERROR
        ; convert to digit
        AND AX,000Fh
        PUSH AX
        MOV AX,10
        MUL BX ; result stored in AX
        POP BX
        ADD BX,AX
        
        ;read next character
        MOV AH,1
        INT 21h
        CMP AL,' '
        JNE _REP
        
        MOV AX,BX
        CMP CX,0
        JZ _END
        NEG AX  
    _END:
        POP DX
        POP CX
        POP BX
        RET
    _INPUT_END:
        MOV INPUT_END_FLAG,01h
        POP DX
        POP CX
        POP BX
        RET 
    _ERROR:
        MOV AH,9
        LEA DX,ERROR_MSG
        INT 21h
        MOV ERROR_FLAG,01h
        POP DX
        POP CX
        POP BX 
        RET
INPUT_ ENDP 
         


OUTPUT_ PROC 
        ;prints the value of AX as decimal 
        ; input : AX
        ; output : none
        
        ;save registers
        PUSH AX
        PUSH BX
        PUSH CX
        PUSH DX
        
        CMP AX,0
        JGE _POSITIVE
        PUSH AX 
        MOV DL,'-'
        MOV AH,2
        INT 21h
        POP AX
        NEG AX
        
    _POSITIVE:
        XOR CX,CX
        MOV BX,10d
     
     _REPEAT:
        XOR DX,DX
        DIV BX
        PUSH DX
        INC CX
        OR AX,AX
        JNE _REPEAT
        
        MOV AH,2
 
    _PRINT_LOOP:
        POP DX
        OR DL,30h
        INT 21h
        LOOP _PRINT_LOOP
        
        POP DX
        POP CX
        POP BX
        POP AX
        
        RET
        
OUTPUT_ ENDP


PROC SORT_
        ; SI offset of array
        ; BX no of elements 
        
        ;save registers
        PUSH BX
        PUSH CX
        PUSH DX
        PUSH SI
        
        DEC BX
        JE _SORT_END
        MOV DX,SI
    
    _SORT_LOOP:
        MOV SI,DX
        MOV CX,BX
        MOV DI,SI
        MOV AX,[DI]
        
    _SMALL:
        ADD SI,2
        CMP [SI],AX
        JNL _NEXT 
        MOV DI,SI
        MOV AX,[DI]
    
    _NEXT:
        LOOP _SMALL:
        CALL SWAP_
        DEC BX
        JNE _SORT_LOOP
    
    _SORT_END:
        POP SI
        POP DX
        POP CX
        POP BX
        RET

SORT_ ENDP
    

SWAP_ PROC
    ; swaps elements which offset in DI , SI
        PUSH AX
        MOV AX,[SI]
        XCHG AX,[DI]
        MOV [SI],AX
        POP AX 
        RET
SWAP_ ENDP


MAIN PROC
         MOV AX,@DATA
         MOV DS,AX
         XOR CX,CX
         LEA SI,ARRAY
         PUSH SI 
         
     ;LOOP FOR INPUT
     _INPUT_LOOP:
         CALL INPUT_
         CMP INPUT_END_FLAG,01h
         JE _SORTING
         CMP ERROR_FLAG,01h
         JE _TERMINATE   
         INC CX     
         MOV [SI],AX
         ADD SI,2     
         JMP _INPUT_LOOP
      
     _SORTING: 
         POP SI
         MOV BX,CX
         CALL SORT_
         
     ;new line 
     MOV AH,2
     MOV DL,0Ah
     INT 21h
     MOV DL,0Dh
     INT 21h 
         
     _OUTPUT_LOOP:
         MOV AX,[SI]
         CALL OUTPUT_
         ADD SI,2
         MOV AH,2
         MOV DL,' '
         INT 21h
         LOOP _OUTPUT_LOOP
         JMP _EXIT
        
     _TERMINATE:
          MOV AH,9
          LEA DX,TERMINATE_MSG
          INT 21h
          
     _EXIT:
         ;return controll
         MOV AH,4Ch 
         INT 21h
     
MAIN ENDP
    END MAIN