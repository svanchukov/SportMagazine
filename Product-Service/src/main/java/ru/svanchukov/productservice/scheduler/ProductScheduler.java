package ru.svanchukov.productservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.service.ProductService;

@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductService productService;

    @Scheduled(cron = "0 0 13 * * ?")
    public void addDailyProduct() {
        CreateNewProductDTO createNewProductDTO = new CreateNewProductDTO();
        createNewProductDTO.setName("Продукт дня");
        createNewProductDTO.setPrice(50.0);
        createNewProductDTO.setDescriptions("Автоматически добавленный самый вкусный продукт");

        productService.saveProduct(createNewProductDTO);

        System.out.println("Продукт добавлен автоматически: " + createNewProductDTO.getName());
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void increaseProductPrices() {
        productService.increasePrices(5.0);
        System.out.println("Цена увеличилась на 5.0 ");
    }
}
