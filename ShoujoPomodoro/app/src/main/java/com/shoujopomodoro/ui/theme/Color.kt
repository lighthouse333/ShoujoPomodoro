package com.shoujopomodoro.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
//  Shoujo Dream Palette — 少女梦幻调色板
//  Inspired by: shoujo manga, sakura, mermaid
//  pastels, magical girl transformation ✨
// ═══════════════════════════════════════════════

// ── Primary: Sakura Pink ──
val SakuraPink = Color(0xFFFFB7C5)          // 樱花粉 — 主色调
val SakuraDeep = Color(0xFFFF69B4)          // 深樱 — 强调/渐变
val SakuraLight = Color(0xFFFFF0F5)         // 浅樱 — 背景
val SakuraGlow = Color(0xFFFFE4E9)          // 樱光 — 辉光

// ── Secondary: Wisteria Lavender ──
val WisteriaLavender = Color(0xFFD8BFD8)    // 紫藤 — 次要色
val WisteriaDeep = Color(0xFFB39DDB)        // 深紫藤
val WisteriaLight = Color(0xFFF3E5F5)       // 浅紫藤
val WisteriaGlow = Color(0xFFEDE7F6)        // 紫光

// ── Tertiary: Sky Blue ──
val MermaidBlue = Color(0xFFB2EBF2)         // 人鱼蓝
val SkyBlue = Color(0xFF81D4FA)             // 天空蓝
val IceBlue = Color(0xFFE1F5FE)             // 冰蓝

// ── Accent Gold ──
val KonpeitoGold = Color(0xFFFFD700)        // 金平糖金 — 点缀/星星
val WarmAmber = Color(0xFFFFECB3)           // 暖琥珀

// ── Mint Green ──
val MatchaMint = Color(0xFFA5D6A7)          // 抹茶薄荷
val MintLight = Color(0xFFE8F5E9)           // 浅薄荷

// ── Rose ──
val RoseBlush = Color(0xFFF8BBD0)           // 蔷薇腮红
val CoralRose = Color(0xFFFF80AB)           // 珊瑚玫瑰

// ── Character Colors ──
val SkinTone = Color(0xFFFFE0BD)            // 肤色
val HairSakura = Color(0xFFFFB7C5)          // 樱色发
val HairBlue = Color(0xFFADD8E6)            // 蓝色发
val HairPurple = Color(0xFFDDA0DD)          // 紫色发
val HairMint = Color(0xFF98FB98)            // 薄荷发
val BlushPink = Color(0x80FF69B4)           // 腮红
val EyeBlue = Color(0xFF4169E1)             // 蓝瞳
val EyeGreen = Color(0xFF66BB6A)            // 绿瞳
val EyePurple = Color(0xFF9C27B0)           // 紫瞳
val UniformNavy = Color(0xFF2C3E50)         // 制服深蓝
val UniformBrown = Color(0xFF5D4037)        // 制服棕
val BowRed = Color(0xFFDC143C)              // 蝴蝶结红

// ── Dark Theme Surfaces ──
val DarkNavy = Color(0xFF0D0D1A)            // 深邃夜空
val DarkIndigo = Color(0xFF1A1A2E)          // 靛蓝暗面
val DarkCard = Color(0xFF252538)            // 暗色卡片
val DarkSurface = Color(0xFF2D2D40)         // 暗色表面

// ── Light Theme Surfaces ──
val LightCream = Color(0xFFFFFAF5)          // 奶油白
val LightPinkBg = Color(0xFFFFF5F8)         // 粉白背景
val LightCard = Color(0xFFFFFFFF)           // 白色卡片

// ── Semantic / Phase Colors ──
val FocusPink = Color(0xFFFF69B4)           // 专注 — 粉
val BreakGreen = Color(0xFF66BB6A)          // 短休 — 绿
val LongBreakBlue = Color(0xFF42A5F5)       // 长休 — 蓝
val AlertRed = Color(0xFFFF5252)            // 提醒 — 红

// ── Gradient Stops ──
val GradientSunsetStart = Color(0xFFFFB7C5) // 日落渐变起
val GradientSunsetMid = Color(0xFFDDA0DD)   // 日落渐变中
val GradientSunsetEnd = Color(0xFFB2EBF2)   // 日落渐变末

val GradientNightStart = Color(0xFF1A237E)  // 夜空渐变起
val GradientNightMid = Color(0xFF4A148C)    // 夜空渐变中
val GradientNightEnd = Color(0xFF880E4F)    // 夜空渐变末

val GradientAuroraStart = Color(0xFF00BCD4) // 极光渐变起
val GradientAuroraMid = Color(0xFF7C4DFF)   // 极光渐变中
val GradientAuroraEnd = Color(0xFFFF4081)   // 极光渐变末

// ── Particle Colors ──
val ParticlePink = Color(0xFFFFB7C5)
val ParticleLavender = Color(0xFFD8BFD8)
val ParticleGold = Color(0xFFFFD700)
val ParticleMint = Color(0xFF98FB98)
val ParticleSky = Color(0xFFADD8E6)
val ParticleRose = Color(0xFFF8BBD0)

// ── Glow / Transparency ──
val GlowPink20 = Color(0x33FF69B4)
val GlowPink40 = Color(0x66FF69B4)
val GlowPink60 = Color(0x99FF69B4)
val GlowGold30 = Color(0x4DFFD700)
val GlowBlue20 = Color(0x3342A5F5)

// ── Legacy aliases (keep compatibility) ──
internal val Pink80 = SakuraPink
internal val Pink40 = SakuraDeep
internal val PinkLight = SakuraLight
internal val Purple80 = WisteriaLavender
internal val Purple40 = WisteriaDeep
internal val SurfacePink = LightPinkBg
internal val SurfaceDark = DarkIndigo
val ShoujoPink = SakuraPink
val ShoujoLavender = WisteriaLavender
val ShoujoGold = KonpeitoGold
val ShoujoSkyBlue = SkyBlue
val ShoujoMint = MatchaMint
val ShoujoRose = RoseBlush
val ShoujoPurple = WisteriaLight
val ShoujoTeal = Color(0xFFA0E4CB)
val ShoujoAmber = WarmAmber
