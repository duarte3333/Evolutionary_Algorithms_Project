# Define variables
JAVAC = javac
JAR = jar
JAVA_FILES = $(wildcard src/*.java)
CLASS_FILES = $(patsubst src/%.java,bin/%.class,$(JAVA_FILES))
MAIN_CLASS = Main
MANIFEST = manifest.txt
JAR_FILE = MyJarProject.jar

# Default target
all: $(JAR_FILE)

# Rule to create the JAR file
$(JAR_FILE): $(CLASS_FILES) $(MANIFEST)
	@echo "Creating JAR file..."
	$(JAR) cvfm $(JAR_FILE) $(MANIFEST) -C bin .

# Rule to compile Java source files
bin/%.class: src/%.java
	@echo "Compiling $<..."
	@mkdir -p bin
	$(JAVAC) -d bin $<

# Rule to clean compiled files and the JAR file
clean:
	@echo "Cleaning up..."
	@rm -rf bin
	@rm -f $(JAR_FILE)

run:
	@echo "Running the JAR file..."
	java -jar $(JAR_FILE) -r 3 6 1000 1 1 1 1 0.1
# Phony targets
.PHONY: all clean
