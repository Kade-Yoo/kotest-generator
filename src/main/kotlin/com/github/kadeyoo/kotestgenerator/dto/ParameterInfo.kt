package com.github.kadeyoo.kotestgenerator.dto

data class ParameterInfo(
    val name: String,
    val type: String,
    val importName: String = "",
    val annotations: List<String> = emptyList()
)