import mode.Customer;
import mode.Order;
import mode.Product;
import org.example.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class JsonSerializerTest {

    @Test
    void shouldSerializeCustomerCorrectly() throws IllegalAccessException {
        JsonSerializer jsonSerializer = new JsonSerializer();
        UUID customerId = UUID.fromString("3b049946-ed7e-40ba-a7cb-f3585409da22");
        Product product1 = new Product(UUID.randomUUID(), "Laptop", 999.99, Map.of(1, 50.0));
        Product product2 = new Product(UUID.randomUUID(), "Headphones", 199.99, Map.of(2, 10.0));

        Order order = new Order(UUID.randomUUID(), Arrays.asList(product1, product2), OffsetDateTime.now());
        List<Order> orders = List.of(order);

        Customer customer = new Customer(customerId, "Maksim", "Khimiak", LocalDate.of(1990, 1, 1), orders);
        String expectedJson = "{" +
                "\"id\":\"3b049946-ed7e-40ba-a7cb-f3585409da22\"," +
                "\"firstName\":\"Maksim\"," +
                "\"lastName\":\"Khimiak\"," +
                "\"dateOfBirth\":\"1990-01-01\"," +
                "\"orders\":[{" +
                "\"id\":\"" + order.getId() + "\"," +
                "\"products\":[" +
                "{" +
                "\"id\":\"" + product1.getId() + "\"," +
                "\"name\":\"Laptop\"," +
                "\"price\":999.99," +
                "\"discounts\":{\"1\":50.0}" +
                "}," +
                "{" +
                "\"id\":\"" + product2.getId() + "\"," +
                "\"name\":\"Headphones\"," +
                "\"price\":199.99," +
                "\"discounts\":{\"2\":10.0}" +
                "}" +
                "]," +
                "\"createDate\":\"" + order.getCreateDate().toString() + "\"" +
                "}]" +
                "}";

        String actualJson = jsonSerializer.serialize(customer);
        Assertions.assertEquals(expectedJson.replaceAll("\\s+", ""), actualJson.replaceAll("\\s+", ""));
    }
}
