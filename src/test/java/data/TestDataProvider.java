package data;

import mode.Customer;
import mode.Order;
import mode.Product;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestDataProvider {

    public static Customer createTestCustomer() {
        Product product1 = new Product(UUID.randomUUID(), "Laptop", 999.99, Map.of(5, 950.0, 10, 900.0));
        Product product2 = new Product(UUID.randomUUID(), "Smartphone", 699.99, Map.of(5, 650.0, 10, 600.0));

        Order order1 = new Order(UUID.randomUUID(), List.of(product1, product2), OffsetDateTime.now());

        Product product3 = new Product(UUID.randomUUID(), "Tablet", 499.99, Map.of(5, 475.0));
        Order order2 = new Order(UUID.randomUUID(), List.of(product3), OffsetDateTime.now().minusDays(5));

        return new Customer(
                UUID.randomUUID(),
                "Maksim",
                "Khimiak",
                LocalDate.of(1985, 5, 20),
                List.of(order1, order2)
        );
    }
}