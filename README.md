# 📘 Kotest Generator

<!-- Plugin description -->
**Kotest Generator**는 IntelliJ IDEA 플러그인으로, Kotlin 클래스와 메서드를 분석하여 Kotest 기반의 테스트 코드를 자동으로 생성해주는 도구입니다. Controller, Service, Repository 등 다양한 컴포넌트 타입에 맞는 테스트 템플릿을 제공하여 개발자의 테스트 코드 작성 시간을 단축하고 일관된 테스트 구조를 보장합니다.
<!-- Plugin description end -->

## 🎯 프로젝트 개요

### 목적
- **반복적인 테스트 코드 작성 시간 단축**: 기본 테스트 구조를 자동 생성
- **일관된 테스트 구조 보장**: Kotest BehaviorSpec 기반의 표준화된 테스트 템플릿 제공
- **테스트 코드 작성 장벽 낮춤**: 복잡한 테스트 설정을 자동화하여 테스트 작성 접근성 향상

### 지원 컴포넌트
- **Controller**: Spring MVC Controller 테스트 (MockMvc 기반)
- **Service**: 비즈니스 로직 테스트 (MockK 기반)
- **Repository**: 데이터 접근 계층 테스트 (기본 템플릿)
- **기타 클래스**: 일반 클래스 및 메서드 테스트

## 🏗️ 아키텍처 및 구조

### 핵심 컴포넌트

```
src/main/kotlin/com/github/kadeyoo/kotestgenerator/
├── action/
│   └── GenerateSpecAction.kt          # IntelliJ 액션 핸들러 (Ctrl+Alt+G)
├── dispatcher/
│   └── SpecGeneratorDispatcher.kt     # PSI 요소 분석 및 적절한 생성기 선택
├── generator/
│   └── CodeSpecGenerator.kt           # 테스트 코드 생성 인터페이스
├── service/
│   ├── ClassService.kt                # 클래스 레벨 테스트 생성
│   └── MethodService.kt               # 메서드 레벨 테스트 생성
├── dto/
│   ├── ClassInfo.kt                   # 클래스 정보 데이터 클래스
│   ├── FunctionInfo.kt                # 함수 정보 데이터 클래스
│   ├── ParameterInfo.kt               # 파라미터 정보
│   └── MappingInfo.kt                 # 매핑 정보 (URL, HTTP 메서드 등)
├── util/
│   ├── ClassExtractor.kt              # PSI에서 클래스 정보 추출
│   ├── FunctionExtractor.kt           # PSI에서 함수 정보 추출
│   ├── SpecTemplateUtil.kt            # 테스트 템플릿 생성
│   └── CodeGeneratorUtil.kt           # 코드 생성 유틸리티
├── common/
│   ├── code/
│   │   └── ComponentType.kt           # 컴포넌트 타입 열거형
│   ├── constants/
│   │   ├── Constants.kt               # 상수 정의
│   │   └── SpecTemplateConstants.kt   # 템플릿 상수
│   └── KotestGeneratorSettings.kt     # 플러그인 설정 관리
└── resources/
    └── META-INF/
        └── plugin.xml                 # IntelliJ 플러그인 설정
```

### 동작 흐름

1. **사용자 액션**: `Ctrl+Alt+G` 단축키 실행
2. **PSI 분석**: 현재 커서 위치의 PSI 요소 분석
3. **컴포넌트 타입 결정**: 어노테이션 기반으로 Controller/Service/Repository 구분
4. **테스트 생성**: 컴포넌트 타입에 맞는 테스트 템플릿 생성
5. **파일 생성/업데이트**: `src/test/kotlin` 디렉토리에 테스트 파일 생성 또는 기존 파일에 추가

## 🛠️ 기술 스택

### 핵심 기술
- **Kotlin**: 2.1+ (주요 개발 언어)
- **IntelliJ Platform**: 2024.2+ (플러그인 개발 플랫폼)
- **Kotest**: 5.9+ (테스트 프레임워크)
- **Gradle**: 8.5+ (빌드 도구)

### 주요 의존성
```kotlin
// 테스트 프레임워크
testImplementation("io.kotest:kotest-runner-junit5")
testImplementation("io.kotest:kotest-assertions-core")

// IntelliJ Platform
intellijPlatform {
    create("IU", "2024.2.6")
    bundledPlugins("com.intellij.java")
}
```

### 지원 환경
- **IDE**: IntelliJ IDEA 2024.2 이상
- **Kotlin**: 2.1+
- **Java**: 21+
- **OS**: Windows, macOS, Linux

## 🚀 개발 환경 설정

### 사전 요구사항
- JDK 21+
- IntelliJ IDEA 2024.2+
- Gradle 8.5+

### 로컬 개발 설정

1. **프로젝트 클론**
```bash
git clone https://github.com/Kade-Yoo/kotest-generator.git
cd kotest-generator
```

2. **Gradle 빌드**
```bash
./gradlew build
```

3. **플러그인 실행**
```bash
./gradlew runIde
```

4. **플러그인 테스트**
```bash
./gradlew test
```

### 빌드 및 배포

1. **플러그인 패키징**
```bash
./gradlew buildPlugin
```

2. **플러그인 검증**
```bash
./gradlew verifyPlugin
```

3. **플러그인 배포** (JetBrains Marketplace)
```bash
./gradlew publishPlugin
```

---

## ✅ 왜 kotest-generator 플러그인을 사용하는가?

- 테스트 코드는 유지보수성과 품질 향상을 위한 최소한의 안전장치입니다.
- `kotest-generator`는 반복적인 테스트 작성 시간을 줄이고, **기본 구조를 자동 생성**해 테스트 코드 작성 장벽을 낮춰줍니다.
---

## 📦 생성된 테스트 코드는 무엇을 테스트하나요?
### Controller
- API 경로 및 요청이 정상적으로 동작하는지 확인
- 잘못된 파라미터, 누락된 값 등의 경우 예외가 발생하는지 확인

### Service
- 비즈니스 로직이 정상적으로 동작하는지 확인
- 예외 상황에서 올바른 예외가 발생하는지 확인

#### Repository 테스트와 통합 테스트는 추후 제공될 예정입니다.

## 🧪 생성된 테스트 코드 예시
### Controller 테스트 예시 (Kotest - BehaviorSpec)
```kotlin
class UserControllerTest : BehaviorSpec({

    given("사용자 조회 API를 호출할 때") {
        val userId = 1L

        // Mock 설정
        every { userService.getUser(userId) } returns UserResponse(userId, "홍길동", 30)
        `when`("정상적인 요청을 보낸다면") {
            // API 호출 및 Mock 설정
            val result = mockMvc.get("/api/users/$userId")
                .andExpect { status { isOk() } }
                .andReturn()
            
            // 응답 상태 검증
            then("200 OK와 응답 데이터를 반환한다") {
                result.response.status shouldBe HttpStatus.OK
            }
        }

        // 예외 상황 설정
        every { userService.getUser(999L) } throws EntityNotFoundException("User not found")
        `when`("존재하지 않는 사용자 ID로 요청하면") {
            // API 호출 및 Mock 설정
            val result = mockMvc.get("/api/users/$userId")
                .andExpect { status { isOk() } }
                .andReturn()
            
            // 응답 상태 검증
            then("404 Not Found 예외가 발생한다") {
                result.response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
})
```

### Service 테스트 예시 (Kotest - BehaviorSpec)
```kotlin
class UserServiceTest : BehaviorSpec({

    given("사용자 조회 서비스가 호출될 때") {
        val userId = 1L

        // Mock 설정
        every { userRepository.findById(userId) } returns User(userId, "홍길동", 30)
        `when`("정상적인 사용자 ID로 조회하면") {
            val result = userService.getUser(userId)

            // 결과 값 검증
            then("사용자 정보를 반환한다") {
                result shouldNotBe null
                result.id shouldBe userId
                result.name shouldBe "홍길동"
            }
        }

        // 예외 상황 테스트
        every { userRepository.findById(999L) } throws EntityNotFoundException("User not found")
        `when`("존재하지 않는 사용자 ID로 조회하면") {
            // 예외 검증
            shouldThrow<EntityNotFoundException> {
                userService.getUser(999L)
            }
        }
    }
})
```
---

## 💡 더 의미 있는 테스트를 위해 할 수 있는 것
| 전략 | 설명 |
|------|------|
| ✔ 예외 상황 테스트 추가 | 존재하지 않는 ID, 잘못된 파라미터 등 |
| ✔ 응답 필드 값 상세 검증 | 단순 `isOk` 외에 실제 데이터 확인 |
| ✔ 다양한 케이스 분기 처리 | 조건문/루프 등이 있는 로직의 분기 테스트 |

---

## 🚀 kotest-generator 플러그인 실행 방법
- 테스트 코드를 작성하고 싶은 클래스(예: Controller/Service)를 열고
- 클래스 선언부 혹은 메소드 선언부에 커서를 두고 → **command + alt + G** 입력(Mac) 또는 **ctrl + alt + G** 입력(Windows)
- 자동으로 `test` 디렉토리에 Kotest 기반 테스트 코드가 생성

## 💬 자주 묻는 질문
### Q. generator가 만든 테스트 외에 뭘 더 작성해야 하나요?
A. 아래 질문에 스스로 답해보세요:
- 이 API는 어떤 실패 케이스가 있을까?
- 어떤 값이 들어오면 로직이 달라질까?
- 이 서비스는 어디서 예외가 발생할 수 있을까?

### Q. Mock이 너무 많아져요. 실환경 테스트는?
A. `@SpringBootTest`, TestContainer, Embedded DB 등을 도입해 실환경 테스트로 확장할 수 있습니다. 통합테스트 자동화도 추후 제공 예정입니다. :)

---

## 🧭 다음 단계로 테스트 실력을 높이려면?

- `given-when-then` 구조 익히기
- Mock과 실제 환경 테스트의 차이점 이해
- 단위 테스트 → 통합 테스트 → 인수 테스트로 확장 연습
- `kotest` DSL 문법 익히기

## 🧩 Kotest 지원 환경
- Kotlin: 2.1+
- kotest: 5.9+
- IntelliJ: 2024.2 이상 권장

## 💬 피드백 및 기여
- 사용 중 불편 사항이나 기능 요청은 [GitHub Issues](https://github.com/Kade-Yoo/kotest-generator/issues)에 남겨주세요.
- Pull Request도 언제든 환영합니다!

---

## 🔮 추가 개선 방향

1. **실제 코드 생성 결과에 대한 통합 테스트**
   - 플러그인 액션 실행 시 생성되는 테스트 코드의 실제 결과물을 검증하는 통합 테스트 추가
2. **플러그인 UI/UX 개선**
   - 설정 화면, 에러 메시지, 안내 문구 등 사용자 경험(UX) 강화
3. **코드 생성 템플릿 커스터마이즈 기능**
   - 사용자가 템플릿(테스트 구조, 네이밍, mock 스타일 등)을 직접 설정/확장할 수 있도록 지원
4. **Kotlin 최신 문법/코틀린 DSL 적극 활용**
   - 빌더 패턴, 확장 함수, sealed class 등 최신 Kotlin 기능 적극 도입
5. **코드 생성 품질 향상 (실제 프로젝트 반영)**
   - 실제 현업에서 많이 쓰는 테스트 패턴, mock 스타일, 예외 케이스 등 반영
6. **문서화 및 예제 강화**
   - README, CHANGELOG, CONTRIBUTING, ISSUE_TEMPLATE 등 오픈소스 표준 문서 강화
7. **CI/CD 및 배포 자동화**
   - GitHub Actions 등으로 테스트/빌드/배포 자동화
8. **성능/메모리 프로파일링 및 최적화**
   - 대형 프로젝트, 대용량 클래스에서도 빠른 속도 보장
9. **사용자 피드백/이슈 적극 반영**
   - 실제 사용자 피드백 수집 및 반영, 자주 요청되는 기능/불편사항 우선 개선
10. **플러그인 확장성(플랫폼/언어)**
    - JetBrains 계열 IDE, Java/Scala 등 JVM 언어 지원 확장 고려