package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.MappingInfo
import com.intellij.psi.PsiElement

object CodeGeneratorUtil {
    fun dummyValue(type: String): String =
        when (type) {
            "Long" -> "1L"
            "Int" -> "1"
            "String" -> "\"example\""
            "Boolean" -> "true"
            "Double" -> "1.0"
            else -> "$type()"
        }

    fun extractMappingInfo(method: PsiElement): MappingInfo? {
        val annotations = method.firstChild.children.filter { it.javaClass.name.contains("Annotation") }
        for (annotation in annotations) {
            val annotationText = annotation.text
            val isGet = annotationText.contains("@GetMapping")
            val isPost = annotationText.contains("@PostMapping")
            val isPut = annotationText.contains("@PutMapping")
            val isDelete = annotationText.contains("@DeleteMapping")
            val isRequest = annotationText.contains("@RequestMapping")
            if (isGet || isPost || isRequest || isPut || isDelete) {
                // URL 추출 (단순한 value="..." 형태로 가정)
                val split = annotationText.split("\"")
                val url = split[1]
                val methodType = when {
                    isGet -> "get"
                    isPost -> "post"
                    isPut -> "put"
                    isDelete -> "delete"
                    else -> "get" // default
                }
                return MappingInfo(url, methodType)
            }
        }
        return null
    }

    // 파라미터 join + 람다 (모든 케이스 커버)
    fun <T> List<T>.toJoinedString(delimiter: String, transform: (T) -> String): String =
        this.joinToString(delimiter, transform = transform)

    // 파라미터 선언
    fun buildParamDecl(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("\n") { (n, t) -> "val $n = ${dummyValue(t)}" }

    // 쿼리스트링 URL
    fun buildUrlParams(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("&") { (n, _) -> "$n=\${$n}" }

    // 예외용 bad 파라미터
    fun buildBadParamDecl(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("\n") { (n, t) ->
            val badVal = when (t) {
                "Long", "Int" -> "-1"
                "String" -> "\"\""
                else -> dummyValue(t)
            }
            "val $n = $badVal"
        }
    fun buildBadUrlParams(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("&") { (n, _) -> "$n=\${$n}" }

    // expected 리턴값
    fun expectedValue(returnType: String): String =
        if (returnType == "Unit") "" else "val expected = ${dummyValue(returnType)} // TODO: 실제 값으로 변경"

    // API import statements
    fun apiImportStatements() = """
        import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
        import org.springframework.beans.factory.annotation.Autowired
        import org.springframework.test.web.servlet.MockMvc
        import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
        import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
        import io.kotest.core.spec.style.BehaviorSpec
    """.trimIndent()

    fun commentStatements() = """
        /**
         * 이 테스트 코드는 Kotest 플러그인에 의해 자동 생성되었습니다.
         * ⚠️ 실제 서비스 상황에 맞게 파라미터, mock, 예외 타입, 응답 검증 코드를 수정하세요!
         * 옵션: 플러그인 설정(톱니바퀴 버튼)에서 코드 스타일, 네이밍, mock 종류 등을 선택할 수 있습니다.
         */
    """.trimIndent()
}