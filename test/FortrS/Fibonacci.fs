BEGINPROG Fibonacci

/*Computes and outputs a desired number of elements in
the Fibonacci sequence (in addition to the 2 first elements 0 and 1, 
so i is minimum 2) */

    READ(num)
    n1 := 0
    n2 := 1
    i := 2

    PRINT(n1)
    PRINT(n2)

    IF(num > 2) THEN
        WHILE(num > i) DO //Loop until the desired number of outputs
            n3 := n1 + n2
            PRINT(n3)
            n1 := n2
            n2 := n3
            i := i + 1
        ENDWHILE
    ENDIF
ENDPROG