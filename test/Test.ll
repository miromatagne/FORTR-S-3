define i32 @main() {
  entry:
    %a = alloca i32 
    %0 = constant i32 1
    store i32 %0, i32* %a
    %1 = load i32, i32 %a 
    %2 = constant i32 0
    %3 = icmp sgt i32 %1, %2
    br i1 %3, label %greater, label %lowerOrEquals
  greater:
    %4 = constant i32 3
    store i32 %4, i32* %a
    br label %lowerOrEquals
  lowerOrEquals:
    ret i32 0
}

; IF  INDENTER TOUT A LINTERIEUR DE ENTRY