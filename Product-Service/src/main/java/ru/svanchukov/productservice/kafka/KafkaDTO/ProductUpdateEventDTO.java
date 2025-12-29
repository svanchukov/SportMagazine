package ru.svanchukov.productservice.kafka.KafkaDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductUpdateEventDTO {

    private Long id;

    private String name;

    private String descriptions;

    private Double price;

    private String message;

    public ProductUpdateEventDTO(Long id, String name, String descriptions, Double price, String message) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
        this.price = price;
        this.message = message;
    }
}
