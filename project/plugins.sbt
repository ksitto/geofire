// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2"

resolvers += "Play2war plugins release" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.github.play2war" % "play2-war-plugin" % "1.2-beta4")