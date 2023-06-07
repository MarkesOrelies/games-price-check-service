package willyth.demoes.pcs.services;

import willyth.demoes.pcs.dtos.GameListingDto;
import willyth.demoes.pcs.entities.GameListingCategory;

import java.util.List;

public interface PriceCheckService {

    List<GameListingDto> getGamesByCategory(GameListingCategory category);



}
