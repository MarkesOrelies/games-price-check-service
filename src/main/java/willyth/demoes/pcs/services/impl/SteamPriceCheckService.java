package willyth.demoes.pcs.services.impl;

import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingCategory;
import willyth.demoes.pcs.entities.GameListingEntity;
import willyth.demoes.pcs.repositories.GameListingRepository;
import willyth.demoes.pcs.services.PriceCheckService;
import willyth.demoes.pcs.services.PriceProviderService;

import java.util.List;
import java.util.Optional;

public class SteamPriceCheckService implements PriceCheckService {

    private final GameListingRepository gameListingRepository;
    private final List<PriceProviderService> priceProviderServices;

    public SteamPriceCheckService(
            GameListingRepository gameListingRepository,
            List<PriceProviderService> priceProviderServices
    ) {
        this.gameListingRepository = gameListingRepository;
        this.priceProviderServices = priceProviderServices;
    }

    @Override
    public List<GameListingDto> getGamesByCategory(GameListingCategory category) {

        var entities = gameListingRepository.findByCategory(category);
        return entities.stream()
                .map(this::mapGameListingDto)
                .toList();
    }

    private GameListingDto mapGameListingDto(GameListingEntity e) {
        final var prices = priceProviderServices.stream()
                .map(service -> service.getPrice(e))
                .flatMap(Optional::stream)
                .toList();
        return new GameListingDto(
                e.getTitle(),
                e.getRanking(),
                prices
        );
    }
}
