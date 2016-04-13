# weka is in /usr/share/java/weka/weka.jar

all: compare_models

compare_models: src/compare_models.java
	javac -d bin -cp "bin;/usr/bin/weka;" src/compare_models.java
	
clean:
	rm -r bin/*
