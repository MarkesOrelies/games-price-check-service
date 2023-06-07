package willyth.demoes.pcs.services.managers;

import org.springframework.stereotype.Component;
import willyth.demoes.pcs.entities.ScrapedSiteTimesEntity;
import willyth.demoes.pcs.repositories.ScrapedSiteTimesRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ScraperTimesManager {

    private final ScrapedSiteTimesRepository scrapedSiteTimesRepository;

    public ScraperTimesManager(ScrapedSiteTimesRepository scrapedSiteTimesRepository) {
        this.scrapedSiteTimesRepository = scrapedSiteTimesRepository;
    }

    public boolean shouldNotScrape(String siteName) {
        var lastScrapedTime = getDate(siteName);
        return getNow().equals(lastScrapedTime);
    }

    public void updateScrape(String siteName) {
        var entity = new ScrapedSiteTimesEntity(siteName, getNow());
        scrapedSiteTimesRepository.save(entity);
    }

    private LocalDateTime getDate(String siteName) {
        return scrapedSiteTimesRepository
                .findById(siteName)
                .map(ScrapedSiteTimesEntity::getDate)
                .orElseGet(() -> getNow().minusDays(32));
    }

    private LocalDateTime getNow() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

}
