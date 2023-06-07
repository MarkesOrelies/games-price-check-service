package willyth.demoes.pcs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "scarped_times")
public class ScrapedSiteTimesEntity {

    @Id
    private String site;

    private LocalDateTime date;

    public ScrapedSiteTimesEntity() {
    }

    public ScrapedSiteTimesEntity(String site, LocalDateTime date) {
        this.site = site;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
