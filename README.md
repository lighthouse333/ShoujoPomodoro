# 少女番茄钟 · ShoujoPomodoro 🌸

<div align="center">

**一款梦幻少女主题的 Android 番茄钟应用**

基于 Jetpack Compose & Material 3 构建 · 极致视觉体验

[📖 English Version](README_EN.md)

<img src="https://img.shields.io/badge/Kotlin-2.0.21-purple?logo=kotlin" alt="Kotlin" />
<img src="https://img.shields.io/badge/Compose-BOM_2024.12-blue?logo=jetpackcompose" alt="Compose" />
<img src="https://img.shields.io/badge/Material-3-pink?logo=materialdesign" alt="Material 3" />
<img src="https://img.shields.io/badge/Min_SDK-24-green?logo=android" alt="Min SDK" />
<img src="https://img.shields.io/badge/Target_SDK-34-orange?logo=android" alt="Target SDK" />
<img src="https://img.shields.io/badge/License-MIT-yellow" alt="License" />

</div>

---

## ✨ 2026年7月 UI 全面升级

应用经历了彻底的视觉改造——从简洁的计时器蜕变为**梦幻少女体验**：

### 🌸 视觉特效系统

| 功能 | 说明 |
|------|------|
| **樱吹雪粒子系统** | 樱花花瓣、星光、光点、爱心实时飘落全屏，支持4种粒子类型，物理运动轨迹 ✨ |
| **AGSL 着色器背景** | GPU 加速渐变背景——极光波浪、柔光粉彩渐变、暗夜星空，60fps 流畅色彩过渡 |
| **庆祝特效** | 番茄钟完成时触发5种庆祝方式：彩纸爆裂、金色星星雨、樱吹雪暴风、爱心爆炸、魔法闪光 🎉 |
| **玻璃拟态 UI** | 半透明毛玻璃面板覆盖计时器数字、音乐条、卡片，柔和模糊与阴影 |

### 👧 增强角色系统

| 功能 | 说明 |
|------|------|
| **4 套服装** | 水手服（专注）、洋装（短休）、魔法少女（长休）、冬装（季节限定） |
| **3 种发型** | 双马尾、长直发、短波波头——各有独立飘动动画 |
| **丰富表情** | 多层眼睛带高光、动态腮红强度、状态嘴型变化、眉毛运动 |
| **角色辉光** | 呼吸脉冲彩色光环，随计时器状态变化颜色 |
| **身体动画** | 待机呼吸、眨眼（含随机连眨）、微小头部晃动 |

### ⭕ 高级计时器

| 功能 | 说明 |
|------|------|
| **多层光晕** | 进度弧带内外辉光环，不同阶段不同颜色 |
| **轨道粒子** | 沿计时器环旋转的星光粒子，物理运动 |
| **星星进度标记** | 0%/25%/50%/75% 处的4颗星标，到达点亮 |
| **光点指示** | 进度弧前端的明亮光点 |
| **渐变弧线** | 进度弧上的平滑渐变着色 |

### 🎛️ 高级控件

| 功能 | 说明 |
|------|------|
| **新拟态按钮** | 重置/播放/跳过按钮带柔和阴影深度 |
| **渐变播放键** | 粉→珊瑚渐变的中央圆形播放按钮 |
| **毛玻璃音乐条** | 音乐播放器包裹在半透明磨砂容器中 |

---

## ⏱️ 核心功能

- 标准番茄钟循环：专注(25分钟) → 短休(5分钟) → 专注 → 长休(15分钟)
- 自定义时长和循环次数
- 角色状态随计时器动态变化（待机/专注/休息/提醒）
- 任务管理（添加、完成、删除、设为当前）
- 后台前台服务计时，通知提醒
- 内置音乐播放器
- 中英文双语支持，无需重启切换
- 亮色/暗色主题跟随系统

---

## 🛠️ 技术栈

| 技术 | 用途 |
|:------|:------|
| **Kotlin 2.0** | 编程语言 |
| **Jetpack Compose** | 现代声明式 UI 框架 |
| **Material 3** | 设计系统基础 |
| **Canvas API** | 自定义角色与粒子渲染 |
| **AGSL 着色器** | GPU 加速渐变背景 |
| **Room** | 本地 SQLite 任务数据库 |
| **DataStore** | 计时器设置持久化 |
| **Coroutines + Flow** | 响应式异步数据流 |
| **Navigation Compose** | 应用内导航 |
| **Foreground Service** | 后台计时执行 |

---

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 以上
- Android SDK 35
- JDK 17

### 构建运行

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

### 系统要求

- 最低 SDK：Android 7.0 (API 24)
- 目标 SDK：Android 14 (API 34)
- 编译 SDK：Android 15 (API 35)

---

## 📁 项目结构

```
app/src/main/java/com/shoujopomodoro/
├── data/
│   ├── local/              # Room 数据库、DAO、实体
│   ├── preferences/        # DataStore 偏好设置
│   └── repository/         # Repository 模式
├── di/                     # 依赖注入容器 & 计时器状态持有者
├── domain/
│   ├── model/              # 领域模型（Task, TimerSession, CharacterState, TimerPhase）
│   └── usecase/            # 业务逻辑
├── notification/           # 通知渠道
├── service/                # 前台服务、音乐播放器
├── ui/
│   ├── component/          # 可复用组件 ★
│   │   ├── SakuraParticleBackground.kt   # 樱吹雪粒子系统
│   │   ├── GradientBackgrounds.kt        # AGSL 着色器背景
│   │   ├── CelebrationOverlay.kt         # 完成庆祝特效
│   │   ├── EnhancedShoujoCharacter.kt    # 完整角色渲染
│   │   ├── PremiumCircularTimer.kt       # 多层光晕计时器
│   │   └── PremiumControls.kt            # 新拟态按钮与标签
│   ├── navigation/         # NavGraph 与路由
│   ├── screen/             # 页面 + ViewModels
│   │   ├── timer/          # 主计时器页面
│   │   ├── tasklist/       # 任务管理
│   │   └── settings/       # 设置与音乐
│   └── theme/              # 调色板、排版、形状
└── util/                   # 常量、时间格式化
```

---

## 🎨 配色体系

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

## 📝 使用指南

1. **开始专注** — 点击计时器页面的 ▶️ 播放按钮
2. **管理任务** — 点击右上角清单图标，添加要专注的任务
3. **自定义设置** — 点击齿轮图标，调整时长、循环次数、语言
4. **后台计时** — 离开应用后计时器通过前台服务继续运行
5. **完成通知** — 阶段结束时弹出全屏提醒，并触发庆祝特效

---

## 📄 开源协议

```
MIT License · Copyright (c) 2025 Crasor

绝赞开源中~ 欢迎使用、修改和分享！🌸
```

---

## 👤 作者

| 角色 | GitHub |
|------|--------|
| 原作者 | [Crasor](https://github.com/Crasor) |
| UI 美化 | [@lighthouse333](https://github.com/lighthouse333) |

---

<div align="center">

Made with ❤️ and Kotlin · 用爱发电 ⚡

*专注就是可爱！* 🌸 *Stay focused, stay kawaii~*

</div>
