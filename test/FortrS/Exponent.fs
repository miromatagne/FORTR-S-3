BEGINPROG Exponent

/* Computes and outputs a positive number to a given positive power, both are entered
by a user
If the number is negative, print an error */

    READ(base)          //Read the base
    READ(power)         //Read the exponent 
    result := 1
    error := -1
    
    i := 0
    IF(power > 0) THEN
            WHILE(power > i) DO
                result := result * base
                i := i + 1
            ENDWHILE
            PRINT(result)
    ELSE
        IF(power == 0) THEN 
            result := 1
            PRINT(result)
        ELSE
            PRINT(error)
        ENDIF
    ENDIF
ENDPROG