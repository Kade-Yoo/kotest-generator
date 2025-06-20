# Kotest Generator 리팩토링 & 개선 요약

## 1단계: 템플릿 생성기 분리
- TemplateGenerator 인터페이스 도입
- ApiTemplateGenerator, ServiceTemplateGenerator, RepositoryTemplateGenerator, DefaultTemplateGenerator로 분리
- SpecTemplateUtil에서 전략 패턴 적용

## 2단계: 공통 유틸리티 분리
- TemplateUtils: 패키지/임포트/Mock 선언 등 공통 템플릿 유틸리티
- UrlGenerator: URL/path variable 생성 로직 분리
- AnnotationAnalyzer: PSI 어노테이션 분석 전담
- TemplateConstants: 하드코딩 값 상수화

## 3단계: 긴 메서드 분리
- ApiTemplateGenerator, ServiceTemplateGenerator의 긴 메서드를 작은 단위로 분리
- 데이터 클래스(ApiTestData, ServiceTestData) 도입
- 섹션별 빌더 클래스 도입 (ApiTestSectionBuilder, ServiceTestSectionBuilder)

## 4단계: 타입 안전성 개선
- SpringAnnotation enum 도입 (문자열 → enum 변환)
- ComponentType, AnnotationAnalyzer 등에서 enum 기반 비교로 변경

## 5단계: 예외 처리 강화
- TemplateGenerationException 커스텀 예외 도입
- AnnotationAnalyzer 등에서 구체적 예외 메시지 및 방어적 코드 적용

## 6단계: 성능 최적화
- SpringAnnotation 변환 캐싱(Map)
- AnnotationAnalyzer 파라미터별 어노테이션 변환 캐싱
- TemplateUtils import 캐싱

## 7단계: 테스트 코드 작성
- SpringAnnotation, TemplateUtils 등 핵심 유틸리티 단위 테스트 작성
- 모든 테스트 통과

---

## 단계별 주요 효과

| 단계 | 효과 | 개선 전 | 개선 후 |
|------|------|---------|---------|
| 1    | 책임 분리, 확장성 | 단일 클래스 | 전략 패턴, 생성기 분리 |
| 2    | 중복 제거, 재사용 | 중복/하드코딩 | 유틸리티/상수화 |
| 3    | 가독성, 유지보수 | 긴 메서드 | 작은 단위 분리 |
| 4    | 타입 안전성 | 문자열 비교 | enum 기반 비교 |
| 5    | 에러 추적, 방어 | 불명확 | 명확한 예외, 메시지 |
| 6    | 성능 | 반복 변환 | 캐싱, 1회 변환 |
| 7    | 신뢰성 | 테스트 없음 | 단위 테스트 통과 |

---

## 코드 구조 변화 (예시)

```text
// Before
SpecTemplateUtil (모든 템플릿/로직 집중)

// After
- generator/
  - ApiTemplateGenerator, ServiceTemplateGenerator, ...
- util/
  - TemplateUtils, UrlGenerator, AnnotationAnalyzer
- common/
  - annotation/SpringAnnotation, constants/TemplateConstants
- exception/TemplateGenerationException
- test/
  - ...Test.kt
```

---

## 중복 코드 체크 및 개선 제안

### 중복 코드 예시
- 파라미터 어노테이션 변환: `param.annotations.mapNotNull { SpringAnnotation.fromString(it) }.toSet()`
- import joinToString: `importNames.joinToString("\n") { "import $it" }`
- Mock 선언: `val ... = mockk(relaxed = true)`

### 개선 제안
- **파라미터 어노테이션 변환**: AnnotationAnalyzer에서 private inline fun으로 추출하여 재사용
- **import joinToString**: TemplateUtils에서 캐싱 활용(이미 적용)
- **Mock 선언**: TemplateUtils에서 메서드로 통일(이미 적용)

#### 예시 (중복 제거)
```kotlin
// AnnotationAnalyzer 내부
private inline fun <T> withSpringAnnotations(param: ParameterInfo, block: (Set<SpringAnnotation>) -> T): T {
    val springAnnotations = param.annotations.mapNotNull { SpringAnnotation.fromString(it) }.toSet()
    return block(springAnnotations)
}

// 사용 예시
parameters.filter { withSpringAnnotations(it) { anns -> anns.contains(annotation) } }
```

---

## 결론
- 구조적, 성능적, 안정성 면에서 모두 대폭 개선
- 중복 코드 최소화, 유지보수성/확장성/테스트 용이성 확보
- 추가 리팩토링/테스트/기능 확장도 용이한 구조로 완성 