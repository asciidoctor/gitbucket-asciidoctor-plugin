VERSION = 1.1.0
GITBUCKET_VERSION = 4.26.0

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

target/gitbucket-${GITBUCKET_VERSION}.war:
	mkdir -p target
	wget https://github.com/gitbucket/gitbucket/releases/download/${GITBUCKET_VERSION}/gitbucket.war -O target/gitbucket-${GITBUCKET_VERSION}.war

.PHONY: localServer # Start a local gitbucket server with the plugin installed
localServer: build target/gitbucket-${GITBUCKET_VERSION}.war
	mkdir -p target/DATA/plugins
	rm -rf target/DATA/plugins/*
	cp target/scala-2.12/gitbucket-asciidoctor-plugin-gitbucket_${GITBUCKET_VERSION}-${VERSION}.jar target/DATA/plugins
	java -jar target/gitbucket-${GITBUCKET_VERSION}.war --gitbucket.home=target/DATA
