all: 2048.jar

2048.jar:
	mkdir -p build/
	javac -source 1.6 -target 1.6 -d build/ java/*.java
	cd build && jar -cvmf ../java/MANIFEST.MF Troll-2048.jar *.class

clean:
	rm -rf build/

