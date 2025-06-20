package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo

/**
 * URL 생성을 담당하는 유틸리티 클래스
 */
class UrlGenerator {
    
    /**
     * 전체 URL을 생성합니다.
     */
    fun generateFullUrl(
        prefixUrl: String, 
        url: String, 
        pathVars: List<String>, 
        isGoodUrl: Boolean, 
        paramAssignments: String
    ): String = buildString {
        append("\"$prefixUrl")
        append(generateUrl(url, pathVars, isGoodUrl))
        append(paramAssignments)
        append("\"")
    }
    
    /**
     * URL의 path variable을 치환합니다.
     */
    private fun generateUrl(url: String, pathVars: List<String>, isGoodUrl: Boolean): String {
        val regex = Regex("\\{\\w+}")
        val variableNames = regex.findAll(url).map { it.groupValues[0] }.toList()
        val variables = pathVars.map {
            if (isGoodUrl) CodeGeneratorUtil.dummyValue(it).replace("\"", "")
            else CodeGeneratorUtil.badDummyValue(it).replace("\"", "")
        }
        return variableNames.zip(variables).fold(url) { acc, (key, value) -> acc.replace(key, value) }
    }
    
    /**
     * RequestParam 파라미터 할당 문자열을 생성합니다.
     */
    fun buildParamAssignments(
        requestParams: List<ParameterInfo>,
        isGood: Boolean
    ): String {
        return requestParams.joinToString("&") {
            val value = if (isGood) {
                CodeGeneratorUtil.dummyValue(it.type).replace("\"", "")
            } else {
                CodeGeneratorUtil.badDummyValue(it.type).replace("\"", "")
            }
            "${it.name}=$value"
        }.let { if (it.isNotEmpty()) "?$it" else "" }
    }
} 