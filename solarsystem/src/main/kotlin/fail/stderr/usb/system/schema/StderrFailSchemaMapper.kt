package fail.stderr.usb.system.schema

import com.networknt.schema.AbsoluteIri
import com.networknt.schema.resource.SchemaMapper

/**
 * A [SchemaMapper] which matches schema id prefix `https://stderr.fail/schemas/NAME` and remaps
 * it to a file on the classpath in pattern `classpath:fail/stderr/schemas/NAME.schema.json5`
 */
class StderrFailSchemaMapper : SchemaMapper {
  override fun map(absoluteIRI: AbsoluteIri?): AbsoluteIri? {
    absoluteIRI?.let {
      val absoluteURI = absoluteIRI.toString()
      if (absoluteURI.startsWith("https://stderr.fail/schemas/")) {
        val schemaName = absoluteURI.substring(PREFIX.length)
        val classpathResource = "classpath:fail/stderr/schemas/${schemaName}.schema.json5"
        return AbsoluteIri.of(classpathResource)
      }
    }
    return null
  }

  companion object {
    var PREFIX: String = "https://stderr.fail/schemas/"
  }

}
