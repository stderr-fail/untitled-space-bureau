package fail.stderr.usb.codegen

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream
import java.time.Instant

private fun OutputStream.appendText(str: String) {
  this.write(str.toByteArray())
}

class MyProcessor(
  val codeGenerator: CodeGenerator,
  val logger: KSPLogger
) : SymbolProcessor {

  var invoked = false


  override fun process(resolver: Resolver): List<KSAnnotated> {

    if (invoked) {
      return emptyList()
    }

    val symbols = resolver.getSymbolsWithAnnotation("fail.stderr.usb.codegen.BuildMeta")

    val matches = symbols.filter { it.validate() && it is KSClassDeclaration }.toList()
    matches.forEach { it.accept(BuilderVisitor(), Unit) }

    invoked = true

    return matches
  }

  inner class BuilderVisitor : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      classDeclaration.primaryConstructor!!.accept(this, data)
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

      val parent = function.parentDeclaration as KSClassDeclaration
      val packageName = parent.containingFile!!.packageName.asString()
      val className = "${parent.simpleName.asString()}Generated"

      val file = codeGenerator.createNewFile(Dependencies(true, function.containingFile!!), packageName, className)
      file.appendText("package $packageName\n\n")
      file.appendText("import godot.Node\n")
      file.appendText("import godot.global.GD\n")
      file.appendText("import godot.annotation.RegisterClass\n")
      file.appendText("import godot.annotation.RegisterFunction\n\n")
      file.appendText("@RegisterClass\n")
      file.appendText("class ${className} : Node() {\n")

      file.appendText("  @RegisterFunction\n")
      file.appendText("  override fun _ready() {\n")
      file.appendText("    GD.print(\"generated @ ${Instant.now()}\")\n")
      file.appendText("  }\n")

      file.appendText("}\n")
      file.close()

    }

  }


}

class MyProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    return MyProcessor(environment.codeGenerator, environment.logger)
  }
}