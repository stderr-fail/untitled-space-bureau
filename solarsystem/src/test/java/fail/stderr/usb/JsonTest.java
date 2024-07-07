package fail.stderr.usb;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.networknt.schema.*;
import com.networknt.schema.resource.SchemaMapper;
import com.networknt.schema.serialization.JsonNodeReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JsonTest {

  @Test
  public void test() throws Exception {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7, builder -> {

        // JSON5(ish) compliant mapper
        final JsonMapper jsonMapper = JsonMapper.builder()
          .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
          .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
          .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
          .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
          .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
          .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
          .enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS)
          .enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS)
          .enable(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)
          .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
          .build();

        final JsonNodeReader reader = JsonNodeReader.builder()
          .locationAware()
          .jsonMapper(jsonMapper)
          .build();

        builder.jsonNodeReader(reader);

        builder.schemaMappers(schemaMappers -> {
          schemaMappers.add(new CustomSchemaMapper());
        });
      }
    );


    SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
    SchemaValidatorsConfig config = builder.build();
    JsonSchema schema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://stderr.fail/schemas/System"), config);

    try (final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("systems/dwarla/system.json5")) {
      final String json5 = new String(stream.readAllBytes(), StandardCharsets.UTF_8);


      Set<ValidationMessage> assertions = schema.validate(json5, InputFormat.JSON, executionContext -> {
        executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
      });

    }



    System.out.println("here!");


  }

  public static class CustomSchemaMapper implements SchemaMapper {

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
}
