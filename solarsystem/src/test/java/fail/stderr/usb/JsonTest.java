package fail.stderr.usb;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.networknt.schema.*;
import com.networknt.schema.serialization.JsonNodeReader;
import fail.stderr.usb.json.schema.StderrFailSchemaMapper;
import fail.stderr.usb.model.system.SystemData;
import org.junit.jupiter.api.Assertions;
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

        final YAMLMapper yamlMapper = YAMLMapper.builder().build();

        final JsonNodeReader reader = JsonNodeReader.builder()
          .locationAware()
          .jsonMapper(jsonMapper)
          .yamlMapper(yamlMapper)
          .build();

        builder.jsonNodeReader(reader);

        builder.schemaMappers(schemaMappers -> {
          schemaMappers.add(new StderrFailSchemaMapper());
        });
      }
    );

    try (final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fail/stderr/schemas/System.schema.yaml")) {
//      final String schemaData = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

      SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
      SchemaValidatorsConfig config = builder.build();
      JsonSchema schema = jsonSchemaFactory.getSchema(SchemaLocation.of("https://stderr.fail/schemas/System"), config);

      System.out.println("here!");


      try (
        final InputStream jsonStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("systems/dwarla/system.json5");
        final InputStream yamlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("systems/dwarla/system.yaml")
      ) {
        final String json5 = new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
        final String yaml = new String(yamlStream.readAllBytes(), StandardCharsets.UTF_8);


//      Set<ValidationMessage> assertions = schema.validate(json5, InputFormat.JSON, executionContext -> {
        Set<ValidationMessage> assertions = schema.validate(yaml, InputFormat.YAML, executionContext -> {
          executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
        });

        Assertions.assertEquals(0, assertions.size());


        final YAMLMapper yamlMapper = YAMLMapper.builder()
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .build();

        final SystemData sys = yamlMapper.readValue(yaml, SystemData.class);

        System.out.println("here!");


      }

    }


    System.out.println("here!");


  }

}
