package willyth.demoes.pcs.services.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingEntity;
import willyth.demoes.pcs.entities.GreenManEntity;
import willyth.demoes.pcs.repositories.GreenManRepository;
import willyth.demoes.pcs.services.GameSites;
import willyth.demoes.pcs.services.PriceProviderService;
import willyth.demoes.pcs.services.Scraper;
import willyth.demoes.pcs.services.managers.ScraperTimesManager;
import willyth.demoes.pcs.utils.JsonBodyHandler;
import willyth.demoes.pcs.utils.JsonBodyPublisher;
import willyth.demoes.pcs.utils.ObjectMapperFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GreenManGamingScraperService implements Scraper, PriceProviderService {

    private final static String SITE_NAME = GameSites.GREEN_MAN.name();
    private final static String REGION = "US";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GreenManRepository greenManRepository;
    private final ScraperTimesManager scraperTimesManager;

    public GreenManGamingScraperService(
            HttpClient httpClient,
            GreenManRepository greenManRepository,
            ScraperTimesManager scraperTimesManager
    ) {
        this.httpClient = httpClient;
        this.greenManRepository = greenManRepository;
        this.scraperTimesManager = scraperTimesManager;
        this.objectMapper = ObjectMapperFactory.scraper();
    }

    @Override
    public void scrape() {
        if (scraperTimesManager.shouldNotScrape(SITE_NAME)) {
            return;
        }
        System.out.println("Scraping GreenManGaming");

        try {
            int upper = 5000;
            while (upper > -1) {
                var results = getPage(upper);
                if (results.statusCode() != 200) {
                    System.out.println("Could not complete scrape of Green Man Gaming: StatsuCode: " + results.statusCode());
                    return;
                }
                var result = results.body().results.get(0);
                if (result.nbHits > 1000) {
                    upper = result.hits.get(result.hits.size() - 1).regions().get(REGION).discountPrice.intValue();
                } else {
                    upper = -1;
                }
                result.hits.stream().map(hit -> {
                    var region = hit.regions.get(REGION);
                    var releaseDate = Date.from(Instant.ofEpochMilli(region.ReleaseDate));
                    var steamId = getSteamAppId(hit);

                    return new GreenManEntity(hit.productId, steamId, hit.displayName, region.fullPrice, region.currencyCode, region.fullPrice.subtract(region.discountPrice), releaseDate);
                }).forEach(greenManRepository::save);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Could not complete scrape of Green Man Gaming");
        }

        System.out.println("Completed scraping GreenManGaming");
        scraperTimesManager.updateScrape(SITE_NAME);
    }

    private long getSteamAppId(GreenManResponse.Result.Hit hit) {
        try {
            var steamAppId = hit.steamAppId;
            if (steamAppId == null) {
                return 0;
            }
            return Long.parseLong(steamAppId);
        } catch (NumberFormatException ex) {
            return 0;
        }

    }

    public HttpResponse<GreenManResponse> getPage(int upper) throws IOException, InterruptedException {
        var url = "https://sczizsp09z-3.algolianet.com/1/indexes/*/queries?x-algolia-agent=Algolia+for+JavaScript+(4.5.1);+Browser+(lite);+instantsearch.js+(4.8.3);+JS+Helper+(3.2.2)&x-algolia-api-key=3bc4cebab2aa8cddab9e9a3cfad5aef3&x-algolia-application-id=SCZIZSP09Z";
        var query = "query=all-games&filters=IsSellable%3Atrue+AND+AvailableRegions%3AUS+AND+NOT+ExcludeCountryCodes%3AUS+AND+IsDlc%3Afalse&hitsPerPage=1000&distinct=true&page=0&numericFilters=%5B%22Regions.US.Drp%3E%3D0%22%2C%22Regions.US.Drp%3C%3D" + upper + "%22%5D";
        var requestBody = new GreenManRequest(List.of(new GreenManRequest.Request("prod_ProductSearch_US_Drp_DESC", query)));

        var request = HttpRequest.newBuilder(URI.create(url))
                .POST(JsonBodyPublisher.of(objectMapper, requestBody))
                .build();

        return httpClient.send(request, new JsonBodyHandler<>(GreenManResponse.class, objectMapper));
    }

    @Override
    public Optional<GameListingDto.Price> getPrice(GameListingEntity entity) {
        if (entity.getGreenmanPrice() == null) {
            return Optional.empty();

        }
        return Optional.of(new GameListingDto.Price(
                GameSites.GREEN_MAN,
                entity.getGreenmanPrice(),
                entity.getGreenmanDiscount()
        ));
    }

    private record GreenManRequest(@JsonProperty List<Request> requests) {
        private record Request(@JsonProperty String indexName, @JsonProperty String params) {
        }
    }

    private record GreenManResponse(@JsonProperty List<Result> results) {
        private record Result(@JsonProperty List<Hit> hits,
                              @JsonProperty int nbHits) {
            private record Hit(@JsonProperty(value = "SteamAppId") String steamAppId,
                               @JsonProperty(value = "ProductId") long productId,
                               @JsonProperty(value = "DisplayName") String displayName,
                               @JsonProperty(value = "Regions") Map<String, Region> regions
            ) {
                private record Region(@JsonProperty(value = "CurrencyCode") String currencyCode,
                                      @JsonProperty(value = "Rrp") BigDecimal fullPrice,
                                      @JsonProperty(value = "Drp") BigDecimal discountPrice,
                                      @JsonProperty(value = "ReleaseDate") long ReleaseDate) {
                }
            }
        }
    }

}
