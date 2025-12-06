package ru.svanchukov.email_service;

import lombok.Data;

@Data
public class ProductUpdateMessage {

    private int id;

    private String name;

    private String descriptions;

    private Double price;

    private String message;
}
