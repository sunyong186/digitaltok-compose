# 색상 값 일괄 변경 계획

프로젝트 전체에서 사용 중인 특정 회색 값(`0xFFE9E9E9` / `#E9E9E9`)을 새로운 색상(`0xFF767676` / `#767676`)으로 변경합니다.

## 제안된 변경 사항

### [UI 컴포넌트 변경 (Compose)]

다음 파일들에서 `Color(0xFFE9E9E9)`를 `Color(0xFF767676)`으로 변경합니다.

#### [MODIFY] [AuthStartScreen.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/auth/AuthStartScreen.kt)
- 버튼 비활성화 색상 변경

#### [MODIFY] [PasswordResetScreen.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/auth/PasswordResetScreen.kt)
- `buttonDisabled` 변수 및 테두리(border) 색상 변경

#### [MODIFY] [SignupScreen.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/auth/SignupScreen.kt)
- `buttonDisabled` 변수 색상 변경

#### [MODIFY] [DeviceFlowComponents.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/device/DeviceFlowComponents.kt)
- 인디케이터/배경 색상 변경 (3곳)

#### [MODIFY] [OnboardingComponents.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/onboarding/OnboardingComponents.kt)
- 비활성화 상태의 배경색 변경

### [리소스 변경 (XML)]

#### [MODIFY] [bg_btn_primary_disabled.xml](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/res/drawable/bg_btn_primary_disabled.xml)
- `<solid android:color="#E9E9E9" />`를 `#767676`으로 변경합니다.

## 검증 계획

### 자동 검증
- 프로젝트 빌드 수행하여 문법 오류가 없는지 확인합니다.

### 수동 검증
- 주요 화면(로그인, 회원가입, 온보딩 등)에서 비활성화된 버튼이나 배경색이 의도한 진한 회색(#767676)으로 변경되었는지 확인합니다.
