package com.github.kadeyoo.kotestgenerator.dto

import com.github.kadeyoo.kotestgenerator.common.annotation.SpringAnnotation

data class ParameterInfo(
    val name: String,
    val type: String,
    val importName: String = "",
    val annotations: List<String> = emptyList()
) {
    /**
     * 파라미터의 어노테이션을 Set<SpringAnnotation>으로 변환하는 확장 함수
     */
    fun springAnnotations(): Set<SpringAnnotation> =
        annotations.mapNotNull { SpringAnnotation.fromString(it) }.toSet()
}