# 🛡️ Sylven Secure

**Privacy-first security tools for Android.**

Sylven Secure is an open-source Android application designed to help users protect their apps and keep their digital lives more private.

Built with **Kotlin** and **Jetpack Compose (Material 3)**, Sylven Secure combines a modern user interface with Android security features to provide a simple, accessible, and privacy-focused experience.

---

## ✨ Features

### 🔐 App Lock

Protect selected applications from unauthorized access.

- **PIN Lock:** Secure apps using a 4-digit numeric PIN.
- **Pattern Lock:** Unlock apps using a custom 3x3 pattern.
- **Password Lock:** Use a 8–12 character password for additional access protection.
- **Instant Background Enforcement:** Ultra-fast ~10ms background monitoring engine (`AppLockService`).
- **Session-Based Unlocking:** Temporarily unlock apps during an active session and automatically re-lock upon exit.
- **Task Isolation:** Standalone overlay window (`taskAffinity="com.sylvenos.tools.overlay"`) preventing app switcher leaks.
- **Brute-Force Protection:** Max 5 failed attempts limit.

---

## 🛠️ Permission Management

A guided setup experience helps users configure the permissions required for security features:

1. **Usage Access Permission**: Detect when protected apps are launched.
2. **Display Over Other Apps**: Show the lock overlay screen.
3. **Disable Battery Optimization**: Ensure background app lock runs smoothly with zero lag.

---

## 🧰 Tech Stack

Sylven Secure is built using modern Android development technologies.

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Declarative UI development |
| Material 3 | UI components and design system |
| Kotlin Coroutines & Flows | Asynchronous operations and reactive data handling |
| Android SDK | Platform APIs and system integration |
| UsageStatsManager | App usage and foreground activity detection |
| Gradle Kotlin DSL | Build configuration |

---

## 📂 Project Structure

```
com.sylvenos.tools/
├── MainActivity.kt                # Root activity & navigation stack controller
├── ui/
│   ├── applock/
│   │   ├── AppLock.kt             # App Lock list & toggle manager
│   │   ├── LockType.kt            # Lock selection (PIN, Pattern, Password)
│   │   ├── LockSecurityManager.kt # Preferences storage for lock credentials & locked packages
│   │   ├── AppLockService.kt      # Standalone background service for App Lock (~10ms)
│   │   ├── AppLockOverlayActivity # Full-screen isolated overlay lock activity
│   │   ├── LockVerification.kt    # Unified lock input verification screen
│   │   ├── PinSetup.kt            # 2-step PIN creation flow
│   │   ├── PatternSetup.kt        # 2-step Pattern canvas creation flow
│   │   ├── PasswordSetup.kt       # 2-step Password creation flow
│   │   └── AppManager.kt          # System PackageManager query helper
│   ├── permission/
│   │   └── PermissionScreen.kt    # Onboarding 3-step permission request screen
│   ├── splash/
│   │   └── SplashScreen.kt        # Splash screen animation
│   └── theme/                     # Compose Material3 styling, typography, and colors
```

---

## 📜 License

Sylven Secure is distributed under the Open Source License.
