package com.github.kadeyoo.kotestgenerator.common.annotation

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SpringAnnotationTest : StringSpec({
    "fromString은 올바른 enum을 반환한다" {
        SpringAnnotation.fromString("Controller") shouldBe SpringAnnotation.CONTROLLER
        SpringAnnotation.fromString("RequestParam") shouldBe SpringAnnotation.REQUEST_PARAM
        SpringAnnotation.fromString("NotExist") shouldBe null
    }

    "isSpringAnnotation은 올바른 값을 반환한다" {
        SpringAnnotation.isSpringAnnotation("Service") shouldBe true
        SpringAnnotation.isSpringAnnotation("NotExist") shouldBe false
    }

    "isControllerAnnotation은 Controller/RestController만 true" {
        SpringAnnotation.isControllerAnnotation(SpringAnnotation.CONTROLLER) shouldBe true
        SpringAnnotation.isControllerAnnotation(SpringAnnotation.REST_CONTROLLER) shouldBe true
        SpringAnnotation.isControllerAnnotation(SpringAnnotation.SERVICE) shouldBe false
    }

    "isRequestAnnotation은 RequestParam/PathVariable/RequestBody만 true" {
        SpringAnnotation.isRequestAnnotation(SpringAnnotation.REQUEST_PARAM) shouldBe true
        SpringAnnotation.isRequestAnnotation(SpringAnnotation.PATH_VARIABLE) shouldBe true
        SpringAnnotation.isRequestAnnotation(SpringAnnotation.REQUEST_BODY) shouldBe true
        SpringAnnotation.isRequestAnnotation(SpringAnnotation.SERVICE) shouldBe false
    }
}) 