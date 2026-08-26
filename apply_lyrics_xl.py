#!/usr/bin/env python3
"""
应用歌词放大 2.5 倍的修改到 PlayerLyricsShareUi.kt 和 AutomotivePlayerUi.kt
使用字符串替换，比 git patch 更稳健。
"""
import re
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
        print("[OK] 添加 TextStyle import")
    if "import androidx.compose.ui.unit.sp" not in content:
        content = content.replace(
            "import androidx.compose.ui.unit.dp",
            "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp"
        )
        print("[OK] 添加 sp import")

    # 2) 添加 lyricsTextScale 参数
    if "lyricsTextScale: Float = 1f" not in content:
        # 用正则匹配，容忍不同数量的空格和换行
        pattern = r"(mobilePlayback: Boolean = false,)\n(\s*)\(\s*\{)"
        replacement = rf"\1\n\2lyricsTextScale: Float = 1f,\n\2) {{
        new_content = re.sub(pattern, replacement, content)
        if new_content != content:
            content = new_content
            print("[OK] 添加 lyricsTextScale 参数")
        else:
            print("[WARN] 未找到 mobilePlayback 参数位置，尝试简单替换...")
            # 更简单的方式：找 mobilePlayback 那行后面加
            lines = content.split('\n')
            new_lines = []
            for i, line in enumerate(lines):
                new_lines.append(line)
                if 'mobilePlayback: Boolean = false' in line and 'lyricsTextScale' not in line:
                    # 找缩进
                    indent = line[:len(line) - len(line.lstrip())]
                    new_lines.append(f"{indent}lyricsTextScale: Float = 1f,")
                    print(f"[OK] 在第 {i+1} 行后添加 lyricsTextScale 参数")
                    break
            content = '\n'.join(new_lines)

    # 3) 在 LazyColumn 之前插入自定义 text style 定义
    #    找一个可靠的锚点：translationLineSpacing 定义之后
    if "lyricsHighlightTextStyle = TextStyle" not in content:
        # 用正则找到 translationLineSpacing 的 when 块末尾，然后插入 text style 定义
        # 匹配模式：translationLineSpacing = when { ... }（容忍不同缩进）
        pattern = r"(val translationLineSpacing = when \{[^}]+\})"
        match = re.search(pattern, content)
        if match:
            # 获取缩进
            full_match = match.group(0)
            lines_in_match = full_match.split('\n')
            first_line = lines_in_match[0]
            indent = first_line[:len(first_line) - len(first_line.lstrip())]
            
            # 先把 lyricsLineSpacing 改成带缩放的
            content = re.sub(
                r"val lyricsLineSpacing = when \{",
                "val lyricsLineSpacing = (when {",
                content,
                count=1
            )
            # 找到 lyricsLineSpacing 的 when 块结束位置，加 ) * lyricsTextScale
            # 简单方式：找 translationLineSpacing 前面的那个 }
            
            # 在 translationLineSpacing 块结束后添加 ) * lyricsTextScale 和新的 text style 定义
            styles_code = f""" * lyricsTextScale
{indent}val lyricsHighlightTextStyle = TextStyle(
{indent}    fontSize = (24f * lyricsTextScale).sp,
{indent}    lineHeight = (32f * lyricsTextScale).sp,
{indent})
{indent}val lyricsNormalTextStyle = TextStyle(
{indent}    fontSize = (22f * lyricsTextScale).sp,
{indent}    lineHeight = (30f * lyricsTextScale).sp,
{indent})
{indent}val lyricsTranslationTextStyle = TextStyle(
{indent}    fontSize = (16f * lyricsTextScale).sp,
{indent}    lineHeight = (22f * lyricsTextScale).sp,
{indent})"""
            
            # 在 translationLineSpacing 的 when 块最后的 } 后面插入
            def add_styles(m):
                return m.group(1) + styles_code
            
            # 先用简单方式：translationLineSpacing 的 when 块结尾后插入
            content = re.sub(
                r"(val translationLineSpacing = when \{[^}]+\})",
                lambda m: m.group(1) + " * lyricsTextScale" + styles_code,
                content,
                count=1
            )
            
            # 同时给 lyricsLineSpacing 的 when 块也加上 * lyricsTextScale
            # 找 lyricsLineSpacing 的 when 块（在 translationLineSpacing 之前的那个）
            content = re.sub(
                r"(val lyricsLineSpacing = when \{[^}]+\})",
                lambda m: m.group(1) + " * lyricsTextScale",
                content,
                count=1
            )
            
            print("[OK] 插入自定义 text style 定义")
        else:
            print("[FAIL] 找不到 translationLineSpacing，无法插入 text style")
            return False

    # 4) 替换 MaterialTheme.typography 为自定义 text style
    replacements = [
        (
            "style = MaterialTheme.typography.headlineSmall",
            "style = lyricsHighlightTextStyle",
            "headlineSmall -> lyricsHighlightTextStyle"
        ),
        (
            "style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge",
            "style = if (isHighlighted) lyricsHighlightTextStyle else lyricsNormalTextStyle",
            "isHighlighted headlineSmall/titleLarge"
        ),
        (
            "style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge",
            "style = lyricsTranslationTextStyle",
            "翻译行 titleMedium/bodyLarge"
        ),
    ]

    for old, new, desc in replacements:
        count = content.count(old)
        if count > 0:
            content = content.replace(old, new)
            print(f"[OK] 替换 {desc} ({count} 处)")
        else:
            print(f"[WARN] 未找到 {desc}")

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
        # 找 pure = true 后面的位置，容忍不同缩进
        pattern = r"(pure = true,)\n(\s+)(modifier = Modifier.fillMaxSize\(\))"
        replacement = rf"\1\n\2lyricsTextScale = {SCALE}f,\n\2\3"
        new_content = re.sub(pattern, replacement, content)
        if new_content != content:
            content = new_content
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
