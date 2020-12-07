define i32 @main() {
  entry:
    %a = alloca i32 
    %0 = constant i32 1
    store i32 %0, i32* %a
    ;ici if
    %1 = load i32, i32 %a 
    %2 = constant i32 0
    %3 = icmp sgt i32 %1, %2
    br i1 %3, label %true1, label %else1
  true1:
    %4 = load i32, i32 %a
    %5 = constant i32 10
    %6 = icmp eq %4, %5
    br i1 %6, label %true2, %else2
  true2:
    %7 = constant i32 3
    store i32 %7, i32* %a
    br label %exit
  else2:
    %8 = constant i32 1
    store i32 %8, i32* %a
    br label %exit
  else1:
    %9 = constant i32 10
    store i32 %9, i32* %a
    br label %exit
  exit:
    ret i32 0
}
