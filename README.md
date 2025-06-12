# 📘 Kotest Generator 사용 및 테스트 작성 가이드
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