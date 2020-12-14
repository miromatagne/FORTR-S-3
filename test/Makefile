all:
	jflex ../src/LexicalAnalyzer.flex
	javac -d ../bin -cp ../src ../src/Main.java

.PHONY: all atom comments errors average exponent factorial fibonacci maximum if while program fortrs

atom:
	@java -cp ../bin Main ./Atom/Atom.fs >file 
	@diff -b --strip-trailing-cr ./Atom/Atom.output file && echo "Atom : Test Successful" || echo "Atom : Test Failed"
	@rm -f file

comments:
	@java -cp ../bin Main ./Comments/Comments.fs >file 
	@diff -b --strip-trailing-cr ./Comments/Comments.output file && echo "Comments : Test Successful" || echo "Comments : Test Failed"
	@rm -f file

average:
	@java -cp ../bin Main ./FortrS/Average.fs >file 
	@diff -b --strip-trailing-cr ./FortrS/Average.output file && echo "Average : Test Successful" || echo "Average : Test Failed"
	@rm -f file

exponent:
	@java -cp ../bin Main ./FortrS/Exponent.fs >file 
	@diff -b --strip-trailing-cr ./FortrS/Exponent.output file && echo "Exponent : Test Successful" || echo "Exponents : Test Failed"
	@rm -f file

factorial:
	@java -cp ../bin Main ./FortrS/Factorial.fs >file 
	@diff -b --strip-trailing-cr ./FortrS/Factorial.output file && echo "Factorial : Test Successful" || echo "Factorial : Test Failed"
	@rm -f file

fibonacci:
	@java -cp ../bin Main ./FortrS/Fibonacci.fs >file 
	@diff -b --strip-trailing-cr ./FortrS/Fibonacci.output file && echo "Fibonacci : Test Successful" || echo "Fibonacci : Test Failed"
	@rm -f file

maximum:
	@java -cp ../bin Main ./FortrS/Maximum.fs >file 
	@diff -b --strip-trailing-cr ./FortrS/Maximum.output file && echo "Maximum : Test Successful" || echo "Maximum : Test Failed"
	@rm -f file

if:
	@java -cp ../bin Main ./Instructions/IF/IFinELSE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/IF/IFinELSE.output file && echo "IFinELSE : Test Successful" || echo "IFinELSE : Test Failed"
	@rm -f file
	@java -cp ../bin Main ./Instructions/IF/WHILEinELSE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/IF/WHILEinELSE.output file && echo "WHILEinELSE : Test Successful" || echo "WHILEinELSE : Test Failed"
	@rm -f file
	@java -cp ../bin Main ./Instructions/IF/WHILEinIF.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/IF/WHILEinIF.output file && echo "WHILEinIF : Test Successful" || echo "WHILEinIF : Test Failed"
	@rm -f file

while:
	@java -cp ../bin Main ./Instructions/WHILE/ConsecutiveWHILE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/WHILE/ConsecutiveWHILE.output file && echo "ConsecutiveWHILE : Test Successful" || echo "ConsecutiveWHILE : Test Failed"
	@rm -f file
	@java -cp ../bin Main ./Instructions/WHILE/IFandELSEinWHILE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/WHILE/IFandELSEinWHILE.output file && echo "IFandELSEinWHILE : Test Successful" || echo "IFandELSEinWHILE : Test Failed"
	@rm -f file
	@java -cp ../bin Main ./Instructions/WHILE/IFinWHILE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/WHILE/IFinWHILE.output file && echo "IFinWHILE : Test Successful" || echo "IFinWHILE : Test Failed"
	@rm -f file
	@java -cp ../bin Main ./Instructions/WHILE/NestedWHILE.fs >file 
	@diff -b --strip-trailing-cr ./Instructions/WHILE/NestedWHILE.output file && echo "NestedWHILE : Test Successful" || echo "NestedWHILE : Test Failed"
	@rm -f file

program:
	@java -cp ../bin Main ./Program/Program.fs >file 
	@diff -b --strip-trailing-cr ./Program/Program.output file && echo "Program : Test Successful" || echo "Program : Test Failed"
	@rm -f file

fortrs:
	@make average
	@make exponent
	@make factorial
	@make fibonacci
	@make maximum

test:
	@make atom
	@make comments
	@make average
	@make exponent
	@make factorial
	@make fibonacci
	@make maximum
	@make if
	@make while
	@make program