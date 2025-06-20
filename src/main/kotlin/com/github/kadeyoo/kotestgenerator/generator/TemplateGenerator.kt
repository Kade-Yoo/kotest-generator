package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo

interface TemplateGenerator {
    fun generate(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String
} 