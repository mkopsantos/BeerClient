package com.mkop.beerclient.client;

import com.mkop.beerclient.config.WebClientProperties;
import com.mkop.beerclient.model.BeerDto;
import com.mkop.beerclient.model.BeerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
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
                        .queryParamIfPresent("pageNumber", Optional.ofNullable(pageNumber))
                        .queryParamIfPresent("pageSize", Optional.ofNullable(pageSize))
                        .queryParamIfPresent("beerName", Optional.ofNullable(beerName))
                        .queryParamIfPresent("beerStyle", Optional.ofNullable(beerStyle))
                        .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
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
                        .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                        .build(beerId))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.BEER_BY_UPC_PATH)
                        .build(upc))
                .retrieve()
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> createBeer(BeerDto beer) {
        return webClient
                .post()
                .uri(WebClientProperties.CREATE_BEER_PATH)
                .body(BodyInserters.fromValue(beer))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteBeer(UUID beerId) {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.DELETE_BEER_BY_ID_PATH)
                        .build(beerId))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beer) {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.UPDATE_BEER_BY_ID_PATH)
                        .build(beerId))
                .body(BodyInserters.fromValue(beer))
                .retrieve()
                .toBodilessEntity();

    }

}
