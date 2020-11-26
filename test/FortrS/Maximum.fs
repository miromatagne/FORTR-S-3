BEGINPROG Maximum

/*Find the maximum between all the positive numbers entered by a user */

    READ(nbnumbers) //The user enters the number of numbers he is going to enter next
    i := 0
    max := 0
    error := -1
    IF(nbnumbers > 0) THEN
        WHILE(nbnumbers > i) DO
            READ(number)
            IF(number > max) THEN
                max := number
            ENDIF
            i := i + 1
        ENDWHILE
        PRINT(max)
    ELSE
        PRINT(error)
    ENDIF
ENDPROG