package weixin.mask;

/**
 * @auther dewey
 * @date 2022/7/21 20:47
 */

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EncrypyByObjectMapper {
    // Define @custom Annotation
    // assumed to be used by String type field for this example
    @Retention(RetentionPolicy.RUNTIME)
    @interface MaskSensitiveData {
    }

    public static class MyBean {
        private String userName;

        @MaskSensitiveData
        private String cardNumber;

        public MyBean() {
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    // map the Serializer/Deserializer based on custom annotation
    public static class MaskSensitiveDataAnnotationIntrospector extends NopAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public Object findSerializer(Annotated am) {
            MaskSensitiveData annotation = am.getAnnotation(MaskSensitiveData.class);
            if (annotation != null) {
                return MaskSensitiveDataSerializer.class;
            }

            return null;
        }

        @Override
        public Object findDeserializer(Annotated am) {
            MaskSensitiveData annotation = am.getAnnotation(MaskSensitiveData.class);
            if (annotation != null) {
                return MaskSensitiveDataDeserializer.class;
            }

            return null;
        }
    }

    public static class MaskSensitiveDataDeserializer extends StdDeserializer<String> {
        private static final long serialVersionUID = 1L;

        public MaskSensitiveDataDeserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            // un-masking logic here. in our example we are removing "MASK"
            // string
            String s = p.getValueAsString();
            return s.substring(4);
        }
    }

    public static class MaskSensitiveDataSerializer extends StdSerializer<String> {
        private static final long serialVersionUID = 1L;

        public MaskSensitiveDataSerializer() {
            super(String.class);
        }

        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            // Masking data; for our example we are adding 'MASK'
            String replace = value.replace("7", "*");
            gen.writeString("MASK" + replace);
        }
    }

    @Test
    public void demo() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AnnotationIntrospector sis = mapper.getSerializationConfig().getAnnotationIntrospector();
        AnnotationIntrospector dis = mapper.getDeserializationConfig().getAnnotationIntrospector();

        AnnotationIntrospector is1 = AnnotationIntrospectorPair.pair(sis, new MaskSensitiveDataAnnotationIntrospector());
        AnnotationIntrospector is2 = AnnotationIntrospectorPair.pair(dis, new MaskSensitiveDataAnnotationIntrospector());

        mapper.setAnnotationIntrospectors(is1, is2);

        MyBean obj = new MyBean();
        obj.setUserName("Saurabh Bhardwaj");
        obj.setCardNumber("4455-7788-9999-7777");
        String json = mapper.writeValueAsString(obj);

        System.out.println(json);
        String expectedJson = "{\"userName\":\"Saurabh Bhardwaj\",\"cardNumber\":\"MASK4455-7788-9999-7777\"}";
//        assertEquals(json, expectedJson);

        MyBean cloned = mapper.readValue(json, MyBean.class);
        System.out.println(cloned.getCardNumber());
//        assertEquals(cloned.getCardNumber(), obj.getCardNumber());
    }
}
