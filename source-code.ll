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
 %0 = call i32 readInt()
 %a = alloca i32
 store i32 %0, i32* %a
 %1 = load i32, i32* %a call void @println(i32 %1)
  ret i32 0
}