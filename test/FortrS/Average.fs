BEGINPROG Average

    READ(nbnumbers)     // Read the number of numbers that the user is going to enter next
    average := 0  
    IF (nbnumbers > 0) THEN
      i := 0
      WHILE(nbnumbers > i) DO
        READ(number)
        i := i + 1
        average := (average * (i - 1) + number)/i 
        ENDWHILE
    ELSE       // The input number of numbers is negative or zero
      average := 0  
    ENDIF
    PRINT(average)
ENDPROG