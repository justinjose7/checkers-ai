main:
	rm -f checkers/*.class
	javac checkers/Engine.java
	java checkers.Engine
clean:
	rm -f checkers/*.class
