# 뒤로가기 버튼 아이콘 변경 계획

제공해주신 커스텀 뒤로가기 아이콘(벡터 드로어블)을 프로젝트 전체에서 사용하도록 변경합니다. 현재 프로젝트에는 이 아이콘이 이미 `ic_back.xml`로 존재하지만, 실제로는 Material Icons의 `ArrowBack`이나 다른 드로어블(`ic_left_arrow`)이 사용되고 있습니다.

## 제안된 변경 사항

### [리소스 변경]

#### [MODIFY] [common_top_app_bar.xml](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/res/layout/common_top_app_bar.xml)
- `backButton`의 `android:src`를 `@drawable/ic_left_arrow`에서 `@drawable/ic_back`으로 변경합니다.

### [UI 컴포넌트 변경 (Compose)]

#### [MODIFY] [PasswordResetScreen.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/auth/PasswordResetScreen.kt)
- `Icons.AutoMirrored.Filled.ArrowBack` 사용을 중단하고 `painterResource(id = R.drawable.ic_back)`를 사용하도록 수정합니다.
- 필요한 `R` 클래스 및 `painterResource` 임포트를 추가합니다.

#### [MODIFY] [SignupScreen.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/ui/auth/SignupScreen.kt)
- `Icons.Default.ArrowBack` 사용을 중단하고 `painterResource(id = R.drawable.ic_back)`를 사용하도록 수정합니다.

## 검증 계획

### 수동 검증
- `common_top_app_bar.xml`을 사용하는 Fragment들(기기 연결, 설정 등)에서 뒤로가기 버튼 아이콘이 변경되었는지 확인합니다.
- 회원가입 화면(`SignupScreen`)과 비밀번호 찾기 화면(`PasswordResetScreen`)에서 뒤로가기 버튼 아이콘이 변경되었는지 확인합니다.
- 빌드가 정상적으로 수행되는지 확인합니다.
