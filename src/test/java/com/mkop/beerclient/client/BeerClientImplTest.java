package com.mkop.beerclient.client;

import com.mkop.beerclient.config.WebClientConfig;
import com.mkop.beerclient.model.BeerDto;
import com.mkop.beerclient.model.BeerPagedList;
import com.mkop.beerclient.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeersBlock() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listBeersMapFunction() throws InterruptedException {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        List<BeerDto> beerDtoList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        beerPagedListMono.map(pagedList -> pagedList.getContent())
                .subscribe(list -> {
                    beerDtoList.addAll(list);
                    countDownLatch.countDown();
                });
        countDownLatch.await();
        assertThat(beerDtoList.size()).isGreaterThan(0);
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
        Mono<BeerDto> beerById = beerClient.getBeerById(getExistingBeer().getId(), false);
        BeerDto beerDto = beerById.block();
        assertThat(beerDto).isNotNull();
        System.out.println(beerDto);
    }

    @Test
    void getBeerByIdNonBlocking() throws InterruptedException {
        Mono<BeerDto> beerMono = beerClient.getBeerById(getExistingBeer().getId(), false);
        AtomicReference<BeerDto> dtoAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        beerMono.map(beerDto -> beerDto)
                .subscribe(beerDto -> {
                    dtoAtomicReference.set(beerDto);
                    countDownLatch.countDown();
                });
        countDownLatch.await();
        assertThat(dtoAtomicReference.get()).isNotNull();
        System.out.println(dtoAtomicReference.get());
    }

    @Test
    void getBeerByUPC() {
        BeerDto existingBeer = getExistingBeer();
        assertThat(existingBeer).isNotNull();
        assertThat(existingBeer.getUpc()).isNotBlank();
        Mono<BeerDto> beerById = beerClient.getBeerByUPC(existingBeer.getUpc());
        BeerDto beerDto = beerById.block();
        assertThat(beerDto).isNotNull();
        System.out.println(beerDto);
    }

    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Estrella")
                .beerStyle(BeerStyleEnum.LAGER)
                .upc("8410793546135")
                .quantityOnHand(100)
                .price(BigDecimal.valueOf(1.50))
                .build();
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void deleteBeer() {
        createBeer();
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, "Estrella", null, null);
        BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        BeerDto beerDto = pagedList.getContent().get(0);
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(beerDto.getId());
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        //deleting all Estrellas
        pagedList.getContent().forEach(beer -> beerClient.deleteBeer(beer.getId()));
    }

    @Test
    void deleteBeerNotFound() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());
        assertThrows(WebClientResponseException.class, () -> {
            ResponseEntity<Void> responseEntity = responseEntityMono.block();
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Test
    void deleteBeerNotFoundHandlingException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());
        ResponseEntity<Void> responseEntity = responseEntityMono.onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientResponseException) {
                        return Mono.just(ResponseEntity.status(webClientResponseException.getStatusCode()).build());
                    } else {
                        throw new RuntimeException(error);
                    }
                })
                .block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateBeer() {
        BeerDto existingBeer = getExistingBeer();
        BeerDto beerDto = BeerDto
                .builder()
                .beerName("Updated Beer Name")
                .beerStyle(existingBeer.getBeerStyle())
                .quantityOnHand(existingBeer.getQuantityOnHand())
                .beerStyle(existingBeer.getBeerStyle())
                .upc(existingBeer.getUpc())
                .price(existingBeer.getPrice())
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(existingBeer.getId(), beerDto);
        ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Mono<BeerDto> beerById = beerClient.getBeerById(existingBeer.getId(), true);
        beerDto = beerById.block();
        assertThat(beerDto).isNotNull();
        assertThat(beerDto.getBeerName()).isEqualTo("Updated Beer Name");
    }

    private BeerDto getExistingBeer() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        return Objects.requireNonNull(beerPagedListMono.block()).getContent().get(0);
    }
}