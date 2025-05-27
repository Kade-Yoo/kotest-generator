package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.openapi.components.Service
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

@Service(Service.Level.PROJECT)
class ClassService : CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean {
        return psiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtClass"
    }

    override fun generateSpec(psiElement: PsiElement, componentType: ComponentType): String {
        val className = (psiElement as? PsiNamedElement)?.name ?: "className"
        val methodNames = getMethodNames(psiElement)
        val packageName =
            JavaDirectoryService.getInstance().getPackage(psiElement.containingFile.containingDirectory)?.qualifiedName

        return when (componentType) {
            ComponentType.API -> generateApiTestCode(packageName, className, methodNames)
            ComponentType.SERVICE -> generateServiceTestCode(packageName, className, methodNames)
            else -> generateDefaultTestCode(packageName, className, methodNames)
        }
    }

    private fun generateDefaultTestCode(packageName: String?, className: String, methodNames: List<String>) =
        buildString {
            if (!packageName.isNullOrBlank()) {
                appendLine("package $packageName")
            }
            appendLine()
            appendLine("import io.kotest.core.spec.style.BehaviorSpec")
            appendLine()
            appendLine("class ${className}Test : BehaviorSpec({")
            for (fn in methodNames) {
                appendLine("""    given("$className::$fn 호출 시") {""")
                appendLine("""        then("기능이 정상 동작해야 한다") { }""")
                appendLine("    }")
            }
            appendLine("})")
        }

    private fun generateServiceTestCode(packageName: String?, className: String, methodNames: List<String>) =
        buildString {
            if (!packageName.isNullOrBlank()) {
                appendLine("package $packageName")
            }
            appendLine()
            appendLine("import io.kotest.core.spec.style.BehaviorSpec")
            appendLine()
            appendLine("class ${className}Test : BehaviorSpec({")
            for (fn in methodNames) {
                appendLine("""    given("$fn 호출 시") {""")
                appendLine("""        When("조건이 주어졌을 때") {""")
                appendLine("""            Then("비즈니스 로직이 수행되어야 한다") { }""")
                appendLine("        }")
                appendLine("    }")
            }
            appendLine("})")
        }

    private fun generateApiTestCode(packageName: String?, className: String, methodNames: List<String>) = buildString {
        if (!packageName.isNullOrBlank()) {
            appendLine("package $packageName")
        }
        appendLine()
        appendLine("import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest")
        appendLine("import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc")
        appendLine("import org.springframework.beans.factory.annotation.Autowired")
        appendLine("import org.springframework.test.web.servlet.MockMvc")
        appendLine("import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get")
        appendLine("import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status")
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine()
        appendLine("@WebMvcTest($className::class)")
        appendLine("@AutoConfigureMockMvc")
        appendLine("class ${className}ApiTest(")
        appendLine("    @Autowired val mockMvc: MockMvc")
        appendLine(") : BehaviorSpec({")
        for (fn in methodNames) {
            appendLine("""    given("$fn 호출 시") {""")
            appendLine("""        When("정상적인 요청을 보낼 경우") {""")
            appendLine("""            then("정상적인 응답을 반환한다") {""")
            appendLine("""                val result = mockMvc.perform(get("/TODO"))""")
            appendLine("""                    .andExpect(status().isOk)""")
            appendLine("""                    .andReturn()""")
            appendLine("""                println(result.response.contentAsString)""")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
        }
        appendLine("})")
    }

    private fun getMethodNames(psiElement: PsiElement): List<String> = extractFunctionNamesFromKtClass(psiElement)

    private fun extractFunctionNamesFromKtClass(psiClassElement: PsiElement): List<String> {
        return PsiTreeUtil.findChildrenOfType(psiClassElement, PsiElement::class.java)
            .filter { it.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction" }
            .mapNotNull { (it as? PsiNamedElement)?.name }
    }
}