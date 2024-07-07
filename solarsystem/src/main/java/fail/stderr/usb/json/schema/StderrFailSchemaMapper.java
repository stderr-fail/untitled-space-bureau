package fail.stderr.usb.json.schema;

import com.networknt.schema.AbsoluteIri;
import com.networknt.schema.resource.SchemaMapper;

/**
 * A {@link SchemaMapper} which matches schema id prefix <code>https://stderr.fail/schemas/NAME</code> and remaps
 * it to a file on the classpath in pattern <code>classpath:fail/stderr/schemas/NAME.schema.json5</code>
 */
public class StderrFailSchemaMapper implements SchemaMapper {

  public static String PREFIX = "https://stderr.fail/schemas/";

  @Override
  public AbsoluteIri map(AbsoluteIri absoluteIRI) {
    String absoluteURI = absoluteIRI != null ? absoluteIRI.toString() : null;
    if (absoluteURI != null && absoluteURI.startsWith("https://stderr.fail/schemas/")) {
      final String schemaName = absoluteURI.substring(PREFIX.length());
      final String classpathResource = "classpath:fail/stderr/schemas/%s.schema.json5".formatted(schemaName);
      return AbsoluteIri.of(classpathResource);
    }
    return null;
  }

}
