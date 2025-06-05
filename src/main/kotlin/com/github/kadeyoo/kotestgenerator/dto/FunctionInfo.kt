package com.github.kadeyoo.kotestgenerator.dto

data class FunctionInfo(
    val name: String,
    val parameters: List<ParameterInfo>,
    val returnType: String,
    val mappingInfo: MappingInfo,
)