package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TemplateUtilsTest : StringSpec({
    "buildImports는 동일한 importNames에 대해 캐싱된 결과를 반환한다" {
        val imports = listOf("a.A", "b.B")
        val result1 = TemplateUtils.buildImports(imports)
        val result2 = TemplateUtils.buildImports(imports)
        result1 shouldBe result2
        result1 shouldBe "import a.A\nimport b.B"
    }

    "buildMockDeclarations는 파라미터 정보를 올바르게 변환한다" {
        val params = listOf(
            ClassInfo.ParameterInfo("foo", "String"),
            ClassInfo.ParameterInfo("bar", "Int")
        )
        val result = TemplateUtils.buildMockDeclarations(params)
        result shouldBe "    val foo: String = mockk(relaxed = true)\n    val bar: Int = mockk(relaxed = true)"
    }
}) 