package willyth.demoes.pcs.services.impl;

import jakarta.annotation.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingCategory;
import willyth.demoes.pcs.entities.GameListingEntity;
import willyth.demoes.pcs.entities.SteamSpyEntity;
import willyth.demoes.pcs.repositories.SteamSpyRepository;
import willyth.demoes.pcs.services.GameSites;
import willyth.demoes.pcs.services.PriceProviderService;
import willyth.demoes.pcs.services.Scraper;
import willyth.demoes.pcs.services.managers.ScraperTimesManager;
import willyth.demoes.pcs.utils.JsoupConnectFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

@Service
class SteamSpyScraperService implements Scraper, PriceProviderService {

    private final static String SITE_NAME = GameSites.STEAM_SPY.name();

    private final SteamSpyRepository steamSpyRepository;
    private final ScraperTimesManager scraperTimesManager;
    private final JsoupConnectFactory jsoupConnectFactory;

    public SteamSpyScraperService(
            SteamSpyRepository steamSpyRepository,
            ScraperTimesManager scraperTimesManager,
            JsoupConnectFactory jsoupConnectFactory
    ) {
        this.steamSpyRepository = steamSpyRepository;
        this.scraperTimesManager = scraperTimesManager;
        this.jsoupConnectFactory = jsoupConnectFactory;
    }


    @Override
    public void scrape() {
        if (scraperTimesManager.shouldNotScrape(SITE_NAME)) {
            return;
        }
        System.out.println("Scraping SteamSpy");
        steamSpyRepository.deleteAll();
        try {
            var document = jsoupConnectFactory.get("https://steamspy.com");
            scrapeSection("trendinggames", GameListingCategory.TRENDING, document);
            scrapeSection("recentgames", GameListingCategory.RECENT, document);
            scrapeSection("tab-total", GameListingCategory.PLAYTIME, document);
        } catch (IOException e) {
            System.out.println(MessageFormat.format("Could not complete scrape of SteamSpy, ({0})", e.getMessage()));
            e.printStackTrace();
            return;
        }

        System.out.println("Completed scrape of SteamSpy");
        scraperTimesManager.updateScrape(SITE_NAME);
    }

    @Override
    public Optional<GameListingDto.Price> getPrice(GameListingEntity entity) {
        return Optional.of(new GameListingDto.Price(
                GameSites.STEAM_SPY,
                null,
                entity.getSteamspyDiscount()
        ));
    }

    private void scrapeSection(String id, GameListingCategory category, Document document) {
        var trendingGamesEl = document.getElementById(id);
        if (trendingGamesEl == null) {
            System.out.println(MessageFormat.format("Could not complete scrape of SteamSpy, missing {0}", id));
            return;
        }
        var trendingGames = trendingGamesEl.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        trendingGames.stream()
                .map(game -> mapSteamSpyEntity(game, category))
                .forEach(steamSpyRepository::save);
    }

    private static SteamSpyEntity mapSteamSpyEntity(Element game, GameListingCategory category) {
        final var properties = game.getElementsByTag("td");
        final var ranking = Integer.parseInt(properties.get(0).html());
        final var title = properties.get(1).getElementsByTag("a").get(0).childNode(1).toString().trim();
        final var appString = properties.get(1).getElementsByTag("a").get(0).attr("href");
        final var steamAppId = Long.parseLong(appString.substring(appString.lastIndexOf('/') + 1));
        final var releaseDate = getReleaseDate(properties);
        final var discountedPrice = new BigDecimal(properties.get(3).attr("data-order"))
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);

        return new SteamSpyEntity(
                steamAppId,
                category,
                title,
                releaseDate,
                discountedPrice,
                ranking
        );
    }

    @Nullable
    private static LocalDate getReleaseDate(Elements properties) {
        var releaseDateString = properties.get(2).attr("data-order");
        if (releaseDateString.equals("0000-00-00")) {
            return null;
        }
        return LocalDate.parse(releaseDateString);
    }
}
