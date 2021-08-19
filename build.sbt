val scala3Version = "3.0.1"

ThisBuild / version := "1.0"

ThisBuild / scalaVersion := scala3Version

ThisBuild / scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")

val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.9"

val commonSettings = Seq(
)

lazy val lib = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      ScalaTest % Test
    )
  )

lazy val examples = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      ScalaTest % Test
    )
  ).dependsOn(lib)
