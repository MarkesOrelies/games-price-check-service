package willyth.demoes.pcs;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import willyth.demoes.pcs.repositories.GameListingRepository;
import willyth.demoes.pcs.services.PriceCheckService;
import willyth.demoes.pcs.services.PriceProviderService;
import willyth.demoes.pcs.services.impl.SteamPriceCheckService;

import java.net.http.HttpClient;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ApplicationConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public PriceCheckService priceCheckService(
            GameListingRepository gameListingRepository,
            List<PriceProviderService> priceProviderServices
    ) {
        return new SteamPriceCheckService(gameListingRepository, priceProviderServices);
    }

}
