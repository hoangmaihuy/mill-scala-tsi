import $file.plugins

import mill._
import mill.scalalib._
import io.github.hoangmaihuy.scalatsi._

object `scala-tsi` extends RootModule with ScalaModule with ScalaTsiModule {

  override def scalaVersion = "3.3.1"

  override def scalaTsiVersion = "0.8.2"

  override def typescriptExports = Seq(
    "DeepThought",
    "GenericCaseClass",
    "GreetFunction",
    "Greeter",
    "Integer",
    "JavaEnum",
    "MyCaseClass",
    "ScalaEnum.type",
    "Sealed"
  )

  override def typescriptGenerationImports = Seq(
    "models._",
    "ReadmeTSTypes._",
    "models.enumeration._"
  )

  override def typescriptOutputFile = T.dest / "model.ts"

  override def typescriptTaggedUnionDiscriminator = Some("kind")

}
