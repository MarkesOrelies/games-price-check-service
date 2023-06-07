package willyth.demoes.pcs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import willyth.demoes.pcs.services.GameSites;

import java.math.BigDecimal;
import java.util.List;

public record GameListingDto(@JsonProperty String title,

                             @JsonProperty int ranking,
                             @JsonProperty List<Price> prices) {

    public record Price(@JsonProperty GameSites site,
                        @JsonProperty BigDecimal price,
                        @JsonProperty BigDecimal discount) {
    }

}
