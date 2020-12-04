define i32 @main() {
  entry:
  %a = alloca i32 
  %0 = constant i32 1
  store i32 %0, i32* %a
  %b = alloca i32
  %1 = constant i32 2
  store i32 %1, i32* %b
  %2 = constant i32 3
  %3 = sdiv i32 %b, %a
  %4 = sdiv i32 %2, %3
  store i32 %4, i32* %b
  ret i32 0
}

; a:= 1  a := 2