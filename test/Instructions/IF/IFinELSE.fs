BEGINPROG IFinELSE
    a := 1
    b := 0
    c := -1
    IF(a = 0) THEN
        // empty
    ELSE
        IF(a > 7) THEN	
            b := 1
        ELSE
            IF(a = 1) THEN
                a := 4
            ELSE
                a := 3
            ENDIF
        ENDIF
        c := 0
    ENDIF
    PRINT(a)
    PRINT(b)
    PRINT(c)
ENDPROG