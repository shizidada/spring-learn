package org.moose.operator.web.security.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import org.moose.operator.exception.BusinessAuthenticationException;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-07-29 23:08:23:08
 * @see org.moose.operator.web.security.component
 */
public class CustomAuthenticationExceptionSerializer
    extends StdSerializer<BusinessAuthenticationException> {
  protected CustomAuthenticationExceptionSerializer() {
    super(BusinessAuthenticationException.class);
  }

  @Override public void serialize(BusinessAuthenticationException e, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("code", e.getCode());
    jsonGenerator.writeStringField("message", e.getMessage());
    if (e.getAdditionalInformation() != null) {
      for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        jsonGenerator.writeStringField(key, value);
      }
    }
    jsonGenerator.writeEndObject();
  }
}
