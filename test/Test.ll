define i32 @main() {
  entry:
  %0 = call i32 readInt()
  %a = alloca i32 
  store i32 %0, i32* %a
  %b = alloca i32  
  call void @println(i32 %b)
  ret i32 0
}