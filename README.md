# 📘 Kotest Generator 사용 및 테스트 작성 가이드
## ✅ 왜 이 플러그인을 사용하는가?

- 테스트 코드는 유지보수성과 품질 향상을 위한 최소한의 안전장치입니다.
- `kotest-generator`는 반복적인 테스트 작성 시간을 줄이고, **기본 구조를 자동 생성**해 테스트 코드 작성 장벽을 낮춰줍니다.
---

## 📦 생성된 테스트 코드는 무엇을 테스트하나요?
### Controller
- API 경로 및 요청이 정상적으로 동작하는지 확인
- 잘못된 파라미터, 누락된 값 등의 경우 예외가 발생하는지 확인

#### ✅ Service 테스트에서 Repository 의존 예외 처리 (Kotest - BehaviorSpec)

```kotlin
class UserServiceTest : BehaviorSpec({

    given("User 조회 요청 시") {

        `when`("Repository가 null을 반환한다면") {
            every { userRepository.findById(any()) } returns null

            then("EntityNotFoundException이 발생한다") {
                shouldThrow<EntityNotFoundException> {
                    userService.getUser(999L)
                }
            }
        }

        `when`("정상적인 ID를 전달할 경우") {
            every { userRepository.findById(1L) } returns User(1L, "홍길동")

            then("정상적으로 유저 정보를 반환한다") {
                val result = userService.getUser(1L)
                result.name shouldBe "홍길동"
                result.id shouldBe 1L
            }
        }
    }
})
```

## 🧪 테스트 코드를 확장하는 방법
### 🔹 정상 응답 값 상세 검증 추가
```kotlin
val result = service.getUser(1L)
result.name shouldBe "홍길동"
result.age shouldBe 30
```

### 🔹 예외 상황 추가 테스트
```kotlin
every { userRepository.findById(any()) } returns null
shouldThrow<EntityNotFoundException> {
    service.getUser(999L)
}
```

### 🔹 Mock 반환값 설정 예시
```kotlin
every { userRepository.findById(1L) } returns User(1L, "홍길동")
```

---

## 💡 더 의미 있는 테스트를 위해 할 수 있는 것
| 전략 | 설명 |
|------|------|
| ✔ 예외 상황 테스트 추가 | 존재하지 않는 ID, 잘못된 파라미터 등 |
| ✔ 응답 필드 값 상세 검증 | 단순 `isOk` 외에 실제 데이터 확인 |
| ✔ 다양한 케이스 분기 처리 | 조건문/루프 등이 있는 로직의 분기 테스트 |

---

## 💬 자주 묻는 질문
### Q. generator가 만든 테스트 외에 뭘 더 작성해야 하나요?
A. 아래 질문에 스스로 답해보세요:
- 이 API는 어떤 실패 케이스가 있을까?
- 어떤 값이 들어오면 로직이 달라질까?
- 이 서비스는 어디서 예외가 발생할 수 있을까?

### Q. Mock이 너무 많아져요. 실환경 테스트는?
A. `@SpringBootTest`, TestContainer, Embedded DB 등을 도입해 실환경 테스트로 확장할 수 있습니다.

---

## 🛠 예시 코드 (Before → After)

### ✅ Controller 테스트 예시 (Kotest - BehaviorSpec)

```kotlin
class QnaControllerTest : BehaviorSpec({

    given("QNA 상세 API를 호출할 때") {
        val seq = 1L

        `when`("정상적인 요청을 보낸다면") {
            every { qnaService.getDetail(seq) } returns QnaDetailResponse(...)

            then("200 OK와 응답 데이터를 반환한다") {
                val result = mockMvc.get("/api/qna/$seq")
                    .andExpect { status { isOk() } }
                    .andReturn()

                val response = mapper.readValue<ApiResponse<QnaDetailResponse>>(result.response.contentAsByteArray)
                response.data.shouldNotBeNull()
            }
        }
    }
})
```

### ✅ Service 테스트 예시 (Kotest - BehaviorSpec)

```kotlin
class QnaServiceTest : BehaviorSpec({

    given("존재하지 않는 QNA seq로 조회하면") {
        val invalidSeq = 999L

        `when`("Repository에서 null을 반환하면") {
            every { qnaRepository.findByIdOrNull(invalidSeq) } returns null

            then("EntityNotFoundException 예외가 발생해야 한다") {
                shouldThrow<EntityNotFoundException> {
                    qnaService.getDetail(invalidSeq)
                }
            }
        }
    }
})
```

---

## 🧭 다음 단계로 테스트 실력을 높이려면?

- `given-when-then` 구조 익히기
- Mock과 실제 환경 테스트의 차이점 이해
- 단위 테스트 → 통합 테스트 → 인수 테스트로 확장 연습
- `kotest` DSL 문법 익히기