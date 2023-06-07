package willyth.demoes.pcs.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "titles_steamspy")
public class SteamSpyEntity {

    @EmbeddedId
    private SteamSpyId steamSpyId;

    private String title;


    private LocalDate releaseDate;

    private BigDecimal discountPrice;

    private int ranking;

    private SteamSpyEntity() {
    }

    public SteamSpyEntity(
            long steamId,
            GameListingCategory category,
            String title,
            LocalDate releaseDate,
            BigDecimal discountPrice,
            int ranking
    ) {
        this.steamSpyId = new SteamSpyId(steamId, category);
        this.title = title;
        this.releaseDate = releaseDate;
        this.discountPrice = discountPrice;
        this.ranking = ranking;
    }

    public SteamSpyId getSteamSpyId() {
        return steamSpyId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public int getRanking() {
        return ranking;
    }

    @Embeddable
    public static class SteamSpyId implements Serializable {
        private long steamId;
        private GameListingCategory category;

        public SteamSpyId() {
        }

        public SteamSpyId(long steamId, GameListingCategory category) {
            this.steamId = steamId;
            this.category = category;
        }
    }
}
