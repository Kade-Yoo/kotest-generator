package com.github.kadeyoo.kotestgenerator.common.constants

/**
 * 템플릿 생성에서 사용되는 상수들
 */
object TemplateConstants {
    
    // 들여쓰기 관련
    const val INDENT_SIZE = 4
    const val DEFAULT_INDENT = "    "
    const val DOUBLE_INDENT = "        "
    const val TRIPLE_INDENT = "            "
    
    // HTTP 상태 코드
    const val HTTP_OK = "isOk()"
    const val HTTP_BAD_REQUEST = "isBadRequest()"
    const val HTTP_NOT_FOUND = "isNotFound()"
    
    // HTTP 메서드
    const val HTTP_GET = "get"
    const val HTTP_POST = "post"
    const val HTTP_PUT = "put"
    const val HTTP_DELETE = "delete"
    
    // Media Type
    const val MEDIA_TYPE_JSON = "MediaType.APPLICATION_JSON"
    
    // 어노테이션 이름
    const val REQUEST_PARAM = "RequestParam"
    const val PATH_VARIABLE = "PathVariable"
    const val REQUEST_BODY = "RequestBody"
    const val CONTROLLER = "Controller"
    const val REST_CONTROLLER = "RestController"
    const val SERVICE = "Service"
    const val REPOSITORY = "Repository"
    
    // 테스트 메시지
    const val SUCCESS_MESSAGE = "정상적인 요청을 보낼 경우"
    const val ERROR_MESSAGE = "존재하지 않는 값으로 요청하면"
    const val SUCCESS_RESULT_MESSAGE = "200 OK를 반환한다"
    const val ERROR_RESULT_MESSAGE = "400 Bad Request 가 반환된다"
    const val SERVICE_SUCCESS_MESSAGE = "조회하면"
    const val SERVICE_ERROR_MESSAGE = "존재하지 않는 ID로 인해"
    const val SERVICE_SUCCESS_RESULT_MESSAGE = "정상 결과가 반환되어야 한다"
    const val SERVICE_ERROR_RESULT_MESSAGE = "예외가 발생해야 한다"
    
    // TODO 메시지
    const val TODO_MOCK_RETURN = "// TODO: mock 반환값/로직 실제에 맞게 수정해주세요."
    const val TODO_SERVICE_MOCK = "// TODO: service mock 반환값/로직 실제에 맞게 수정해주세요."
    const val TODO_URL = "// TODO: 실제 URL에 맞게 수정해주세요."
    const val TODO_RESPONSE_CODE = "// TODO: 실제 응답 코드에 맞게 수정해주세요."
    const val TODO_RESPONSE_VALUE = "// TODO: 실제 응답 값에 맞게 수정해주세요."
    const val TODO_ACTUAL_VALUE = "// TODO: 실제 값으로 변경"
    const val TODO_VERIFICATION = "// TODO: 실제 값에 맞게 검증"
    const val TODO_EXCEPTION_TYPE = "// TODO: 예외 타입에 맞게 수정"
    
    // 변수명
    const val MAPPER_VARIABLE = "mapper"
    const val MOCK_MVC_VARIABLE = "mockMvc"
    const val SERVICE_VARIABLE = "service"
    const val RESULT_VARIABLE = "result"
    const val EXPECTED_VARIABLE = "expected"
    const val RESPONSE_DATA_VARIABLE = "responseData"
    
    // 메서드명
    const val JACKSON_OBJECT_MAPPER = "jacksonObjectMapper()"
    const val WRITE_VALUE_AS_STRING = "writeValueAsString"
    const val READ_VALUE = "readValue"
    const val CONTENT_AS_BYTE_ARRAY = "contentAsByteArray"
    const val SHOULD_BE = "shouldBe"
    const val SHOULD_THROW = "shouldThrow"
    
    // 클래스명 접미사
    const val TEST_SUFFIX = "Test"
    
    // 예외 타입
    const val DEFAULT_EXCEPTION = "Exception"
} 