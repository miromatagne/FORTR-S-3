all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src src/Main.java
	jar cfe dist/part3.jar Main -C bin .

testing:
	java -jar dist/part3.jar test/FortrS/Average.fs

output:
	java -jar dist/part3.jar -o outputFile.ll test/Atom/Atom.fs

exec:
	java -jar dist/part3.jar -exec test/Program/Program.fs

ast:
	java -jar dist/part3.jar test/FortrS/Average.fs -ast test.tex