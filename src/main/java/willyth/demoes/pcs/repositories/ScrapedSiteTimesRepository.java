package willyth.demoes.pcs.repositories;

import org.springframework.data.repository.CrudRepository;
import willyth.demoes.pcs.entities.ScrapedSiteTimesEntity;

public interface ScrapedSiteTimesRepository extends CrudRepository<ScrapedSiteTimesEntity, String> {
}
