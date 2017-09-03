val Organization = "com.github.lefou"
// Don't forget to also update src/main/scala/Plugin.scala
val Version = "1.0.3"

val CompatMode = System.getenv("COMPAT_MODE") == "1"

val ScalaVersion = if(CompatMode) "2.11.8" else "2.12.3"

val GitBucketVersion = if(CompatMode) "4.0" else "4.16"
val GitBucketAssembly =
  if(CompatMode) "gitbucket" % "gitbucket-assembly" % s"${GitBucketVersion}.0"
  else "io.github.gitbucket" %% "gitbucket" % s"${GitBucketVersion}.0"
val JavaOptions =
  if(CompatMode) Seq("-target", "7", "-source", "7")
  else Seq("-target", "8", "-source", "8")

val Name = s"gitbucket-${GitBucketVersion}-asciidoctor-plugin"


lazy val root = (project in file(".")).
  settings(
    sourcesInBase := false,
    organization := Organization,
    name := Name,
    version := Version,
    scalaVersion := ScalaVersion,
    scalacOptions := Seq("-deprecation", "-language:postfixOps"),
    resolvers ++= Seq(
      "amateras-repo" at "http://amateras.sourceforge.jp/mvn/"
    ),
    libraryDependencies ++= Seq(
      GitBucketAssembly % "provided",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
      "org.asciidoctor" % "asciidoctorj" % "1.5.6",
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.21"
    ),
    javacOptions in compile ++= JavaOptions,

    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

  )
