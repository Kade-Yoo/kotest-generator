package com.github.kadeyoo.kotestgenerator.common.constants

object SpecTemplateConstants {
    const val IMPORT_WEB_MVC_TEST = "import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest"
    const val IMPORT_AUTO_CONFIGURE_MOCK_MVC = "import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc"
    const val IMPORT_AUTOWIRED = "import org.springframework.beans.factory.annotation.Autowired"
    const val IMPORT_MOCK_MVC = "import org.springframework.test.web.servlet.MockMvc"
    const val IMPORT_REQUEST_BUILDERS_GET = "import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get"
    const val IMPORT_RESULT_MATCHERS_STATUS = "import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status"
    const val IMPORT_BEHAVIOR_SPEC = "import io.kotest.core.spec.style.BehaviorSpec"
    const val IMPORT_MOCKK = "import io.mockk.mockk"
    const val IMPORT_MOCKITO = "import org.mockito.Mockito"
    const val IMPORT_MOCKK_EVERY = "import io.mockk.every"
    const val IMPORT_HTTP_STATUS = "import org.springframework.http.HttpStatus"
    const val IMPORT_MEDIA_TYPE = "import org.springframework.http.MediaType"
    const val IMPORT_JACKSON_OBJECT_MAPPTER = "import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper"
    const val IMPORT_READ_VALUE = "import com.fasterxml.jackson.module.kotlin.readValue"
    const val IMPORT_SHOULD_BE_EQUAL = "import io.kotest.matchers.equals.shouldBeEqual"
    const val IMPORT_SHOULD_BE = "import io.kotest.matchers.shouldBe"
    const val IMPORT_MVC_BUILDER = "import org.springframework.test.web.servlet.setup.MockMvcBuilders"
    const val IMPORT_POST = "import org.springframework.test.web.servlet.post"
    const val IMPORT_PUT = "import org.springframework.test.web.servlet.put"
    const val IMPORT_DELETE = "import org.springframework.test.web.servlet.delete"
    const val IMPORT_GET = "import org.springframework.test.web.servlet.get"
    const val IMPORT_ASSERT_THROW = "import io.kotest.assertions.throwables.shouldThrow"

    const val GIVEN = "given"
    const val WHEN = "`when`"
    const val THEN = "then"
    const val CLASS_SUFFIX_API = "ApiTest"
    const val CLASS_SUFFIX_TEST = "Test"

    const val WEB_MVC_TEST_ANNOTATION = "@WebMvcTest"
    const val AUTO_CONFIGURE_MOCK_MVC_ANNOTATION = "@AutoConfigureMockMvc"
    const val MOCK_MVC_PARAM = "@Autowired val mockMvc: MockMvc"
    const val EXPECTED_COMMENT = "// TODO: 실제 값에 맞게 검증"
    const val MOCKK_EVERY_COMMENT = "// TODO: mock 반환값/로직 실제에 맞게 수정"
    const val MOCKK_THROW_COMMENT = "// TODO: 실제 로직에 맞게 mock 세팅"

    const val GET_METHOD_NAME = "get"

    const val VALID_RESPONSE_STATUS_IS_OK = "result.response.status shouldBe HttpStatus.OK.value()"
    const val VALID_RESPONSE_STATUS_IS_BAD_REQUEST = "result.response.status shouldBe HttpStatus.BAD_REQUEST.value()"
    val TEST_COMMENT = """
        /**
         * 이 테스트 코드는 Kotest 플러그인에 의해 자동 생성되었습니다.
         * ⚠️ 실제 서비스 상황에 맞게 파라미터, mock, 예외 타입, 응답 검증 코드를 수정하세요!
         * 옵션: 플러그인 설정(톱니바퀴 버튼)에서 코드 스타일, 네이밍, mock 종류 등을 선택할 수 있습니다.
         */
    """.trimIndent()
}