# kotest-generator

![Build](https://github.com/Kade-Yoo/kotest-generator/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "kotest-generator"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/Kade-Yoo/kotest-generator/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation

# 📘 Kotest Generator 사용 및 테스트 작성 가이드
## ✅ 왜 이 플러그인을 사용하는가?

- 테스트 코드는 유지보수성과 품질 향상을 위한 최소한의 안전장치입니다.
- `kotest-generator`는 반복적인 테스트 작성 시간을 줄이고, **기본 구조를 자동 생성**해 테스트 코드 작성 장벽을 낮춰줍니다.
---

## 📦 생성된 테스트 코드는 무엇을 테스트하나요?
### Controller
- API 경로 및 요청이 정상적으로 동작하는지 확인
- 잘못된 파라미터, 누락된 값 등의 경우 예외가 발생하는지 확인

### Repository (예시용 템플릿)
- 추후 확장을 위해 기본 Given/When/Then 구조 제공
---

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
| ✔ 인증/인가 테스트 | 시큐리티 적용된 경우 허용/거부 흐름 테스트 |

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

```kotlin
// ✅ 생성된 코드 (기본)
val result = service.getUser(1L)
result shouldBe expected

// 🔧 확장 예시
val result = service.getUser(1L)
result.id shouldBe 1L
result.name shouldBe "홍길동"
```

---

## 🧭 다음 단계로 테스트 실력을 높이려면?

- `given-when-then` 구조 익히기
- Mock과 실제 환경 테스트의 차이점 이해
- 단위 테스트 → 통합 테스트 → 인수 테스트로 확장 연습
- `kotest` DSL 문법 익히기