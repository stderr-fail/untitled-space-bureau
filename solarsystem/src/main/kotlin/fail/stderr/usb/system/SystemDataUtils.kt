package fail.stderr.usb.system

import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.networknt.schema.*
import com.networknt.schema.resource.SchemaMappers
import com.networknt.schema.serialization.JsonNodeReader
import fail.stderr.usb.data.system.SystemData
import fail.stderr.usb.ex.DataDeserializationException
import fail.stderr.usb.system.schema.StderrFailSchemaMapper
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Collectors

object SystemDataUtils {
  @JvmStatic
  fun fromYAML(streamProvider: Supplier<InputStream>): SystemData {

    streamProvider.get().use { stream ->

      val rawData = String(stream.readAllBytes(), StandardCharsets.UTF_8)

      val schema = systemJsonSchema

      val assertions: Set<ValidationMessage> =
        schema.validate(rawData, InputFormat.YAML) { executionContext: ExecutionContext ->
          executionContext.executionConfig.formatAssertionsEnabled = true
        }

      if (assertions.isNotEmpty()) {

        val messages = assertions.stream()
          .map { obj: ValidationMessage -> obj.message }
          .collect(Collectors.joining(", "))

        throw DataDeserializationException(messages)
      }

      val data: SystemData = yamlMapper.readValue(rawData, SystemData::class.java)

      return data
    }

  }

  fun createClasspathScanningJsonSchemaFactory(): JsonSchemaFactory {
    val factory: JsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7) { //
        builder: JsonSchemaFactory.Builder ->
      val reader: JsonNodeReader = JsonNodeReader.builder()
        .locationAware()
        .jsonMapper(jsonMapper)
        .yamlMapper(yamlMapper)
        .build()
      builder.jsonNodeReader(reader)
      builder.schemaMappers { schemaMappers: SchemaMappers.Builder ->
        schemaMappers.add(StderrFailSchemaMapper())
      }

    }
    return factory
  }

  val systemJsonSchema: JsonSchema by lazy {
    val factory: JsonSchemaFactory = createClasspathScanningJsonSchemaFactory()
    val builder: SchemaValidatorsConfig.Builder = SchemaValidatorsConfig.builder()
    val config: SchemaValidatorsConfig = builder.build()
    factory.getSchema(SchemaLocation.of("https://stderr.fail/schemas/System"), config)
  }

  val jsonMapper: JsonMapper by lazy {
    JsonMapper.builder()
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
      .build()
  }

  val yamlMapper: YAMLMapper by lazy {
    YAMLMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build()
  }

}
