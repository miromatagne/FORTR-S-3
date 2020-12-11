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
	br i1 %6, label %true1, label %exit1
  true1:
	%i = alloca i32
	%7 = alloca i32
	store i32 0, i32* %7
	%8 = load i32, i32* %7
	store i32 %8, i32* %i
	br label %while1
  while1:
	%9 = load i32, i32* %nbnumbers
	%10 = icmp sgt i32 %9, %0
	br i1 %10, label %wtrue1, label %wexit1
  wtrue1:
	%11 = call i32 @readInt()
	%number = alloca i32
	store i32 %11, i32* %number
	%12 = load i32, i32* %i
	%13 = alloca i32
	store i32 1, i32* %13
	%14 = load i32, i32* %13
	%15 = add i32 %12, %14
	store i32 %15, i32* %i
	%16 = load i32, i32* %average
	%17 = load i32, i32* %i
	%18 = alloca i32
	store i32 1, i32* %18
	%19 = load i32, i32* %18
	%20 = sub i32 %17, %19
	%21 = mul i32 %16, %20
	%22 = load i32, i32* %number
	%23 = add i32 %21, %22
	%24 = load i32, i32* %i
	%25 = sdiv i32 %23, %24
	store i32 %25, i32* %average
	br label %while1
  wexit1:
	%26 = alloca i32
	store i32 0, i32* %26
	%27 = load i32, i32* %26
	store i32 %27, i32* %average
	br label %exit1
  exit1:
	%28 = load i32, i32* %average
	call void @println(i32 %28)
	ret i32 0
}