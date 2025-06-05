package com.github.kadeyoo.kotestgenerator.dto

class ClassInfo (
    val name: String,
    val parameters: List<Pair<String, String>>,
    val importNames: List<String>,
    val packageName: String,
    val requestMappingUrl: String = "",
)