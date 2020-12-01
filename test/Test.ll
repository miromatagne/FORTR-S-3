; What we have to output from test.fs

define i32 @main() {
  entry:
  %a = alloca i32 
  %0 = constant i32 -2
  %1 = constant i32 3
  %2 = constant i32 6
  %3 = constant i32 1
  %4 = add i32 %2, %3
  %5 = mul i32 %1, %4   ; problem here
  %6 = constant i32 5
  %7 = sdiv i32 %5, %6
  %8 = add i32 %0, %7   ; problem here
  store i32 %2, i32* %a
  ret i32 0
}