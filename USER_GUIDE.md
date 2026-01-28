# 📘 Kotest Generator - 사용자 가이드

## 🎯 소개

**Kotest Generator**는 Kotlin 프로젝트에서 테스트 코드를 자동으로 생성해주는 IntelliJ IDEA 플러그인입니다.

### 주요 기능
- ✅ **자동 테스트 생성**: Controller, Service, Repository 등의 테스트 코드를 자동으로 생성
- ✅ **Kotest BehaviorSpec 기반**: Given-When-Then 구조의 읽기 쉬운 테스트 코드
- ✅ **시간 절약**: 반복적인 테스트 보일러플레이트 작성 시간 단축
- ✅ **일관된 구조**: 프로젝트 전체에 걸쳐 표준화된 테스트 패턴 유지

---

## 📦 설치 방법

### IntelliJ IDEA Marketplace에서 설치 (권장)

1. IntelliJ IDEA를 실행합니다
2. `Settings/Preferences` → `Plugins` 메뉴로 이동
3. `Marketplace` 탭에서 **"Kotest Generator"** 검색
4. `Install` 버튼 클릭
5. IDE 재시작

### 수동 설치

1. [GitHub Releases](https://github.com/Kade-Yoo/kotest-generator/releases)에서 최신 `.zip` 파일 다운로드
2. IntelliJ IDEA에서 `Settings/Preferences` → `Plugins` 메뉴로 이동
3. ⚙️ 아이콘 클릭 → `Install Plugin from Disk...` 선택
4. 다운로드한 `.zip` 파일 선택
5. IDE 재시작

---

## 🚀 사용 방법

### 기본 사용법

1. **테스트를 생성하고 싶은 Kotlin 클래스 또는 메서드를 엽니다**
   - Controller, Service 등 어떤 클래스든 가능합니다

2. **커서를 클래스 또는 메서드 선언부에 위치시킵니다**
   ```kotlin
   @RestController
   class UserController {  // ← 여기에 커서를 두거나
       
       @GetMapping("/users/{id}")
       fun getUser(@PathVariable id: Long): UserResponse {  // ← 여기에 커서를 둡니다
           // ...
       }
   }
   ```

3. **단축키를 입력합니다**
   - **macOS**: `⌘ + ⌥ + G` (Command + Option + G)
   - **Windows/Linux**: `Ctrl + Alt + G`

4. **자동으로 테스트 파일이 생성됩니다**
   - `src/test/kotlin` 디렉토리에 테스트 파일이 생성되거나 기존 파일에 추가됩니다

---

## 📝 생성되는 테스트 예시

### Controller 테스트

**원본 코드:**
```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        return userService.getUser(id)
    }
}
```

**생성되는 테스트:**
```kotlin
class UserControllerTest : BehaviorSpec({
    
    given("사용자 조회 API를 호출할 때") {
        val userId = 1L
        
        every { userService.getUser(userId) } returns UserResponse(userId, "홍길동", 30)
        
        `when`("정상적인 요청을 보낸다면") {
            val result = mockMvc.get("/api/users/$userId")
                .andExpect { status { isOk() } }
                .andReturn()
            
            then("200 OK와 응답 데이터를 반환한다") {
                result.response.status shouldBe HttpStatus.OK
            }
        }
        
        every { userService.getUser(999L) } throws EntityNotFoundException("User not found")
        
        `when`("존재하지 않는 사용자 ID로 요청하면") {
            val result = mockMvc.get("/api/users/999")
                .andExpect { status { isNotFound() } }
                .andReturn()
            
            then("404 Not Found 예외가 발생한다") {
                result.response.status shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
})
```

### Service 테스트

**원본 코드:**
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUser(id: Long): UserResponse {
        val user = userRepository.findById(id)
            ?: throw EntityNotFoundException("User not found")
        return UserResponse.from(user)
    }
}
```

**생성되는 테스트:**
```kotlin
class UserServiceTest : BehaviorSpec({
    
    given("사용자 조회 서비스가 호출될 때") {
        val userId = 1L
        
        every { userRepository.findById(userId) } returns User(userId, "홍길동", 30)
        
        `when`("정상적인 사용자 ID로 조회하면") {
            val result = userService.getUser(userId)
            
            then("사용자 정보를 반환한다") {
                result shouldNotBe null
                result.id shouldBe userId
                result.name shouldBe "홍길동"
            }
        }
        
        every { userRepository.findById(999L) } throws EntityNotFoundException("User not found")
        
        `when`("존재하지 않는 사용자 ID로 조회하면") {
            shouldThrow<EntityNotFoundException> {
                userService.getUser(999L)
            }
        }
    }
})
```

---

## 💡 생성된 테스트를 더 의미 있게 만들기

생성된 테스트는 **기본 구조**를 제공합니다. 다음과 같이 개선할 수 있습니다:

| 개선 방법 | 설명 | 예시 |
|----------|------|------|
| **예외 상황 추가** | 다양한 실패 케이스 테스트 | 잘못된 파라미터, null 값, 권한 없음 등 |
| **응답 검증 강화** | 상태 코드뿐만 아니라 실제 데이터 검증 | 응답 필드 값, 배열 크기, 데이터 형식 등 |
| **엣지 케이스 테스트** | 경계값, 특수 문자, 빈 값 등 | 0, 음수, 빈 문자열, 최댓값 등 |
| **비즈니스 로직 검증** | 도메인 규칙이 올바르게 적용되는지 확인 | 상태 전환, 계산 로직, 유효성 검사 등 |

### 개선 예시

```kotlin
// 생성된 기본 테스트
then("200 OK와 응답 데이터를 반환한다") {
    result.response.status shouldBe HttpStatus.OK
}

// 개선된 테스트
then("200 OK와 올바른 사용자 정보를 반환한다") {
    result.response.status shouldBe HttpStatus.OK
    val response = objectMapper.readValue<UserResponse>(result.response.contentAsString)
    response.id shouldBe userId
    response.name shouldBe "홍길동"
    response.age shouldBe 30
    response.email shouldContain "@"
}
```

---

## ⚙️ 시스템 요구사항

### 필수 요구사항
- **IntelliJ IDEA**: 2024.2 이상
- **Kotlin**: 2.1 이상
- **JDK**: 21 이상

### 지원 OS
- ✅ Windows
- ✅ macOS
- ✅ Linux

### 권장 환경
- **Kotest**: 5.9 이상
- **Spring Boot**: 3.0 이상 (Controller 테스트 생성 시)

---

## ❓ 자주 묻는 질문 (FAQ)

### Q1. 어떤 종류의 클래스를 지원하나요?

**A:** 다음 컴포넌트 타입을 지원합니다:
- `@RestController`, `@Controller` - Spring MVC Controller
- `@Service` - Service 계층
- 일반 Kotlin 클래스 및 메서드

### Q2. 생성된 테스트 외에 무엇을 더 작성해야 하나요?

**A:** 다음 질문에 스스로 답해보세요:
- 이 API는 어떤 실패 케이스가 있을까?
- 어떤 값이 들어오면 로직이 달라질까?
- 이 서비스는 어디서 예외가 발생할 수 있을까?
- 경계값(0, 음수, 최댓값 등)에서는 어떻게 동작할까?

### Q3. Mock이 너무 많아지는데, 실제 환경 테스트는 어떻게 하나요?

**A:** 다음 방법을 고려해보세요:
- `@SpringBootTest`를 사용한 통합 테스트
- TestContainer를 활용한 실제 DB 테스트
- Embedded DB (H2, TestContainers 등) 사용
- 통합 테스트 자동화 기능은 추후 제공 예정입니다

### Q4. 기존 테스트 파일이 있으면 어떻게 되나요?

**A:** 기존 파일이 있으면 새로운 테스트가 **추가**됩니다. 기존 테스트는 덮어쓰지 않습니다.

### Q5. 단축키를 변경할 수 있나요?

**A:** 네, 가능합니다:
1. `Settings/Preferences` → `Keymap`
2. "Generate Kotest Spec" 검색
3. 원하는 단축키로 변경

### Q6. 테스트가 생성되지 않아요!

**A:** 다음을 확인해보세요:
- 커서가 클래스 또는 메서드 선언부에 있는지 확인
- Kotlin 파일인지 확인 (Java는 현재 미지원)
- IntelliJ IDEA 버전이 2024.2 이상인지 확인
- 플러그인이 올바르게 설치되었는지 확인

---

## 🎓 다음 단계

### Kotest 학습 리소스
- [Kotest 공식 문서](https://kotest.io/)
- [BehaviorSpec 가이드](https://kotest.io/docs/framework/testing-styles.html#behavior-spec)
- [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html)

### 테스트 작성 실력 향상
1. **Given-When-Then 구조 익히기**
   - Given: 테스트 전제 조건
   - When: 실제 실행 동작
   - Then: 결과 검증

2. **Mock vs 실제 환경 이해**
   - 단위 테스트: Mock 사용
   - 통합 테스트: 실제 환경 사용

3. **테스트 피라미드 이해**
   - 단위 테스트 (많이)
   - 통합 테스트 (적당히)
   - E2E 테스트 (최소한)

---

## 🐛 문제 신고 및 기능 요청

### 버그 리포트
문제가 발생하면 [GitHub Issues](https://github.com/Kade-Yoo/kotest-generator/issues)에 다음 정보와 함께 제보해주세요:
- IntelliJ IDEA 버전
- Kotlin 버전
- 플러그인 버전
- 재현 방법
- 오류 메시지 (있는 경우)

### 기능 요청
새로운 기능이 필요하시면 [GitHub Issues](https://github.com/Kade-Yoo/kotest-generator/issues)에 다음 내용을 포함해주세요:
- 원하는 기능 설명
- 사용 사례
- 예상되는 동작
---

## 📞 지원 및 커뮤니티

- **GitHub**: [Kade-Yoo/kotest-generator](https://github.com/Kade-Yoo/kotest-generator)
- **Issues**: [문제 신고 및 기능 요청](https://github.com/Kade-Yoo/kotest-generator/issues)

---

**Happy Testing! 🎉**

테스트 코드 작성이 더 이상 부담스럽지 않도록, Kotest Generator가 함께합니다.
