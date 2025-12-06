package com.example.GateWay.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/swagger/api-docs")
public class SwaggerAggregatorController {

    private final WebClient client = WebClient.builder().build();

    @GetMapping
    public Mono<String> aggregate() {

        Mono<String> user = client.get()
                .uri("lb://user-service/v3/api-docs")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> product = client.get()
                .uri("lb://product-service/v3/api-docs")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> order = client.get()
                .uri("lb://order-service/v3/api-docs")
                .retrieve()
                .bodyToMono(String.class);

        return Mono.zip(user, product, order)
                .map(tuple -> merge(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }


    // метод объединения JSON
    private String merge(String... jsons) {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode result = mapper.createObjectNode();
        ObjectNode paths = mapper.createObjectNode();
        ObjectNode schemas = mapper.createObjectNode();
        ArrayNode tags = mapper.createArrayNode();

        for (String json : jsons) {
            try {
                JsonNode node = mapper.readTree(json);

                // объединяем paths
                if (node.has("paths"))
                    paths.setAll((ObjectNode) node.get("paths"));

                // объединяем schemas
                if (node.has("components")) {
                    JsonNode c = node.get("components");

                    if (c.has("schemas"))
                        schemas.setAll((ObjectNode) c.get("schemas"));
                }

                // объединяем теги
                if (node.has("tags"))
                    tags.addAll((ArrayNode) node.get("tags"));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        result.put("openapi", "3.0.1");
        result.set("paths", paths);

        ObjectNode components = mapper.createObjectNode();
        components.set("schemas", schemas);

        result.set("components", components);
        result.set("tags", tags);

        return result.toPrettyString();
    }
}
