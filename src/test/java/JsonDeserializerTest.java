import mode.Customer;
import data.TestDataProvider.*;
import org.example.JsonDeserializer;
import org.example.JsonSerializer;
import org.junit.jupiter.api.Test;

import static data.TestDataProvider.createTestCustomer;
import static org.junit.jupiter.api.Assertions.*;

class JsonDeserializerTest {

    private final JsonDeserializer deserializer = new JsonDeserializer();

    @Test
    public void testDeserializeCustomer() {
        Customer originalCustomer = createTestCustomer();

        JsonSerializer serializer = new JsonSerializer();
        JsonDeserializer deserializer = new JsonDeserializer();

        String json = serializer.serialize(originalCustomer);

        Customer deserializedCustomer = deserializer.deserialize(json, Customer.class);

        assertEquals(originalCustomer, deserializedCustomer, "Deserialized object does not match the original");
    }
}