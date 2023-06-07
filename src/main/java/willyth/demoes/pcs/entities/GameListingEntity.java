package willyth.demoes.pcs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "game_listing_view")
@Immutable //do not add setters
public class GameListingEntity {

    @Id
    private long steamId;

    private GameListingCategory category;

    private String title;
    private int ranking;

    private LocalDate releaseDate;

    private BigDecimal steamspyDiscount;

    private BigDecimal gogPrice;

    private BigDecimal gogDiscount;

    private BigDecimal greenmanPrice;

    private BigDecimal greenmanDiscount;

    public long getSteamId() {
        return steamId;
    }

    public GameListingCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public int getRanking() {
        return ranking;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public BigDecimal getSteamspyDiscount() {
        return steamspyDiscount;
    }

    public BigDecimal getGogPrice() {
        return gogPrice;
    }

    public BigDecimal getGogDiscount() {
        return gogDiscount;
    }

    public BigDecimal getGreenmanPrice() {
        return greenmanPrice;
    }

    public BigDecimal getGreenmanDiscount() {
        return greenmanDiscount;
    }
}
