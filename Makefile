# Define variables
JAVAC = javac
JAR = jar
JAVADOC = javadoc
SRC_DIRS = src/main src/model src/service src/util
JAVA_FILES = $(foreach dir, $(SRC_DIRS), $(wildcard $(dir)/*.java))
CLASS_FILES = $(patsubst src/%.java, bin/%.class, $(JAVA_FILES))
MAIN_CLASS = main.Main
MANIFEST = manifest.txt
JAR_FILE = MyJarProject.jar
DOC_DIR = JDOC

# Default target
all: $(JAR_FILE) javadoc

# Rule to create the JAR file
$(JAR_FILE): $(CLASS_FILES) $(MANIFEST)
	@echo "Creating JAR file..."
	$(JAR) cvfm $(JAR_FILE) $(MANIFEST) -C bin .

# Rule to compile Java source files
bin/%.class: src/%.java
	@echo "Compiling $<..."
	@mkdir -p $(dir $@)
	$(JAVAC) -d bin $<

# Rule to generate Javadoc documentation
javadoc: $(JAVA_FILES)
	@echo "Generating Javadoc..."
	@mkdir -p $(DOC_DIR)
	$(JAVADOC) -d $(DOC_DIR) $(JAVA_FILES)

# Rule to clean compiled files, the JAR file, and Javadoc
clean:
	@echo "Cleaning up..."
	@rm -rf bin
	@rm -f $(JAR_FILE)
	@rm -rf $(DOC_DIR)

command:
	@echo "Running the JAR file..."
	java -jar $(JAR_FILE) -r 3 6 1000 100 1000 10 1 1

file:
	@echo "Running the JAR file..."
	java -jar $(JAR_FILE) -f ./TESTS/input.txt 

# Phony targets
.PHONY: all clean run javadoc
