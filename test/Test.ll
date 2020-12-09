define i32 @main() {
 entry:
 %a = alloca i32
 %0 = alloca i32
 store i32 10, i32* %0
 %1 = load i32, i32* %0
 store i32 %1, i32* %a
 %b = alloca i32
 %2 = alloca i32
 store i32 1, i32* %2
 %3 = load i32, i32* %2
 store i32 %3, i32* %b
 br label %while1
while1:
 %4 = load i32, i32* %a
 %5 = alloca i32
 store i32 0, i32* %5
 %6 = load i32, i32* %5
 %7 = icmp sgt i32 %4, %6
 br i1 %7, label %true1, label %false  ; NO false ?
true1:
 %8 = load i32, i32* %b
 %9 = alloca i32
 store i32 1, i32* %9
 %10 = load i32, i32* %9
 %11 = icmp eq i32 %8, %10
 br i1 %11, label %true2, label %exit2
true2:
 br label %exit2
exit2:
 %12 load i32, i32* a
 %13 = alloca i32
 store i32 1, i32* %13
 %14 = load i32, i32* %13
 store i32 1, i32* %13
 %14 = load i32, i32* %13
 %15 = sub i32 %12, %14
 store i32 %15, i32* %a     
 ; after this, there must be "br label %while1"
 ; false:
 %16 = load i32, i32* %a
 %17 = alloca i32
 store i32 10, i32* %17
 %18 = load i32, i32* %17
 %19 = icmp eq i32 %16, %18
 br i1 %19, label %true3, label %exit3
true3:
 %20 = alloca i32
 store i32 11, i32* %20
 %21 = load i32, i32* %20
 store i32 %21, i32* %a
 br label %exit3
exit3:
  ret i32 0
}