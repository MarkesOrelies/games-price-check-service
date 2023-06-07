package willyth.demoes.pcs.services.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingEntity;
import willyth.demoes.pcs.entities.GogGamesEntity;
import willyth.demoes.pcs.repositories.GogGamesRepository;
import willyth.demoes.pcs.services.GameSites;
import willyth.demoes.pcs.services.PriceProviderService;
import willyth.demoes.pcs.services.Scraper;
import willyth.demoes.pcs.services.managers.ScraperTimesManager;
import willyth.demoes.pcs.utils.JsonBodyHandler;
import willyth.demoes.pcs.utils.ObjectMapperFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
class GogGamesScraperService implements Scraper, PriceProviderService {

    private final static String SITE_NAME = GameSites.GOG.name();

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GogGamesRepository gogGamesRepository;
    private final ScraperTimesManager scraperTimesManager;


    public GogGamesScraperService(
            HttpClient httpClient,
            GogGamesRepository gogGamesRepository,
            ScraperTimesManager scraperTimesManager
    ) {
        this.httpClient = httpClient;
        this.scraperTimesManager = scraperTimesManager;
        this.objectMapper = ObjectMapperFactory.scraper();
        this.gogGamesRepository = gogGamesRepository;
    }

    @Override
    public void scrape() {
        if (scraperTimesManager.shouldNotScrape(SITE_NAME)) {
            return;
        }

        System.out.println("Scraping Gog games");


        try {
            final HttpResponse<GogCatalogPage> initResponse = getPage(1);
            var initBody = initResponse.body();
            int totalPages = initBody.pages;
            processPage(initBody);
            for (int page = 2; page <= totalPages; page++) {
                var pageResponse = getPage(page);
                processPage(pageResponse.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Could not scrape gog games");
            return;
        }

        System.out.println("Completed Scraping Gog games");
        scraperTimesManager.updateScrape(SITE_NAME);
    }

    private HttpResponse<GogCatalogPage> getPage(int page) throws IOException, InterruptedException {
        var url = MessageFormat.format("https://catalog.gog.com/v1/catalog?limit=1000&order=desc:releaseDate&productType=in:game&page={0}", page);

        var request = HttpRequest.newBuilder(URI.create(url))
                .build();

        return httpClient.send(request, new JsonBodyHandler<>(GogCatalogPage.class, objectMapper));
    }

    private void processPage(GogCatalogPage page) throws IOException, InterruptedException {
        page.products.stream()
                .map((p) -> {
                    var entity = new GogGamesEntity.Builder(p.id, p.title.trim(), p.releaseDate, p.storeReleaseDate);
                    p.price.map(e -> e.finalMoney).ifPresent((price) -> {
                        var total = price.amount.add(price.discount);
                        entity.addPrice(total, price.currency, price.discount);
                    });
                    return entity.build();

                }).forEach(gogGamesRepository::save);
    }

    @Override
    public Optional<GameListingDto.Price> getPrice(GameListingEntity entity) {
        if (entity.getGogPrice() == null) {
            return Optional.empty();
        }
        return Optional.of(new GameListingDto.Price(
                GameSites.GOG,
                entity.getGogPrice(),
                entity.getGogDiscount()
        ));
    }

    private record GogCatalogPage(
            @JsonProperty int pages,
            @JsonProperty List<GogProduct> products) {

        private record GogProduct(
                @JsonProperty long id,
                @JsonProperty String title,

                @JsonProperty @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd") Date releaseDate,
                @JsonProperty @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd") Date storeReleaseDate,
                @JsonProperty Optional<GogPrice> price
        ) {
            private record GogPrice(
                    @JsonProperty GogFinalMoney finalMoney
            ) {

                private record GogFinalMoney(
                        @JsonProperty BigDecimal amount,
                        @JsonProperty String currency,
                        @JsonProperty BigDecimal discount
                ) {
                }
            }
        }
    }


}
