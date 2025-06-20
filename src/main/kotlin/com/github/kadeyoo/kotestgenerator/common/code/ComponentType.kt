package com.github.kadeyoo.kotestgenerator.common.code

import com.github.kadeyoo.kotestgenerator.common.annotation.SpringAnnotation

enum class ComponentType {
    API, SERVICE, REPOSITORY, UNKNOWN;

    companion object {
        /**
         * 어노테이션 목록으로부터 컴포넌트 타입을 결정합니다.
         */
        fun determineByAnnotations(annotations: List<String>): ComponentType {
            val springAnnotations = annotations
                .mapNotNull { SpringAnnotation.fromString(it) }
                .toSet()
            
            return when {
                springAnnotations.any { SpringAnnotation.isControllerAnnotation(it) } -> API
                springAnnotations.any { SpringAnnotation.isServiceAnnotation(it) } -> SERVICE
                springAnnotations.any { SpringAnnotation.isRepositoryAnnotation(it) } -> REPOSITORY
                else -> UNKNOWN
            }
        }

    }
}