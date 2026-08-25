# Implementation Plan - Add @Preview for AppNavHost

This plan outlines the steps to add a Compose Preview for the `AppNavHost` function in `MainActivity.kt`. Following the system instructions, we will extract a stateless version of the Composable to make it previewable.

## Proposed Changes

### [MainActivity.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/MainActivity.kt)

#### [MODIFY] [MainActivity.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/MainActivity.kt)
- Extract the core logic of `AppNavHost` into a new `@Composable` function `AppNavHostContent`.
- `AppNavHostContent` will take parameters for its state and actions:
    - `navController: NavHostController`
    - `isOnboardingDone: Boolean` (extracted from `OnboardingPrefs`)
    - `onOnboardingFinish: () -> Unit` (extracted side effect)
    - `mainViewModel: MainViewModel` and `mainUiViewModel: MainUiViewModel` (passed down to `HomeScreen`)
- Update `AppNavHost` to call `AppNavHostContent`.
- Add `@Preview` functions for `AppNavHostContent` at the bottom of the file.
    - `AppNavHostPreview`: Shows the onboarding screen.
    - `AppNavHostLoginPreview`: Shows the login screen.

## Verification Plan

### Manual Verification
- Render the newly created previews using `render_compose_preview`.
- Verify that `AppNavHostPreview` shows the `OnboardingScreen` and `AppNavHostLoginPreview` shows the `AuthStartScreen`.
- Ensure the app still builds and runs correctly.
