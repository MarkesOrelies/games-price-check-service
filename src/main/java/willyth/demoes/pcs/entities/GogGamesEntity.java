package willyth.demoes.pcs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "titles_goggames")
public class GogGamesEntity {

    @Id
    private long id;

    private String title;


    private BigDecimal price;
    private String currency;

    private BigDecimal discount;

    private Date releaseDate;

    private Date storeReleaseDate;

    private GogGamesEntity() {
    }

    private GogGamesEntity(long id, String title, BigDecimal price, String currency, BigDecimal discount, Date releaseDate, Date storeReleaseDate) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.discount = discount;
        this.releaseDate = releaseDate;
        this.storeReleaseDate = storeReleaseDate;
    }

    public static class Builder {
        private final long id;

        private final String title;

        private final Date releaseDate;
        private final Date storeReleaseDate;

        private BigDecimal price;
        private String currency;

        private BigDecimal discount;


        public Builder(long id, String title, Date releaseDate, Date storeReleaseDate) {
            this.id = id;
            this.title = title;
            this.releaseDate = releaseDate;
            this.storeReleaseDate = storeReleaseDate;
        }

        public void addPrice(BigDecimal price, String currency, BigDecimal discount) {
            this.price = price;
            this.currency = currency;
            this.discount = discount;
        }

        public GogGamesEntity build() {
            return new GogGamesEntity(id, title, price, currency, discount, releaseDate, storeReleaseDate);
        }
    }
}
