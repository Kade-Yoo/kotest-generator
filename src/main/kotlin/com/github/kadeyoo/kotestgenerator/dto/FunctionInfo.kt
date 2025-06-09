package com.github.kadeyoo.kotestgenerator.dto

data class FunctionInfo(
    val name: String,
    val parameters: List<ParameterInfo>,
    val returnType: String,
    val mappingInfo: MappingInfo,
    val dependencyCall: List<DependencyCall> = emptyList()
) {
    data class DependencyCall(
        val name: String,
        val methodCall: String,
        val parameters: List<String>,
        val returnType: String
    )
}