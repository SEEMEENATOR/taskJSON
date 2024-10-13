package mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private UUID id;
    private String name;
    private double price;
    private Map<Integer, Double> discounts;
}