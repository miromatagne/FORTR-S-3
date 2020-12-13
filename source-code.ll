@.strR = private unnamed_addr constant [3 x i8] c"%d\00", align 1
define i32 @readInt() #0 {
 %1 = alloca i32, align 4
 %2 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %1)
 %3 = load i32, i32* %1, align 4
 ret i32 %3}
declare i32 @scanf(i8*, ...) #1
@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

define void @println(i32 %x) {
 %1 = alloca i32, align 4
 store i32 %x, i32* %1, align 4
 %2 = load i32, i32* %1, align 4
 %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
 ret void
}
declare i32 @printf(i8*, ...) #1
define i32 @main() {
  entry:
	%0 = call i32 @readInt()
	%num = alloca i32
	store i32 %0, i32* %num
	%n1 = alloca i32
	%1 = alloca i32
	store i32 0, i32* %1
	%2 = load i32, i32* %1
	store i32 %2, i32* %n1
	%n2 = alloca i32
	%3 = alloca i32
	store i32 1, i32* %3
	%4 = load i32, i32* %3
	store i32 %4, i32* %n2
	%n3 = alloca i32
	%5 = alloca i32
	store i32 0, i32* %5
	%6 = load i32, i32* %5
	store i32 %6, i32* %n3
	%i = alloca i32
	%7 = alloca i32
	store i32 2, i32* %7
	%8 = load i32, i32* %7
	store i32 %8, i32* %i
	%9 = load i32, i32* %n1
	call void @println(i32 %9)
	%10 = load i32, i32* %n2
	call void @println(i32 %10)
	%11 = load i32, i32* %num
	%12 = alloca i32
	store i32 2, i32* %12
	%13 = load i32, i32* %12
	%14 = icmp sgt i32 %11, %13
	br i1 %14, label %true1, label %exit1
  true1:
	br label %while1
  while1:
	%15 = load i32, i32* %num
	%16 = load i32, i32* %i
	%17 = icmp sgt i32 %15, %16
	br i1 %17, label %wtrue1, label %wexit1
  wtrue1:
	%18 = load i32, i32* %n1
	%19 = load i32, i32* %n2
	%20 = add i32 %18, %19
	store i32 %20, i32* %n3
	%21 = load i32, i32* %n3
	call void @println(i32 %21)
	store i32 %21, i32* %n1
	store i32 %21, i32* %n2
	%22 = load i32, i32* %i
	%23 = alloca i32
	store i32 1, i32* %23
	%24 = load i32, i32* %23
	%25 = add i32 %22, %24
	store i32 %25, i32* %i
	br label %while1
  wexit1:
	br label %exit1
  exit1:
	ret i32 0
}