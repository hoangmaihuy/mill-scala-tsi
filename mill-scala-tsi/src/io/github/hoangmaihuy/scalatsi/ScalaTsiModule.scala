package io.github.hoangmaihuy.scalatsi

import mill._
import mill.scalalib._

trait ScalaTsiModule extends ScalaModule { outer =>

  def scalaTsiVersion: T[String]

  override def ivyDeps = T {
    super.ivyDeps() ++ Seq(ivy"com.scalatsi::scala-tsi:${scalaTsiVersion()}")
  }

  /** Types to export typescript version for */
  def typescriptExports: T[Seq[String]] = T { Seq.empty[String] }

  /** Additional imports, i.e. your packages so you don't need to prefix your classes. */
  def typescriptGenerationImports: T[Seq[String]] = T { Seq.empty[String] }

  /** File where all typescript interfaces will be written to */
  def typescriptOutputFile: T[os.Path] = T.dest / "scala-tsi.ts"

  /** Whether to add semicolons to the exported model" */
  def typescriptStyleSemicolons: T[Boolean] = T { false }

  /** Optional header for the output file */
  def typescriptHeader: T[Option[String]] = T { Some("// DO NOT EDIT: generated file by scala-tsi") }

  /** The discriminator field for tagged unions, or None to disable tagged unions */
  def typescriptTaggedUnionDiscriminator: T[Option[String]] = T { Some("type") }

  def createTypescriptExporter: T[PathRef] = T {
    val exporter = T.dest / "ExportTypescript.scala"
    val content = txt
      .ExportTypescriptTemplate(
        imports = typescriptGenerationImports(),
        classes = typescriptExports(),
        targetFile = typescriptOutputFile().toIO.getAbsolutePath,
        useSemicolons = typescriptStyleSemicolons(),
        header = typescriptHeader().getOrElse(""),
        taggedUnionDiscriminator = typescriptTaggedUnionDiscriminator()
      )
      .body
      .stripMargin
    os.write.over(exporter, content)
    PathRef(exporter)
  }

  private object exporter extends ScalaModule {
    override def scalaVersion = outer.scalaVersion

    override def moduleDeps = Seq(outer)

    override def sources = Seq(createTypescriptExporter())
  }

  def generateTypescript: T[PathRef] = T {
    exporter.runMain("com.scalatsi.generator.ExportTypescript")()
    PathRef(typescriptOutputFile())
  }

}
