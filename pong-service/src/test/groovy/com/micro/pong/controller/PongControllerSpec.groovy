package com.micro.pong.controller

import com.micro.pong.service.ThrottleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@WebFluxTest(PongController.class)
@Import(ThrottleService.class)
@TestPropertySource(properties = "pong.throttle.distributed=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PongControllerSpec extends Specification {

    @Autowired
    WebTestClient webTestClient

    def "should return World for first request"() {
        when:
        def result = webTestClient.post()
                .uri('/pong')
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue('Hello')
                .exchange()

        then:
        result.expectStatus().isOk()
        result.expectBody(String.class).isEqualTo('World')
    }

    def "should return 429 when throttled"() {
        when:
        def first = webTestClient.post()
                .uri('/pong')
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue('Hello')
                .exchange()
        def second = webTestClient.post()
                .uri('/pong')
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue('Hello')
                .exchange()

        then:
        first.expectStatus().isOk()
        second.expectStatus().isEqualTo(429)
        second.expectBody(String.class).isEqualTo('Throttled')
    }
}
