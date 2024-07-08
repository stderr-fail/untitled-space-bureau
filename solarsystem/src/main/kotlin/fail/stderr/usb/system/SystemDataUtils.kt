package fail.stderr.usb.json;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.networknt.schema.*;
import com.networknt.schema.serialization.JsonNodeReader;
import fail.stderr.usb.json.schema.StderrFailSchemaMapper;
import fail.stderr.usb.model.data.DataDeserializationError;
import fail.stderr.usb.model.result.Result;
import fail.stderr.usb.model.result.ResultErr;
import fail.stderr.usb.model.result.ResultOk;
import fail.stderr.usb.data.system.SystemData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SystemDataUtils {

  public static Result<SystemData, DataDeserializationError> fromYAML(Supplier<InputStream> streamProvider) {
    // read the data
    try (final InputStream stream = streamProvider.get()) {

      final String rawData = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

      final JsonSchema schema = getSystemJsonSchema();

      final Set<ValidationMessage> assertions = schema.validate(rawData, InputFormat.YAML, executionContext -> {
        executionContext.getExecutionConfig().setFormatAssertionsEnabled(true);
      });

      if (!assertions.isEmpty()) {
        final String messages = assertions.stream().map(ValidationMessage::getMessage).collect(Collectors.joining(", "));
        return new ResultErr(new DataDeserializationError(messages, null));
      }

      final SystemData data = getYAMLMapper().readValue(rawData, SystemData.class);

      return new ResultOk(data);

    } catch (IOException e) {
      return new ResultErr(new DataDeserializationError(e.getMessage(), e));
    }
  }

  static JsonSchema __SYSTEM_JSON_SCHEMA = null;

  public static synchronized JsonSchema getSystemJsonSchema() {
    if (null == __SYSTEM_JSON_SCHEMA) {
      final JsonSchemaFactory factory = createClasspathScanningJsonSchemaFactory();
      final SchemaValidatorsConfig.Builder builder = SchemaValidatorsConfig.builder();
      final SchemaValidatorsConfig config = builder.build();
      __SYSTEM_JSON_SCHEMA = factory.getSchema(SchemaLocation.of("https://stderr.fail/schemas/System"), config);
    }
    return __SYSTEM_JSON_SCHEMA;
  }

  static JsonMapper __JSON_MAPPER = null;

  public static synchronized JsonMapper getJsonMapper() {
    if (null == __JSON_MAPPER) {
      // JSON5(ish) compliant mapper
      __JSON_MAPPER = JsonMapper.builder()
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

    }
    return __JSON_MAPPER;
  }

  static YAMLMapper __YAML_MAPPER = null;

  public static synchronized YAMLMapper getYAMLMapper() {
    if (null == __YAML_MAPPER) {
      __YAML_MAPPER = YAMLMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    }
    return __YAML_MAPPER;
  }

  public static JsonSchemaFactory createClasspathScanningJsonSchemaFactory() {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7, builder -> {

        final JsonNodeReader reader = JsonNodeReader.builder()
          .locationAware()
          .jsonMapper(getJsonMapper())
          .yamlMapper(getYAMLMapper())
          .build();

        builder.jsonNodeReader(reader);

        builder.schemaMappers(schemaMappers -> {
          schemaMappers.add(new StderrFailSchemaMapper());
        });

      }
    );
    return factory;
  }

}
