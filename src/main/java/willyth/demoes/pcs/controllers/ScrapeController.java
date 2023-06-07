package willyth.demoes.pcs.controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import willyth.demoes.pcs.services.Scraper;

import java.util.List;

@RestController
public class ScrapeController {

    private final List<Scraper> scrapers;

    public ScrapeController(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    @PostMapping("/scrape")
    public ResponseEntity<String> scrapeWebsite() {
        // todo ran scrappers async
        scrapers.forEach(Scraper::scrape);

        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
}
