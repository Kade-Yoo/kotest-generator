package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.openapi.components.Service
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

@Service(Service.Level.PROJECT)
class MethodService: CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean {
        return psiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction"
    }

    override fun generateSpec(psiElement: PsiElement, componentType: ComponentType): String {
        val name = (psiElement as? PsiNamedElement)?.name
        val packageName = JavaDirectoryService.getInstance().getPackage(psiElement.containingFile.containingDirectory)?.qualifiedName

        return when(componentType) {
            ComponentType.API -> generateApiSpec(packageName, name)
            ComponentType.SERVICE -> generateServiceSpec(packageName, name)
            ComponentType.REPOSITORY -> generateRepositorySpec(packageName, name)
            else -> generateDefaultSpec(packageName, name)
        }
    }

    private fun generateApiSpec(packageName: String?, name: String?): String = buildString {
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine(
            """
        import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
        import org.springframework.beans.factory.annotation.Autowired
        import org.springframework.test.web.servlet.MockMvc
        import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
        import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
        import io.kotest.core.spec.style.BehaviorSpec
    """.trimIndent()
        )
        appendLine()
        appendLine("@WebMvcTest(className::class)")
        appendLine("@AutoConfigureMockMvc")
        appendLine("class classNameApiTest(")
        appendLine("    @Autowired val mockMvc: MockMvc")
        appendLine(") : BehaviorSpec({")
        appendLine("""    given("$name 호출 시") {""")
        appendLine("""        When("정상적인 요청을 보낼 경우") {""")
        appendLine("""            then("정상적인 응답을 반환한다") {""")
        appendLine("""                val result = mockMvc.perform(get("/TODO"))""")
        appendLine("""                    .andExpect(status().isOk)""")
        appendLine("""                    .andReturn()""")
        appendLine("""                println(result.response.contentAsString)""")
        appendLine("            }")
        appendLine("        }")
        appendLine("    }")
        appendLine("})")
    }

    private fun generateServiceSpec(packageName: String?, name: String?): String = buildString {
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine()
        appendLine("class classNameTest : BehaviorSpec({")
        appendLine("""    given("$name 호출 시") {""")
        appendLine("""        When("조건이 주어졌을 때") {""")
        appendLine("""            Then("비즈니스 로직이 수행되어야 한다") { }""")
        appendLine("        }")
        appendLine("    }")
        appendLine("})")
    }

    private fun generateRepositorySpec(packageName: String?, name: String?): String = buildString {
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine()
        appendLine("class classNameTest : BehaviorSpec({")
        appendLine("""    given("Repository className::$name 호출 시") {""")
        appendLine("""        When("데이터 접근이 수행되면") {""")
        appendLine("""            Then("DB와 정상적으로 상호작용해야 한다") { }""")
        appendLine("        }")
        appendLine("    }")
        appendLine("})")
    }

    private fun generateDefaultSpec(packageName: String?, name: String?): String = buildString {
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine()
        appendLine("class className : BehaviorSpec({")
        appendLine("""    given("$name") {""")
        appendLine("""        then("should do something") { }""")
        appendLine("    }")
        appendLine("})")
    }
}