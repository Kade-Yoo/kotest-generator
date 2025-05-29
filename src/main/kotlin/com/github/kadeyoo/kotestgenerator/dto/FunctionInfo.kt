package com.github.kadeyoo.kotestgenerator.dto

data class FunctionInfo(
    val name: String,
    val parameters: List<Pair<String, String>>,
    val returnType: String,
    val mappingInfo: MappingInfo? = null
)