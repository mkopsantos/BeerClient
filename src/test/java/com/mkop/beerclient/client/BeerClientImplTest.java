package com.mkop.beerclient.client;

import com.mkop.beerclient.config.WebClientConfig;
import com.mkop.beerclient.model.BeerDto;
import com.mkop.beerclient.model.BeerPagedList;
import com.mkop.beerclient.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listBeersWithParams() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, 3, null,
                BeerStyleEnum.IPA.name(), null);
        BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isGreaterThan(0);
        assertThat(pagedList.getContent().size()).isLessThanOrEqualTo(3);
        System.out.println(pagedList.toList());
    }

    @Test
    void getBeerById() {
        Mono<BeerDto> beerById = beerClient.getBeerById(UUID.fromString("0a9e1830-7b1a-42d9-8884-844b6e56eb79"), false);
        BeerDto beerDto = beerById.block();
        assertThat(beerDto).isNotNull();
        System.out.println(beerDto);

    }

    @Test
    void getBeerByUPC() {
    }

    @Test
    void createBeer() {
    }

    @Test
    void deleteBeer() {
    }

    @Test
    void updateBeer() {
    }
}