# Walkthrough - Added @Preview for AppNavHost

I have successfully added `@Preview` functions for `AppNavHost` in `MainActivity.kt`. To achieve this while following best practices, I refactored the code to extract a stateless version of the navigation host.

## Changes Made

### [MainActivity.kt](file:///Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/MainActivity.kt)

- **Extracted `AppNavHostContent`**: Created a new private Composable that takes only the necessary state (`isOnboardingDone`) and actions (`onOnboardingFinish`, `homeScreen` lambda) instead of ViewModels directly. This follows the principle of separating state from UI logic for better testability and previewability.
- **Updated `AppNavHost`**: Modified the original `AppNavHost` to act as a stateful wrapper that reads from `OnboardingPrefs` and provides the real `HomeScreen` (with ViewModels) to `AppNavHostContent`.
- **Added Previews**: Added two new preview functions at the bottom of the file:
    - `AppNavHostOnboardingPreview`: Renders the app starting at the onboarding screen.
    - `AppNavHostLoginPreview`: Renders the app starting at the login screen.

## Verification Results

### Previews Rendered Successfully

````carousel
![AppNavHostOnboardingPreview](/Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/MainActivity_AppNavHostOnboardingPreview.png)
<!-- slide -->
![AppNavHostLoginPreview](/Users/iseon-yong/Desktop/프로젝트/digitaltok-compose/app/src/main/java/com/yourcompany/digitaltok/MainActivity_AppNavHostLoginPreview.png)
````

> [!NOTE]
> The `homeScreen` parameter in the previews is provided as a simple `Text` placeholder to avoid constructing `ViewModel`s within the preview scope, which is a recommended practice in Jetpack Compose.
