val Organization = "com.github.lefou"
// Don't forget to also update src/main/scala/Plugin.scala
val Version = "1.0.1"

val GitBucketVersion = "4.0"
val GitBucketAssemblyVersion = s"${GitBucketVersion}.0"
val Name = s"gitbucket-${GitBucketVersion}-asciidoctor-plugin"

val ScalaVersion = "2.11.8"

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
      "gitbucket"          % "gitbucket-assembly" % GitBucketAssemblyVersion % "provided",
      "com.typesafe.play" %% "twirl-compiler"     % "1.0.4"      % "provided",
      "javax.servlet"      % "javax.servlet-api"  % "3.1.0"      % "provided",
      "org.asciidoctor"    % "asciidoctorj"       % "1.5.4",
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.16"
    ),
    javacOptions in compile ++= Seq("-target", "7", "-source", "7"),
    
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
    
  )
