package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GIVEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_BEHAVIOR_SPEC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCKK_EVERY
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.THEN
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.util.TemplateUtils

class DefaultTemplateGenerator : TemplateGenerator {

    override fun generate(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        val body = buildString {
            for (fn in functionInfos) {
                appendLine("    $GIVEN(\"${fn.name}\") {")
                appendLine("        $$THEN(\"should do something\") { }")
                appendLine("    }")
            }
        }
        append(
            TemplateUtils.buildTestClass(
            classInfo = classInfo,
            additionalImports = "$IMPORT_BEHAVIOR_SPEC\n$IMPORT_MOCKK_EVERY",
            body = body
        ))
    }
} 