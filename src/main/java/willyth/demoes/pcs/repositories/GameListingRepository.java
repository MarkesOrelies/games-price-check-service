package willyth.demoes.pcs.repositories;

import willyth.demoes.pcs.entities.GameListingCategory;
import willyth.demoes.pcs.entities.GameListingEntity;
import willyth.demoes.pcs.repositories.custom.ReadOnlyRepository;

import java.util.List;

public interface GameListingRepository extends ReadOnlyRepository<GameListingEntity, Long> {

    List<GameListingEntity> findByCategory(GameListingCategory category);
}
