# 기기별 최적화된 반응형 레이아웃 적용

어떤 안드로이드 기기에서도 UI가 겹치지 않고 의도한 대로 보이도록 `DeviceConnectContent`의 구조를 개선했습니다.

## 주요 변경 사항

### 1. 유연한 스크롤 구조 복구
- 중간 영역에 `.verticalScroll(rememberScrollState())`를 다시 추가했습니다.
- 이를 통해 화면이 매우 작은 기기에서도 내용이 잘리지 않고 스크롤해서 모든 정보를 확인할 수 있습니다.

### 2. 가변 가중치(`weight(1f)`) 유지
- 상단 바와 하단 안내 박스는 자기 크기만큼만 차지하고, 중간 영역이 화면의 나머지 모든 공간을 유연하게 채우도록 설정했습니다.

### 3. 세로 중앙 정렬 (`Arrangement.Center`)
- 화면이 큰 기기(태블릿 등)에서 내용이 위로 쏠리지 않도록, 남는 공간의 정중앙에 내용이 배치되도록 수정했습니다.

### 4. 고정 간격 최적화
- 최상단 여백을 `73.dp`에서 `30.dp`로 줄여, 작은 화면에서 더 많은 정보가 한눈에 들어오도록 조정했습니다.

## 수정된 코드 구조
```kotlin
Column(modifier = Modifier.fillMaxSize()) {
    FlowTopAppBar(...) // 상단 고정

    Column(
        modifier = Modifier
            .weight(1f) // 유연한 공간 차지
            .verticalScroll(rememberScrollState()), // 필요 시 스크롤
        verticalArrangement = Arrangement.Center // 중앙 배치
    ) {
        // 이미지, 텍스트, 버튼 등 내용물
    }

    BottomInfoBox(...) // 하단 고정
}
```

이제 어떤 해상도의 기기에서도 겹침 현상 없이 깔끔한 UI를 경험하실 수 있습니다.
