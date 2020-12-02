define i32 @main() {
  entry:
  %0 = call i32 readInt()
  %number = alloca i32 
  store i32 %0, i32* %number
  ret i32 0
}