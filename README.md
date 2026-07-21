# ShoujoPomodoro（少女番茄钟）

<div align="center">

<img src="app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" width="120" alt="App Icon" />

**🌸 A cute anime-girl-themed Pomodoro Timer for Android**

Built with Jetpack Compose & Material 3

</div>

---

## ✨ Features

### ⏱️ Pomodoro Timer
- Standard Pomodoro cycle: Focus (25 min) → Short Break (5 min) → Focus → Long Break (15 min)
- Smooth circular progress indicator with animated transitions
- Skip to next phase, pause/resume, and reset controls
- Customizable durations and cycle count in Settings

### 👧 Anime Girl Character
- Cute shoujo-style character drawn entirely with Compose Canvas
- **4 expressive states** that change based on timer status:
  - `IDLE` — blinking animation, gentle smile
  - `FOCUSING` — determined eyes, sweat drop accessory, sparkle effects
  - `RESTING` — closed happy eyes, soft "Zzz" bubbles
  - `ALERTING` — wide shocked eyes, exclamation marks, open mouth

### 📋 Task Management
- Create, complete, and delete tasks
- Set current task to focus on
- Tasks persisted with Room (SQLite)

### 🔔 Background Timer
- Foreground service keeps timer running when app is in background
- Notification shows remaining time and current phase
- Full-screen alert notification when timer completes
- Vibration alert (customizable)

### 🌐 Internationalization
- **English** and **Simplified Chinese (简体中文)** support
- Language switching without app restart

### 🎨 Theme
- Light & Dark theme support (follows system)
- Warm pink primary palette — gentle and comfortable

### ⚙️ Customizable Settings
- Focus duration: 5–60 minutes
- Short break: 1–15 minutes
- Long break: 5–30 minutes
- Cycles before long break: 1–10
- Reset to defaults

---

## 📸 Screenshots

| Timer (Focus) | Timer (Break) | Task List | Settings |
|:---:|:---:|:---:|:---:|
| Focus timer with character | Resting character | Task management | Duration settings |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|:-----------|:--------|
| **Kotlin** | Programming language |
| **Jetpack Compose** | Modern declarative UI toolkit |
| **Material 3** | Design system |
| **Room** | Local SQLite database for tasks |
| **DataStore Preferences** | Timer settings persistence |
| **Coroutines + Flow** | Asynchronous reactive data streams |
| **Navigation Compose** | In-app navigation |
| **Foreground Service** | Background timer execution |
| **Canvas API** | Custom character drawing |

---

## 📁 Project Structure

```
ShoujoPomodoro/
├── app/
│   └── src/main/
│       ├── java/com/shoujopomodoro/
│       │   ├── data/
│       │   │   ├── local/           # Room database, DAO, entities
│       │   │   ├── preferences/     # DataStore preferences
│       │   │   └── repository/      # Repository pattern implementation
│       │   ├── di/                  # Dependency injection (AppContainer, TimerStateHolder)
│       │   ├── domain/
│       │   │   ├── model/           # Domain models (Task, TimerSession, TimerPhase, CharacterState)
│       │   │   └── usecase/         # Business logic (PomodoroCycleUseCase, TaskListUseCase)
│       │   ├── notification/        # Notification channels & helpers
│       │   ├── service/             # Foreground service for background timer
│       │   ├── ui/
│       │   │   ├── component/       # Reusable composables (ShoujoCharacter, CircularTimer, etc.)
│       │   │   ├── navigation/      # NavGraph & screen routes
│       │   │   ├── screen/          # Screen composables + ViewModels (timer, tasklist, settings)
│       │   │   └── theme/           # Material 3 theme (colors, typography, shapes)
│       │   └── util/                # Constants, TimeFormatter
│       ├── res/                     # Resources (strings, icons, themes)
│       └── AndroidManifest.xml
├── build.gradle.kts                 # Root build config
├── settings.gradle.kts              # Project settings
└── gradle/                          # Gradle wrapper
```

### Architecture: MVVM + Repository + UseCase

```
┌──────────────────────┐
│   UI (Compose)        │  ← collects StateFlow
│   ViewModel           │  ← exposes UiState
├──────────────────────┤
│   UseCase             │  ← business logic
│   Repository          │  ← data source abstraction
├──────────────────────┤
│   Data Sources        │  ← Room, DataStore
└──────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or later
- **Android SDK** 35
- **JDK 17**
- **Gradle** 8.9+ (wrapper included)

### Build & Run

1. **Clone the repository**
   ```bash
   git clone git@github.com:lighthouse333/ShoujoPomodoro.git
   cd ShoujoPomodoro
   ```

2. **Open in Android Studio**
   - File → Open → select the `ShoujoPomodoro` directory
   - Wait for Gradle sync to complete

3. **Run on device/emulator**
   - Connect an Android device (API 24+) or launch an emulator
   - Click ▶️ Run, or press `Shift+F10`

4. **Build APK**
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

### System Requirements
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Compile SDK**: Android 15 (API 35)

---

## 📦 Dependencies

```toml
[versions]
compose-bom = "2024.12.01"
room = "2.6.1"
lifecycle = "2.8.7"
navigation = "2.8.5"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
```

Key libraries: Compose BOM, Material 3, Room, Lifecycle ViewModel, Navigation Compose, DataStore Preferences, Kotlin Coroutines.

---

## 🧪 Usage

1. **Start a focus session** — Tap the ▶️ play button on the Timer screen
2. **Add tasks** — Navigate to the task list (checklist icon) and add tasks to focus on
3. **Customize** — Go to Settings (gear icon) to adjust durations, cycles, and language
4. **Background timer** — The timer continues running via foreground service even when you leave the app
5. **Get notified** — When a session ends, a full-screen alert notification pops up

---

## 📄 License

```
MIT License

Copyright (c) 2024 lighthouse333

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👤 Author

- **lighthouse333** — [GitHub](https://github.com/lighthouse333)

---

<div align="center">

Made with ❤️ and Kotlin

*Stay focused, stay kawaii~* 🌸

</div>
