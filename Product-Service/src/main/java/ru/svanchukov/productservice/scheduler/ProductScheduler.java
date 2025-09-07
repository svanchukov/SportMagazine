package ru.svanchukov.productservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.service.ProductService;


/**
 * Планировщик задач для работы с продуктами.
 * <p>
 * Содержит методы для автоматического добавления новых продуктов и
 * периодического увеличения их цен.
 */
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductScheduler.class);
    private static final double DISCOUNT_THRESHOLD = 300.0;
    private static final double DEFAULT_PRODUCT_PRICE = 50.0;
    private static final double PRICE_INCREASE_PERCENT = 5.0;

    private final ProductService productService;

    /**
     * Ежедневно в 13:00 добавляет новый продукт.
     * Если цена нового продукта превышает DISCOUNT_THRESHOLD,
     * то устанавливаются дефолтные значения (название, цена, описание).
     */
    @Scheduled(cron = "0 0 13 * * ?")
    public void addDailyProduct() {
        CreateNewProductDTO createNewProductDTO = new CreateNewProductDTO();
        if (createNewProductDTO.getPrice() > DISCOUNT_THRESHOLD) {
            createNewProductDTO.setName("Продукт дня");
            createNewProductDTO.setPrice(DEFAULT_PRODUCT_PRICE);
            createNewProductDTO.setDescriptions("Автоматически добавленный самый вкусный продукт");
        }

        productService.saveProduct(createNewProductDTO);

        LOGGER.info("Продукт добавлен автоматически: {}", createNewProductDTO.getName());
    }

    /**
     * Каждые 2 минуты увеличивает цену всех продуктов
     * на PRICE_INCREASE_PERCENT процентов.
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void increaseProductPrices() {
        productService.increasePrices(PRICE_INCREASE_PERCENT);
        LOGGER.info("Цена увеличилась на: {}", PRICE_INCREASE_PERCENT);
    }
}

