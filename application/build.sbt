lazy val scala3 = "3.3.0"
lazy val scala3Nightly = "3.3.2-RC1-bin-20230606-5d2812a-NIGHTLY"

lazy val core = Seq(
  "-feature",
  "-Werror",
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """lunatech-covid-exercise""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "org.playframework.anorm" %% "anorm" % "2.7.0",
      "org.postgresql" % "postgresql" % "42.6.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M6" % Test,
    ),
    scalacOptions ++= core,
  )
