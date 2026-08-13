# 기기 연결 플로우 UI 유동적 레이아웃 적용

고정된 수치의 여백 대신 **비율(Weight) 기반의 유연한 여백**을 적용하여, 어떤 화면 크기에서도 하단 버튼이 잘리지 않고 모든 콘텐츠가 한눈에 들어오도록 개선했습니다.

## 주요 변경 사항

### 1. 고정 여백에서 유동적 여백으로 전환
- `Spacer(height = 150.dp)` 또는 `200.dp`와 같은 큰 고정 여백을 모두 제거했습니다.
- 대신 `Spacer(Modifier.weight(1f))`를 사용하여, 화면의 남는 공간을 비율에 맞춰 유동적으로 나누어 가지도록 수정했습니다.

### 2. 하단 버튼 가시성 확보
- 이제 화면이 작은 기기에서는 여백이 스스로 줄어들고, 화면이 큰 기기에서는 여백이 넓어집니다.
- 결과적으로 `DeviceFailureContent` 하단의 '고객지원 문의하기' 버튼을 포함한 모든 버튼이 절대 잘리지 않고 항상 화면 하단에 고정된 것처럼 안전하게 배치됩니다.

### 3. 대상 화면 (DeviceFlowComponents.kt)
- **기기 연결 중 (`DeviceSearchingContent`)**: 로딩 인디케이터와 텍스트 배치 최적화.
- **연결 성공 (`DeviceSuccessContent`)**: 체크 아이콘과 완료 버튼 배치 최적화.
- **연결 실패 (`DeviceFailureContent`)**: 경고 아이콘, 해결 방법 박스, 하단 버튼들 간의 간격 비율 조정.

## 수정된 코드 컨셉
```kotlin
Column(modifier = Modifier.fillMaxSize()) {
    FlowTopAppBar(...) // 상단 고정

    Spacer(modifier = Modifier.weight(1f)) // 유동적 여백 1

    // 메인 콘텐츠 (이미지, 텍스트 등)

    Spacer(modifier = Modifier.weight(0.5f)) // 유동적 여백 2 (비율 조정 가능)

    // 하단 고정 영역 (버튼 등)
}
```

이제 어떤 기기에서도 "스크롤 없이" 모든 정보를 한 화면에서 깔끔하게 확인하실 수 있습니다!
