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
	%nbnumbers = alloca i32
	store i32 %0, i32* %nbnumbers
	%average = alloca i32
	%1 = alloca i32
	store i32 0, i32* %1
	%2 = load i32, i32* %1
	store i32 %2, i32* %average
	%3 = load i32, i32* %nbnumbers
	%4 = alloca i32
	store i32 0, i32* %4
	%5 = load i32, i32* %4
	%6 = icmp sgt i32 %3, %5
	br i1 %6, label %true1, label %else1
  true1:
	%i = alloca i32
	%7 = alloca i32
	store i32 0, i32* %7
	%8 = load i32, i32* %7
	store i32 %8, i32* %i
	br label %while1
  while1:
	%9 = load i32, i32* %nbnumbers
	%10 = load i32, i32* %i
	%11 = icmp sgt i32 %9, %10
	br i1 %11, label %wtrue1, label %wexit1
  wtrue1:
	%12 = call i32 @readInt()
	%number = alloca i32
	store i32 %12, i32* %number
	%13 = load i32, i32* %i
	%14 = alloca i32
	store i32 1, i32* %14
	%15 = load i32, i32* %14
	%16 = add i32 %13, %15
	store i32 %16, i32* %i
	%17 = load i32, i32* %number
	call void @println(i32 %17)
	%18 = load i32, i32* %average
	%19 = load i32, i32* %i
	%20 = alloca i32
	store i32 1, i32* %20
	%21 = load i32, i32* %20
	%22 = sub i32 %19, %21
	%23 = mul i32 %18, %22
	%24 = load i32, i32* %number
	%25 = add i32 %23, %24
	%26 = load i32, i32* %i
	%27 = sdiv i32 %25, %26
	store i32 %27, i32* %average
	%28 = load i32, i32* %average
	call void @println(i32 %28)
	br label %while1
  wexit1:
	br label %exit
  else1:
	%29 = load i32, i32* %average
	call void @println(i32 %29)
	%30 = alloca i32
	store i32 0, i32* %30
	%31 = load i32, i32* %30
	store i32 %31, i32* %average
	br label %exit
  exit:
	%32 = load i32, i32* %average
	call void @println(i32 %32)
	ret i32 0
}