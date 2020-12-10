all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src src/Main.java
	jar cfe dist/part3.jar Main -C bin .

testing:
	java -jar dist/part3.jar test/Atom/Atom.fs

output:
	java -jar dist/part3.jar -o outputFile.ll test/Atom/Atom.fs

exec:
	java -jar dist/part3.jar -exec test/Atom/Atom.fs