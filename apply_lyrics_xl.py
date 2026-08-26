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
    else:
        print("[SKIP] TextStyle import 已存在")

    if "import androidx.compose.ui.unit.sp" not in content:
        content = content.replace(
            "import androidx.compose.ui.unit.dp",
            "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp"
        )
        print("[OK] 添加 sp import")
    else:
        print("[SKIP] sp import 已存在")

    # 2) 添加 lyricsTextScale 参数到 PlayerLyricsPane 函数
    if "lyricsTextScale: Float = 1f" not in content:
        lines = content.split('\n')
        new_lines = []
        inserted = False
        for i, line in enumerate(lines):
            new_lines.append(line)
            if not inserted and 'mobilePlayback: Boolean = false' in line:
                # 获取当前行的缩进
                stripped = line.lstrip()
                indent = line[:len(line) - len(stripped)]
                new_lines.append(indent + "lyricsTextScale: Float = 1f,")
                inserted = True
                print(f"[OK] 在第 {i+1} 行后添加 lyricsTextScale 参数")
        if inserted:
            content = '\n'.join(new_lines)
        else:
            print("[FAIL] 找不到 mobilePlayback 参数")
            return False
    else:
        print("[SKIP] lyricsTextScale 参数已存在")

    # 3) 插入自定义 text style 定义
    #    在 translationLineSpacing 的 when 块结束之后插入
    if "lyricsHighlightTextStyle = TextStyle" not in content:
        # 先给 lyricsLineSpacing 加括号和缩放
        # 找 lyricsLineSpacing = when { 的位置
        lls_match = re.search(r"(\s*)(val lyricsLineSpacing = when \{)", content)
        if not lls_match:
            print("[FAIL] 找不到 lyricsLineSpacing = when {")
            return False
        
        # 找 translationLineSpacing = when { 的位置
        tls_match = re.search(r"(\s*)(val translationLineSpacing = when \{)", content)
        if not tls_match:
            print("[FAIL] 找不到 translationLineSpacing = when {")
            return False
        
        indent = tls_match.group(1)
        
        # 找 lyricsLineSpacing 的 when 块结束位置（下一个 val 之前的最后一个 }）
        # 简单方法：找 lyricsLineSpacing 行之后、translationLineSpacing 行之前的最后一个 }
        lls_start = lls_match.start()
        tls_start = tls_match.start()
        between = content[lls_start:tls_start]
        
        # 在 between 里找最后一个 }
        last_brace = between.rfind('}')
        if last_brace == -1:
            print("[FAIL] 找不到 lyricsLineSpacing when 块的结束}")
            return False
        
        # 替换 lyricsLineSpacing 的 when 块：开头加 ( ，结尾加 ) * lyricsTextScale
        # 开头
        content = content.replace(
            "val lyricsLineSpacing = when {",
            "val lyricsLineSpacing = (when {",
            1
        )
        # 结尾（在 translationLineSpacing 之前的那个 } 后面加 ) * lyricsTextScale
        # 重新计算位置（因为前面替换了一次）
        tls_match2 = re.search(r"(\s*)(val translationLineSpacing = when \{)", content)
        if tls_match2:
            tls_start2 = tls_match2.start()
            indent2 = tls_match2.group(1)
            # 在 translationLineSpacing 之前插入
            insert_code = " * lyricsTextScale\n" + indent2 + "val lyricsHighlightTextStyle = TextStyle(\n" + indent2 + "    fontSize = (24f * lyricsTextScale).sp,\n" + indent2 + "    lineHeight = (32f * lyricsTextScale).sp,\n" + indent2 + ")\n" + indent2 + "val lyricsNormalTextStyle = TextStyle(\n" + indent2 + "    fontSize = (22f * lyricsTextScale).sp,\n" + indent2 + "    lineHeight = (30f * lyricsTextScale).sp,\n" + indent2 + ")\n" + indent2 + "val lyricsTranslationTextStyle = TextStyle(\n" + indent2 + "    fontSize = (16f * lyricsTextScale).sp,\n" + indent2 + "    lineHeight = (22f * lyricsTextScale).sp,\n" + indent2 + ")\n" + indent2
            
            content = content[:tls_start2] + insert_code + content[tls_start2:]
            print("[OK] 插入自定义 text style 定义")
        else:
            print("[FAIL] 重新定位 translationLineSpacing 失败")
            return False
        
        # 同时给 translationLineSpacing 的 when 块也加上 * lyricsTextScale
        # 方式：在 translationLineSpacing = when { 行前面已经有了，但值还没改
        # 找 translationLineSpacing 的 when 块，在结尾 } 后加 * lyricsTextScale
        # 用正则找到 translationLineSpacing 的 when 块
        tls_block_pattern = r"val translationLineSpacing = when \{[^}]+\}"
        tls_block_match = re.search(tls_block_pattern, content)
        if tls_block_match:
            # 把结尾的 } 替换为 } * lyricsTextScale
            block_text = tls_block_match.group(0)
            new_block = block_text[:-1] + "} * lyricsTextScale"
            content = content[:tls_block_match.start()] + new_block + content[tls_block_match.end():]
            print("[OK] translationLineSpacing 添加缩放")
        
        # lyricsLineSpacing 同理
        lls_block_pattern = r"val lyricsLineSpacing = \(when \{[^}]+\}"
        lls_block_match = re.search(lls_block_pattern, content)
        if lls_block_match:
            block_text = lls_block_match.group(0)
            # 已经有 (when { 了，结尾的 } 后面加 ) * lyricsTextScale
            new_block = block_text + " * lyricsTextScale"
            content = content[:lls_block_match.start()] + new_block + content[lls_block_match.end():]
            print("[OK] lyricsLineSpacing 添加缩放")
    else:
        print("[SKIP] text style 定义已存在")

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
        lines = content.split('\n')
        new_lines = []
        inserted = False
        for i, line in enumerate(lines):
            new_lines.append(line)
            if not inserted and 'pure = true' in line and 'modifier' not in line:
                stripped = line.lstrip()
                indent = line[:len(line) - len(stripped)]
                new_lines.append(indent + "lyricsTextScale = " + str(SCALE) + "f,")
                inserted = True
                print(f"[OK] 在第 {i+1} 行后添加 lyricsTextScale 参数")
        if inserted:
            content = '\n'.join(new_lines)
        else:
            print("[FAIL] 未找到 pure = true 的位置")
            return False
    else:
        print("[SKIP] Automotive lyricsTextScale 已存在")

    with open(AUTOMOTIVE_FILE, 'w', encoding='utf-8') as f:
        f.write(content)

    if len(content) == original_len:
        print("\n[FAIL] Automotive 文件内容没有变化")
        return False
    else:
        print(f"\n[OK] Automotive 文件修改成功 ({original_len} -> {len(content)} 字节)")
        return True


if __name__ == "__main__":
    print("=== 歌词放大修改工具 (缩放: " + str(SCALE) + "x) ===")
    print()

    result1 = modify_lyrics_ui()
    result2 = modify_automotive_ui()

    if result1 and result2:
        print("\n=== 所有修改应用成功！ ===")
        sys.exit(0)
    else:
        print("\n=== 部分修改失败，请检查上方日志 ===")
        sys.exit(1)
