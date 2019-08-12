name := "gitbucket-asciidoctor-plugin"
organization := "com.github.lefou"
version := "1.1.0"
scalaVersion := "2.13.0"
gitbucketVersion := "4.32.0"
sourcesInBase := false

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.asciidoctor" % "asciidoctorj" % "1.5.6",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.21"
)
