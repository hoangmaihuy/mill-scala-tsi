import $ivy.`de.tototec::de.tobiasroeser.mill.integrationtest::0.7.1`
import $ivy.`io.chris-kipp::mill-ci-release::0.1.9`
import $ivy.`com.lihaoyi::mill-contrib-twirllib:`

import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.api.ZincWorkerUtil.scalaNativeBinaryVersion

import mill.twirllib._
import de.tobiasroeser.mill.integrationtest._
import io.kipp.mill.ci.release.CiReleaseModule
import io.kipp.mill.ci.release.SonatypeHost

def millVersionFile = T.source(PathRef(os.pwd / ".mill-version"))

def millVersion = T {
  os.read(millVersionFile().path).trim
}

object Versions {
  lazy val scala = "2.13.13"
  lazy val twirl = "2.0.3"
}

object `mill-scala-tsi` extends ScalaModule with TwirlModule with CiReleaseModule {

  override def scalaVersion = Versions.scala

  override def twirlVersion = Versions.twirl

  override def twirlScalaVersion = Versions.scala

  override def twirlSources = T.sources(millSourcePath / "twirl")

  override def generatedSources = super.generatedSources() :+ compileTwirl().classes

  override def sonatypeHost = Some(SonatypeHost.s01)

  override def versionScheme: T[Option[VersionScheme]] = T(Option(VersionScheme.EarlySemVer))

  override def pomSettings = PomSettings(
    description = "Mill plugin for Scala TSI",
    organization = "io.github.hoangmaihuy",
    url = "https://github.com/hoangmaihuy/mill-scala-tsi",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github(owner = "hoangmaihuy", repo = "mill-scala-tsi"),
    developers = Seq(Developer("hoangmaihuy", "Hoang Mai", "https://github.com/hoangmaihuy"))
  )

  override def artifactName = "mill-scala-tsi"

  override def artifactSuffix =
    "_mill" + scalaNativeBinaryVersion(millVersion()) +
      super.artifactSuffix()

  override def scalacOptions = Seq("-Ywarn-unused", "-deprecation")

  override def compileIvyDeps = super.compileIvyDeps() ++ Agg(
    ivy"com.lihaoyi::mill-scalalib:${millVersion()}"
  )

  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.playframework.twirl::twirl-api:${Versions.twirl}"
  )

}

object itest extends MillIntegrationTestModule {

  override def millTestVersion = millVersion

  override def pluginsUnderTest = Seq(`mill-scala-tsi`)

  def testBase = millSourcePath / "src"

  override def testInvocations = Seq(
    PathRef(testBase / "scala-tsi") -> Seq(
      TestInvocation.Targets(Seq("generateTypescript"))
    )
  )

}
