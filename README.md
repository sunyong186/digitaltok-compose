# DigitalTok - Compose Refactoring Project 🚀

> 기존 팀 프로젝트로 완성했던 NFC 스마트 키링 앱(DigitalTok)을 모던 안드로이드 기술 스택인 **Jetpack Compose**로 100% 마이그레이션하는 개인 리팩토링 프로젝트입니다.

## 🎯 프로젝트 리팩토링 목표

1. **100% Jetpack Compose 도입**
   - 29개 이상의 복잡한 XML 레이아웃과 파편화된 Fragment를 순수 Compose UI로 마이그레이션합니다.
   - 선언형 UI를 통한 상태(State) 관리 최적화 및 렌더링 성능 개선.

2. **외부 의존성 분리 및 Mock 환경 구축 (Testability 개선)**
   - 기존 앱은 실물 NFC 기기와 백엔드 서버가 켜져 있어야만 정상적인 UI 흐름을 테스트할 수 있었습니다.
   - 본 리팩토링에서는 하드웨어(NFC) 및 서버 의존성을 완전히 분리(Decoupling)하여, 가짜 데이터(Mock)만으로도 앱의 모든 UI 플로우와 비즈니스 로직을 검증할 수 있는 '독립적인 테스트 환경'을 구축했습니다.

3. **안드로이드 최신 권장 아키텍처 적용**
   - 불필요한 Adapter(RecyclerView) 및 ViewBinding 코드를 걷어내고, 상태 끌어올리기(State Hoisting) 및 단방향 데이터 흐름(UDF)을 적용합니다.

## 🛠 주요 마이그레이션 단계 (Phase)

- **Phase 0:** 의존성 분리 및 가짜(Mock) 데이터 환경 세팅
- **Phase 1:** Auth (로그인/회원가입) 및 Dialog 마이그레이션
- **Phase 2:** 기기 연결(NFC) 플로우 마이그레이션
- **Phase 3:** 핵심 비즈니스 로직 (키링 꾸미기 화면, 이미지 크롭 및 미리보기) 전환
- **Phase 4:** 설정 화면 및 불필요한 레거시(XML/Fragment) 코드 완전 제거

## 💡 레거시(원래 코드)와의 차이점
- 원본 팀 프로젝트: [https://github.com/DigitalTok/digitaltok-android]
- 본 레포지토리는 원본 코드를 포크(또는 복사)하여 **UI/UX 계층의 모던화** 및 **아키텍처 개선**에 온전히 집중한 개인 작업물입니다.
