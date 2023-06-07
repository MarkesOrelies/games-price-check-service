package willyth.demoes.pcs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "titles_greenman")
public class GreenManEntity {

    @Id
    private long id;

    private long steamAppId;

    private String title;


    private BigDecimal price;
    private String currency;

    private BigDecimal discount;

    private Date releaseDate;

    public GreenManEntity() {
    }

    public GreenManEntity(
            long id,
            long steamAppId,
            String title,
            BigDecimal price,
            String currency,
            BigDecimal discount,
            Date releaseDate
    ) {
        this.id = id;
        this.steamAppId = steamAppId;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.discount = discount;
        this.releaseDate = releaseDate;
    }

}
