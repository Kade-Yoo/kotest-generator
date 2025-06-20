package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.common.KotestGeneratorSettings
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.util.TemplateUtils

class RepositoryTemplateGenerator : TemplateGenerator {
    
    private val state = KotestGeneratorSettings.getInstance().state

    override fun generate(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        val entityName = classInfo.name.removeSuffix("Repository").removeSuffix("Jpa")
        val repositoryVar = classInfo.name.replaceFirstChar { it.lowercaseChar() }
        val body = buildString {
            appendLine("@DataJpaTest")
            appendLine("class ${classInfo.name}Test : BehaviorSpec({")
            appendLine("    @Autowired")
            appendLine("    lateinit var $repositoryVar: ${classInfo.name}")
            appendLine()
            appendLine("    given(\"엔티티 저장\") {")
            appendLine("        `when`(\"정상적으로 저장하면\") {")
            appendLine("            then(\"엔티티가 DB에 저장된다\") {")
            appendLine("                val entity = /* TODO: $entityName 예시 엔티티 생성 */")
            appendLine("                val saved = $repositoryVar.save(entity)")
            appendLine("                saved.id shouldNotBe null")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
            appendLine()
            appendLine("    given(\"엔티티 조회\") {")
            appendLine("        `when`(\"ID로 조회하면\") {")
            appendLine("            then(\"정상적으로 조회된다\") {")
            appendLine("                val entity = /* TODO: $entityName 예시 엔티티 생성 */")
            appendLine("                val saved = $repositoryVar.save(entity)")
            appendLine("                val found = $repositoryVar.findById(saved.id!!)")
            appendLine("                found.isPresent shouldBe true")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
            appendLine()
            appendLine("    given(\"엔티티 삭제\") {")
            appendLine("        `when`(\"삭제하면\") {")
            appendLine("            then(\"DB에서 삭제된다\") {")
            appendLine("                val entity = /* TODO: $entityName 예시 엔티티 생성 */")
            appendLine("                val saved = $repositoryVar.save(entity)")
            appendLine("                $repositoryVar.delete(saved)")
            appendLine("                $repositoryVar.findById(saved.id!!).isPresent shouldBe false")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
            appendLine("})")
        }
        append(
            TemplateUtils.buildTestClass(
                classInfo = classInfo,
                additionalImports = """import org.springframework.beans.factory.annotation.Autowired\nimport io.kotest.core.spec.style.BehaviorSpec\nimport io.kotest.matchers.shouldBe\nimport io.kotest.matchers.shouldNotBe""".trimIndent(),
                body = body
            )
        )
    }
} 