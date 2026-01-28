# 📜 Kotest Generator - 변경 이력

이 문서는 Kotest Generator 플러그인의 주요 변경 사항과 릴리스 히스토리를 기록합니다.

---

## [Unreleased]

### 계획된 기능
- Repository 테스트 자동 생성 지원
- 통합 테스트 자동 생성 기능
- 테스트 템플릿 커스터마이즈 기능
- 다국어 지원 (영어)

---

## [1.0.0] - 2026-01-28

### 🎉 첫 번째 정식 릴리스

#### ✨ 주요 기능
- **Controller 테스트 자동 생성**
  - Spring MVC Controller (`@RestController`, `@Controller`) 지원
  - MockMvc 기반 테스트 코드 생성
  - HTTP 메서드 및 경로 자동 추출
  - Given-When-Then 구조의 BehaviorSpec 테스트 생성

- **Service 테스트 자동 생성**
  - Service 계층 (`@Service`) 지원
  - MockK 기반 Mock 객체 자동 설정
  - 비즈니스 로직 테스트 템플릿 제공
  - 예외 상황 테스트 케이스 포함

- **단축키 지원**
  - macOS: `⌘ + ⌥ + G`
  - Windows/Linux: `Ctrl + Alt + G`

- **스마트 파일 관리**
  - `src/test/kotlin` 디렉토리에 자동 생성
  - 기존 테스트 파일이 있으면 추가 (덮어쓰기 안 함)
  - 패키지 구조 자동 유지

#### 🏗️ 아키텍처
- Hexagonal Architecture 기반 설계
- PSI (Program Structure Interface) 기반 코드 분석
- 컴포넌트 타입별 생성기 분리 (Dispatcher 패턴)

#### 📚 문서
- README.md: 프로젝트 개요 및 개발자 가이드
- USER_GUIDE.md: 사용자 가이드
- HISTORY.md: 변경 이력 관리

#### 🔧 기술 스택
- Kotlin 2.1+
- IntelliJ Platform 2024.2+
- Kotest 5.9+
- Gradle 8.5+

#### ⚙️ 시스템 요구사항
- IntelliJ IDEA 2024.2 이상
- JDK 21 이상
- Kotlin 2.1 이상

---

## 변경 이력 작성 가이드

### 버전 번호 규칙 (Semantic Versioning)
- **Major (X.0.0)**: 호환되지 않는 API 변경
- **Minor (0.X.0)**: 하위 호환되는 기능 추가
- **Patch (0.0.X)**: 하위 호환되는 버그 수정

### 변경 사항 카테고리
- `✨ Added`: 새로운 기능 추가
- `🔧 Changed`: 기존 기능 변경
- `🗑️ Deprecated`: 곧 제거될 기능
- `🚫 Removed`: 제거된 기능
- `🐛 Fixed`: 버그 수정
- `🔒 Security`: 보안 관련 수정

### 작성 예시

```markdown
## [1.1.0] - 2026-02-15

### ✨ Added
- Repository 테스트 자동 생성 기능 추가
- 테스트 템플릿 커스터마이즈 설정 UI 추가
- 영어 언어팩 지원

### 🔧 Changed
- Controller 테스트 생성 로직 개선
- 성능 최적화: 대형 클래스 분석 속도 30% 향상

### 🐛 Fixed
- 중첩 클래스에서 테스트 생성 시 패키지 경로 오류 수정
- Nullable 파라미터 처리 시 NPE 발생 문제 해결
- 특수 문자가 포함된 메서드명 처리 오류 수정

### 🔒 Security
- 의존성 보안 업데이트: Kotlin 2.1.5
```

---

## 참고 링크

- [GitHub Releases](https://github.com/Kade-Yoo/kotest-generator/releases)
- [Issues](https://github.com/Kade-Yoo/kotest-generator/issues)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)
