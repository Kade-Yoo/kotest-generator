package com.github.kadeyoo.kotestgenerator.common.annotation

/**
 * Spring Framework 어노테이션들을 타입 안전하게 관리하는 enum
 */
enum class SpringAnnotation(val value: String) {
    // Controller 관련
    CONTROLLER("Controller"),
    REST_CONTROLLER("RestController"),
    
    // Service 관련
    SERVICE("Service"),
    
    // Repository 관련
    REPOSITORY("Repository"),
    
    // Request 관련
    REQUEST_PARAM("RequestParam"),
    PATH_VARIABLE("PathVariable"),
    REQUEST_BODY("RequestBody"),
    REQUEST_MAPPING("RequestMapping"),
    GET_MAPPING("GetMapping"),
    POST_MAPPING("PostMapping"),
    PUT_MAPPING("PutMapping"),
    DELETE_MAPPING("DeleteMapping"),
    
    // 기타
    AUTOWIRED("Autowired"),
    COMPONENT("Component"),
    CONFIGURATION("Configuration");

    companion object {
        // 캐싱 맵
        private val stringToEnum: Map<String, SpringAnnotation> = values().associateBy { it.value }

        /**
         * 문자열로부터 SpringAnnotation을 찾습니다. (캐싱)
         */
        fun fromString(value: String): SpringAnnotation? = stringToEnum[value]
        
        /**
         * 문자열이 Spring 어노테이션인지 확인합니다. (캐싱)
         */
        fun isSpringAnnotation(value: String): Boolean = stringToEnum.containsKey(value)
        
        /**
         * Controller 관련 어노테이션인지 확인합니다.
         */
        fun isControllerAnnotation(annotation: SpringAnnotation): Boolean =
            annotation == CONTROLLER || annotation == REST_CONTROLLER
        
        /**
         * Service 관련 어노테이션인지 확인합니다.
         */
        fun isServiceAnnotation(annotation: SpringAnnotation): Boolean =
            annotation == SERVICE
        
        /**
         * Repository 관련 어노테이션인지 확인합니다.
         */
        fun isRepositoryAnnotation(annotation: SpringAnnotation): Boolean =
            annotation == REPOSITORY
        
        /**
         * Request 관련 어노테이션인지 확인합니다.
         */
        fun isRequestAnnotation(annotation: SpringAnnotation): Boolean =
            annotation == REQUEST_PARAM || annotation == PATH_VARIABLE || annotation == REQUEST_BODY
    }
} 