; What we have to output from test.fs

define i32 @main() {
  entry:
  %a = alloca i32 
  %value = constant i32 2
  store i32 %value, i32* %a
  ret i32 0
}