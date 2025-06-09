package com.github.kadeyoo.kotestgenerator.dto

class ClassInfo (
    val name: String,
    val parameters: List<ParameterInfo>,
    val importNames: List<String>,
    val packageName: String,
    val requestMappingUrl: String = "",
) {
    data class ParameterInfo(
        val name: String,
        val type: String,
    )
}