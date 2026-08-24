package com.micro.pong.service

import spock.lang.Specification
import spock.lang.Subject

class ThrottleServiceSpec extends Specification {

    @Subject
    ThrottleService throttleService = new ThrottleService()

    def "first request should be allowed"() {
        when:
        def allowed = throttleService.tryProcess()

        then:
        allowed
    }

    def "second request in same second should be throttled"() {
        when:
        throttleService.tryProcess()
        def second = throttleService.tryProcess()

        then:
        !second
    }

    def "request after window expires should be allowed"() {
        given:
        throttleService.tryProcess()

        when:
        Thread.sleep(1100)
        def allowed = throttleService.tryProcess()

        then:
        allowed
    }
}
