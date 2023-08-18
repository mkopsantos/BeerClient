package com.mkop.beerclient.client;

import com.mkop.beerclient.model.BeerDto;
import com.mkop.beerclient.model.BeerPagedList;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BeerClient {

    Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize,
                                  String beerName, String beerStyle, Boolean showInventoryOnHand);

    Mono<BeerDto> getBeerById(UUID beerId, Boolean showInventoryOnHand);

    Mono<BeerDto> getBeerByUPC(String upc);

    Mono<ResponseEntity> createBeer(BeerDto beer);

    Mono<ResponseEntity> deleteBeer(UUID beerId);


    Mono<ResponseEntity> updateBeer(UUID beerId, BeerDto beer);

}
