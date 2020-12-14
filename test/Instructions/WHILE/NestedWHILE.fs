BEGINPROG NestedWHILE
    a := 10
    b := 10

    WHILE(b > 0) DO
        b := b-1
        WHILE (a > 1) DO
            a := a - 1
        ENDWHILE
    
    ENDWHILE
    PRINT(a)
    PRINT(b)
ENDPROG