package ru.svanchukov.productservice.kafka.KafkaDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateEventDTO {

    private Integer id;

    private String name;

    private String descriptions;

    private Double price;

    private String message;
}
