.PHONY: clean # Clean output files (target dir)
clean:
	rm -rf target/

.PHONY: package # Build the project and the plugin package
build:
	sbt assembly

