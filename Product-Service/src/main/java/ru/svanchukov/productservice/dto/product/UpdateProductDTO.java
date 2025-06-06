package ru.svanchukov.productservice.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductDTO {

    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    @Size(min = 5, max = 50, message = "Название должно иметь не меньше 5 символов и не больше 50 символов")
    private String name;

    @NotBlank(message = "Категория не должна быть пустой")
    private String category;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String descriptions;

    @Min(value = 1, message = "Цена не должна быть нулевой или отрицательной")
    private double price;

    @NotBlank(message = "Бренд не должен быть пустым")
    private String brand;

    public UpdateProductDTO(String name, String category, String descriptions, double price, String brand) {
        this.name = name;
        this.category = category;
        this.descriptions = descriptions;
        this.price = price;
        this.brand = brand;
    }
}
