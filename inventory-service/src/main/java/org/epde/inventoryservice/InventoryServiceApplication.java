package org.epde.inventoryservice;

import org.epde.inventoryservice.entity.Inventory;
import org.epde.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            inventoryRepository.deleteAll();
            var inventories = List.of(
                    Inventory.builder()
                            .skuCode("iPhone_13")
                            .quantity(100)
                            .build(),
                    Inventory.builder()
                            .skuCode("iPhone_13_red")
                            .quantity(0)
                            .build()
            );
            inventoryRepository.saveAll(inventories);
        };
    }

}
