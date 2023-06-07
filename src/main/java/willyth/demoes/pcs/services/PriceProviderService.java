package willyth.demoes.pcs.services;

import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingEntity;

import java.util.Optional;

public interface PriceProviderService {
    Optional<GameListingDto.Price> getPrice(GameListingEntity entity);
}
