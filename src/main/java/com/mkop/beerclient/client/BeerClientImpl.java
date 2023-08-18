package com.mkop.beerclient.client;

import com.mkop.beerclient.config.WebClientProperties;
import com.mkop.beerclient.model.BeerDto;
import com.mkop.beerclient.model.BeerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final WebClient webClient;

    @Override
    public Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName,
                                         String beerStyle, Boolean showInventoryOnHand) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.LIST_BEER_PATH)
                        .queryParamIfPresent("pageNumber", getQueryParameterOptional(pageNumber))
                        .queryParamIfPresent("pageSize", getQueryParameterOptional(pageSize))
                        .queryParamIfPresent("beerName", getQueryParameterOptional(beerName))
                        .queryParamIfPresent("beerStyle", getQueryParameterOptional(beerStyle))
                        .queryParamIfPresent("showInventoryOnHand", getQueryParameterOptional(showInventoryOnHand))
                        .build()
                )
                .retrieve()
                .bodyToMono(BeerPagedList.class);
    }

    @Override
    public Mono<BeerDto> getBeerById(UUID beerId, Boolean showInventoryOnHand) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.BEER_BY_ID_PATH)
                        .build(beerId))
                .retrieve()
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
        return null;
    }

    @Override
    public Mono<ResponseEntity> createBeer(BeerDto beer) {
        return null;
    }

    @Override
    public Mono<ResponseEntity> deleteBeer(UUID beerId) {
        return null;
    }

    @Override
    public Mono<ResponseEntity> updateBeer(UUID beerId, BeerDto beer) {
        return null;
    }

    private Optional<Object> getQueryParameterOptional(Object o){
        return o!=null?Optional.of(o):Optional.empty();
    }

}
