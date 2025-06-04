package com.github.kadeyoo.kotestgenerator.common.code

enum class ComponentType {
    API, SERVICE, REPOSITORY, UNKNOWN;

    companion object {
        fun determineByAnnotations(annotations: List<String>): ComponentType =
            when {
                annotations.any { it.endsWith("RestController") || it.endsWith("Controller") } -> API
                annotations.any { it.endsWith("Service") } -> SERVICE
                annotations.any { it.endsWith("Repository") } -> REPOSITORY
                else -> UNKNOWN
            }
    }
}