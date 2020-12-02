define i32 @main() {
  entry:
  %a = alloca i32 
  %0 = constant i32 -2
  %1 = constant i32 3
  %2 = constant i32 6
  %3 = constant i32 1
  %4 = add i32 %2, %3
  %5 = constant i32 3
  %6 = add i32 %4, %5
  %7 = constant i32 7
  %8 = add i32 %6, %7 
  %9 = mul i32 %1, %8  
  %10 = constant i32 5
  %11 = sdiv i32 %9, %10
  %12 = constant i32 4
  %13 = mul i32 %11, %12
  %14 = add i32 %0, %13
  store i32 %14, i32* %a
  ret i32 0
}