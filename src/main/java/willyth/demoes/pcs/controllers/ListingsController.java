package willyth.demoes.pcs.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingCategory;
import willyth.demoes.pcs.services.PriceCheckService;

import java.util.List;

@RestController
public class ListingsController {


    private final PriceCheckService priceCheckService;

    public ListingsController(PriceCheckService priceCheckService) {
        this.priceCheckService = priceCheckService;
    }

    @GetMapping("/listings/{category}")
    public ResponseEntity<List<GameListingDto>> scrapeWebsite(@PathVariable GameListingCategory category) {
        return ResponseEntity.ok(priceCheckService.getGamesByCategory(category));
    }
}
