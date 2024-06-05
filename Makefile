# Define variables
JAVAC = javac
JAR = jar
JAVADOC = javadoc
SRC_DIRS = src/main src/model src/service src/util
JAVA_FILES = $(foreach dir, $(SRC_DIRS), $(wildcard $(dir)/*.java))
CLASS_FILES = $(patsubst src/%.java, build/classes/%.class, $(JAVA_FILES))
MAIN_CLASS = main.Main
MANIFEST = manifest.txt
JAR_FILE = project.jar
DOC_DIR = JDOC

# Default target
all: $(JAR_FILE) javadoc

# Create the JAR file
$(JAR_FILE): $(CLASS_FILES) $(MANIFEST)
	@echo "Creating build directories..."
	@mkdir -p build/classes
	@mkdir -p build/src
	@echo "Copying source files..."
	@cp -r src/* build/src/
	@echo "Creating JAR file..."
	$(JAR) cvfm $(JAR_FILE) $(MANIFEST) -C build/classes . -C build/src .

# Compile Java source files
build/classes/%.class: src/%.java
	@echo "Compiling $<..."
	@mkdir -p $(dir $@)
	$(JAVAC) -d build/classes $<

# Generate Javadoc documentation
javadoc: $(JAVA_FILES)
	@echo "Generating Javadoc..."
	@mkdir -p $(DOC_DIR)
	$(JAVADOC) -d $(DOC_DIR) $(JAVA_FILES)

# Clean compiled files, the JAR file, and Javadoc
clean:
	@echo "Cleaning up..."
	@rm -rf build
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
