

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

val scalaVersion = "2.13.1"
val Http4sVersion = "0.21.5"
val CirceVersion = "0.13.0"
val CalibanVersion = "0.9.0"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file("."))
  .settings(
    organization := "space.snu",
    name := "fp",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-literal"       % CirceVersion,
      // settings for db
      "com.typesafe.slick" %% "slick"           % "3.2.0",
      "com.typesafe.slick" %% "slick-hikaricp"  % "3.2.2",
      "org.xerial"          % "sqlite-jdbc"     % "3.7.2",
      "org.slf4j"           % "slf4j-nop"       % "1.6.4",

      "dev.profunktor"  %% "http4s-jwt-auth"     % "0.0.5",
      // support for graphql
      "com.github.ghostdogpr" %% "caliban"            % CalibanVersion,
      "com.github.ghostdogpr" %% "caliban-http4s"     % CalibanVersion,
      "com.github.ghostdogpr" %% "caliban-cats"       % CalibanVersion,

    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-Ypartial-unification",
  "-language:higherKinds"
)
