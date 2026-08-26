#!/usr/bin/env python3
"""
应用歌词放大 2.5 倍的修改到 PlayerLyricsShareUi.kt 和 AutomotivePlayerUi.kt
使用字符串替换，比 git patch 更稳健。
"""
import sys

LYRICS_FILE = "player/app/src/commonMain/kotlin/top/iwesley/lyn/music/PlayerLyricsShareUi.kt"
AUTOMOTIVE_FILE = "player/app/src/commonMain/kotlin/top/iwesley/lyn/music/automotive/AutomotivePlayerUi.kt"

SCALE = 2.5  # 字体放大倍数

def modify_lyrics_ui():
    with open(LYRICS_FILE, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_len = len(content)
    
    # 1) 添加 import
    if "import androidx.compose.ui.text.TextStyle" not in content:
        content = content.replace(
            "import androidx.compose.ui.text.font.FontWeight",
            "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.text.TextStyle"
        )
    if "import androidx.compose.ui.unit.sp" not in content:
        content = content.replace(
            "import androidx.compose.ui.unit.dp",
            "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp"
        )
    
    # 2) 添加 lyricsTextScale 参数
    if "lyricsTextScale: Float = 1f" not in content:
        content = content.replace(
            "    mobilePlayback: Boolean = false,\n) {",
            "    mobilePlayback: Boolean = false,\n    lyricsTextScale: Float = 1f,\n) {"
        )
    
    # 3) 替换 lyricsLineSpacing 和 translationLineSpacing，并添加自定义 text style
    old_block = """                    val lyricsLineSpacing = when {
                        compact -> 12.dp
                        pure -> 24.dp
                        else -> 16.dp
                    }
                    val translationLineSpacing = when {
                        compact -> 3.dp
                        pure -> 6.dp
                        else -> 4.dp
                    }"""
    
    new_block = f"""                    val lyricsLineSpacing = (when {{
                        compact -> 12.dp
                        pure -> 24.dp
                        else -> 16.dp
                    }}) * lyricsTextScale
                    val translationLineSpacing = (when {{
                        compact -> 3.dp
                        pure -> 6.dp
                        else -> 4.dp
                    }}) * lyricsTextScale
                    val lyricsHighlightTextStyle = TextStyle(
                        fontSize = (24f * lyricsTextScale).sp,
                        lineHeight = (32f * lyricsTextScale).sp,
                    )
                    val lyricsNormalTextStyle = TextStyle(
                        fontSize = (22f * lyricsTextScale).sp,
                        lineHeight = (30f * lyricsTextScale).sp,
                    )
                    val lyricsTranslationTextStyle = TextStyle(
                        fontSize = (16f * lyricsTextScale).sp,
                        lineHeight = (22f * lyricsTextScale).sp,
                    )"""
    
    if old_block in content:
        content = content.replace(old_block, new_block)
        print("[OK] 替换 lyricsLineSpacing 块成功")
    else:
        print("[WARN] 未找到精确匹配的 lyricsLineSpacing 块，尝试宽松方式...")
        if "lyricsLineSpacing = when" not in content:
            print("  [ERROR] 完全找不到 lyricsLineSpacing")
    
    # 4) 替换 MaterialTheme.typography 为自定义 text style
    if "style = MaterialTheme.typography.headlineSmall" in content:
        content = content.replace(
            "style = MaterialTheme.typography.headlineSmall",
            "style = lyricsHighlightTextStyle"
        )
        print("[OK] 替换 headlineSmall -> lyricsHighlightTextStyle")
    
    if "style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge" in content:
        content = content.replace(
            "style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge",
            "style = if (isHighlighted) lyricsHighlightTextStyle else lyricsNormalTextStyle"
        )
        print("[OK] 替换 isHighlighted headlineSmall/titleLarge")
    
    if "style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge" in content:
        content = content.replace(
            "style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge",
            "style = lyricsTranslationTextStyle"
        )
        print("[OK] 替换翻译行 titleMedium/bodyLarge")
    
    with open(LYRICS_FILE, 'w', encoding='utf-8') as f:
        f.write(content)
    
    if len(content) == original_len:
        print("\n[FAIL] Lyrics 文件内容没有变化")
        return False
    else:
        print(f"\n[OK] Lyrics 文件修改成功 ({original_len} -> {len(content)} 字节)")
        return True


def modify_automotive_ui():
    with open(AUTOMOTIVE_FILE, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_len = len(content)
    
    if "lyricsTextScale =" not in content:
        if "pure = true,\n            modifier = Modifier.fillMaxSize()" in content:
            content = content.replace(
                "pure = true,\n            modifier = Modifier.fillMaxSize()",
                f"pure = true,\n            lyricsTextScale = {SCALE}f,\n            modifier = Modifier.fillMaxSize()"
            )
            print("[OK] AutomotivePlayerUi 添加 lyricsTextScale 参数")
        else:
            print("[FAIL] 未找到 pure = true 的调用位置")
            return False
    
    with open(AUTOMOTIVE_FILE, 'w', encoding='utf-8') as f:
        f.write(content)
    
    if len(content) == original_len:
        print("\n[FAIL] Automotive 文件内容没有变化")
        return False
    else:
        print(f"\n[OK] Automotive 文件修改成功 ({original_len} -> {len(content)} 字节)")
        return True


if __name__ == "__main__":
    print(f"=== 歌词放大修改工具 (缩放: {SCALE}x) ===")
    print()
    
    result1 = modify_lyrics_ui()
    result2 = modify_automotive_ui()
    
    if result1 and result2:
        print("\n=== 所有修改应用成功！ ===")
        sys.exit(0)
    else:
        print("\n=== 部分修改失败，请检查上方日志 ===")
        sys.exit(1)
