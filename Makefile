.PHONY: help # List of targets with descriptions
help:
	@grep '^\.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1\t\2/' | expand -t20

.PHONY: clean # Clean output files (target dir)
clean:
	rm -rf target/

.PHONY: build # Build the project and the plugin package
build:
	sbt assembly

.PHONY: travis # Build on travis
travis: build
