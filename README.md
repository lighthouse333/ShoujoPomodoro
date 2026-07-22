# ShoujoPomodoro · 少女番茄钟 🌸

<div align="center">

**A Dreamy Anime-Girl-Themed Pomodoro Timer for Android · 一款梦幻少女主题的 Android 番茄钟应用**

Built with Jetpack Compose & Material 3 · 极致视觉体验

[English](#english) | [中文](#中文)

<img src="https://img.shields.io/badge/Kotlin-2.0.21-purple?logo=kotlin" alt="Kotlin" />
<img src="https://img.shields.io/badge/Compose-BOM_2024.12-blue?logo=jetpackcompose" alt="Compose" />
<img src="https://img.shields.io/badge/Material-3-pink?logo=materialdesign" alt="Material 3" />
<img src="https://img.shields.io/badge/Min_SDK-24-green?logo=android" alt="Min SDK" />
<img src="https://img.shields.io/badge/Target_SDK-34-orange?logo=android" alt="Target SDK" />
<img src="https://img.shields.io/badge/License-MIT-yellow" alt="License" />

</div>

---

<a name="english"></a>
## 🇬🇧 English

### ✨ What's New (July 2026 UI Overhaul)

The app has received a complete visual transformation — from a simple timer to a **dreamy shoujo experience**:

#### 🌸 Visual Effects System
| Feature | Description |
|---------|-------------|
| **Sakura Particle System** | Cherry blossom petals, sparkles, light motes, and hearts drift across the screen in real-time. Supports 4 particle types with physics-based motion ✨ |
| **AGSL Shader Backgrounds** | GPU-accelerated gradient backgrounds — aurora waves, soft pastel gradients, and starry night skies. Smooth animated color transitions at 60fps |
| **Celebration Effects** | 5 celebration styles on pomodoro completion: Confetti burst, Golden star shower, Sakura storm, Heart explosion, and Magic sparkle transformation 🎉 |
| **Glassmorphism UI** | Semi-transparent frosted glass panels for timer display, music bar, and cards with soft blur and shadows |

#### 👧 Enhanced Character System
| Feature | Description |
|---------|-------------|
| **4 Outfits** | Sailor uniform (focus), Pastel dress (short break), Magical girl (long break), Winter coat (seasonal) |
| **3 Hairstyles** | Twin tails, Long straight, Short bob — each with independent sway animations |
| **Rich Expressions** | Multi-layer eyes with highlights, dynamic blush intensity, state-based mouth shapes, eyebrow movement |
| **Glow Aura** | Pulsing colored aura around the character that changes with timer state |
| **Body Animation** | Breathing idle animation, blink (with random double-blink), micro head movements |

#### ⭕ Premium Timer
| Feature | Description |
|---------|-------------|
| **Multi-layer Glow** | Progress arc with inner and outer glow rings in phase-specific colors |
| **Orbiting Particles** | Sparkles orbit around the timer ring with physics-based motion |
| **Star Progress Markers** | 4 star markers at 0%, 25%, 50%, 75% that light up as progress advances |
| **Glow Tip Dot** | A bright glowing dot at the leading edge of the progress arc |
| **Gradient Sweep** | Smooth gradient coloring along the progress arc |

#### 🎛️ Premium Controls
| Feature | Description |
|---------|-------------|
| **Neumorphic Buttons** | Reset, Play/Pause, and Skip buttons with soft shadow depth |
| **Gradient Play Button** | Pink-to-coral gradient for the primary play/pause button |
| **Glass Music Bar** | Frosted glass container for the music player with rounded pill shape |

---

### 🛠️ Tech Stack

| Technology | Purpose |
|:-----------|:--------|
| **Kotlin 2.0** | Programming language |
| **Jetpack Compose** | Modern declarative UI toolkit |
| **Material 3** | Design system foundation |
| **Canvas API** | Custom character & particle rendering |
| **AGSL Shaders** | GPU-accelerated gradient backgrounds |
| **Room** | Local SQLite database for tasks |
| **DataStore** | Timer settings persistence |
| **Coroutines + Flow** | Reactive async data streams |
| **Navigation Compose** | In-app navigation |
| **Foreground Service** | Background timer execution |

---

### 📁 Project Structure

```
app/src/main/java/com/shoujopomodoro/
├── data/
│   ├── local/              # Room database, DAO, entities
│   ├── preferences/        # DataStore preferences
│   └── repository/         # Repository pattern
├── di/                     # DI container & state holder
├── domain/
│   ├── model/              # Domain models (Task, TimerSession, CharacterState, TimerPhase)
│   └── usecase/            # Business logic
├── notification/           # Notification channels
├── service/                # Foreground service, music player
├── ui/
│   ├── component/          # Reusable composables ★
│   │   ├── SakuraParticleBackground.kt   # Cherry blossom particle system
│   │   ├── GradientBackgrounds.kt        # AGSL shader backgrounds
│   │   ├── CelebrationOverlay.kt         # Completion celebration effects
│   │   ├── EnhancedShoujoCharacter.kt    # Full-featured character rendering
│   │   ├── PremiumCircularTimer.kt       # Multi-layer glow timer
│   │   └── PremiumControls.kt           # Neumorphic buttons & labels
│   ├── navigation/         # NavGraph & routes
│   ├── screen/             # Screens + ViewModels
│   │   ├── timer/          # Main timer screen
│   │   ├── tasklist/       # Task management
│   │   └── settings/       # Settings & music
│   └── theme/              # Color palette, typography, shapes
└── util/                   # Constants, time formatter
```

---

<a name="中文"></a>
## 🇨🇳 中文

### ✨ 2026年7月 UI 全面升级

应用经历了彻底的视觉改造——从简洁的计时器蜕变为**梦幻少女体验**：

#### 🌸 视觉特效系统
| 功能 | 说明 |
|------|------|
| **樱吹雪粒子系统** | 樱花花瓣、星光、光点、爱心实时飘落全屏，支持4种粒子类型，物理运动轨迹 ✨ |
| **AGSL 着色器背景** | GPU 加速渐变背景——极光波浪、柔光粉彩渐变、暗夜星空，60fps 流畅色彩过渡 |
| **庆祝特效** | 番茄钟完成时触发5种庆祝方式：彩纸爆裂、金色星星雨、樱吹雪暴风、爱心爆炸、魔法闪光 🎉 |
| **玻璃拟态 UI** | 半透明毛玻璃面板覆盖计时器数字、音乐条、卡片，柔和模糊与阴影 |

#### 👧 增强角色系统
| 功能 | 说明 |
|------|------|
| **4 套服装** | 水手服（专注）、洋装（短休）、魔法少女（长休）、冬装（季节限定） |
| **3 种发型** | 双马尾、长直发、短波波头——各有独立飘动动画 |
| **丰富表情** | 多层眼睛带高光、动态腮红强度、状态嘴型变化、眉毛运动 |
| **角色辉光** | 呼吸脉冲彩色光环，随计时器状态变化颜色 |
| **身体动画** | 待机呼吸、眨眼（含随机连眨）、微小头部晃动 |

#### ⭕ 高级计时器
| 功能 | 说明 |
|------|------|
| **多层光晕** | 进度弧带内外辉光环，不同阶段不同颜色 |
| **轨道粒子** | 沿计时器环旋转的星光粒子，物理运动 |
| **星星进度标记** | 0%/25%/50%/75% 处的4颗星标，到达点亮 |
| **光点指示** | 进度弧前端的明亮光点 |
| **渐变弧线** | 进度弧上的平滑渐变着色 |

#### 🎛️ 高级控件
| 功能 | 说明 |
|------|------|
| **新拟态按钮** | 重置/播放/跳过按钮带柔和阴影深度 |
| **渐变播放键** | 粉→珊瑚渐变的中央圆形播放按钮 |
| **毛玻璃音乐条** | 音乐播放器包裹在半透明磨砂容器中 |

---

### 🚀 快速开始

**环境要求：**
- Android Studio Hedgehog (2023.1.1) 以上
- Android SDK 35
- JDK 17

**构建运行：**
```bash
# 克隆仓库
git clone git@github.com:Crasor/ShoujoPomodoro.git
cd ShoujoPomodoro/ShoujoPomodoro

# 构建 APK
./gradlew assembleDebug

# 安装到模拟器
adb install app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.shoujopomodoro/.MainActivity
```

**系统要求：**
- 最低 SDK：Android 7.0 (API 24)
- 目标 SDK：Android 14 (API 34)
- 编译 SDK：Android 15 (API 35)

---

### 🎨 配色体系

| 颜色 | 色值 | 用途 |
|------|------|------|
| 樱花粉 `SakuraPink` | `#FFB7C5` | 主色调 |
| 深樱 `SakuraDeep` | `#FF69B4` | 强调/渐变 |
| 紫藤 `Wisteria` | `#D8BFD8` | 次要色 |
| 人鱼蓝 `MermaidBlue` | `#B2EBF2` | 长休 |
| 抹茶薄荷 `MatchaMint` | `#A5D6A7` | 短休 |
| 金平糖金 `KonpeitoGold` | `#FFD700` | 点缀/星星 |
| 蔷薇腮红 `RoseBlush` | `#F8BBD0` | 腮红/爱心 |

---

### 📝 使用指南

1. **开始专注** — 点击计时器页面的 ▶️ 播放按钮
2. **管理任务** — 点击右上角清单图标，添加要专注的任务
3. **自定义设置** — 点击齿轮图标，调整时长、循环次数、语言
4. **后台计时** — 离开应用后计时器通过前台服务继续运行
5. **完成通知** — 阶段结束时弹出全屏提醒，并触发庆祝特效

---

## 📄 License

```
MIT License · Copyright (c) 2025 Crasor

绝赞开源中~ Feel free to use, modify, and share! 🌸
```

---

## 👤 Authors

| Role | GitHub |
|------|--------|
| Original Author | [Crasor](https://github.com/Crasor) |
| UI Enhancement | [@lighthouse333](https://github.com/lighthouse333) |

---

<div align="center">

Made with ❤️ and Kotlin · 用爱发电 ⚡

*Stay focused, stay kawaii~* 🌸 · *专注就是可爱！*

</div>
