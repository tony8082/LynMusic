#!/usr/bin/env python3
"""
应用歌词放大 2.5 倍的修改到 PlayerLyricsShareUi.kt 和 AutomotivePlayerUi.kt
逐行处理，确保语法正确。
"""
import sys

LYRICS_FILE = "player/app/src/commonMain/kotlin/top/iwesley/lyn/music/PlayerLyricsShareUi.kt"
AUTOMOTIVE_FILE = "player/app/src/commonMain/kotlin/top/iwesley/lyn/music/automotive/AutomotivePlayerUi.kt"

SCALE = 2.5  # 字体放大倍数


def modify_lyrics_ui():
    with open(LYRICS_FILE, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    original_line_count = len(lines)
    new_lines = []
    i = 0
    
    # === 阶段1: 添加 import ===
    added_textstyle_import = False
    added_sp_import = False
    
    for i, line in enumerate(lines):
        new_lines.append(line)
        
        if not added_textstyle_import and "import androidx.compose.ui.text.font.FontWeight" in line:
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append("import androidx.compose.ui.text.TextStyle\n")
            added_textstyle_import = True
            print("[OK] 添加 TextStyle import")
        
        if not added_sp_import and "import androidx.compose.ui.unit.dp" in line and "unit.sp" not in line:
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append("import androidx.compose.ui.unit.sp\n")
            added_sp_import = True
            print("[OK] 添加 sp import")
    
    lines = new_lines
    new_lines = []
    
    # === 阶段2: 添加 lyricsTextScale 参数 ===
    added_param = False
    for i, line in enumerate(lines):
        new_lines.append(line)
        if not added_param and "mobilePlayback: Boolean = false" in line:
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append(indent + "lyricsTextScale: Float = 1f,\n")
            added_param = True
            print(f"[OK] 添加 lyricsTextScale 参数 (第 {i+1} 行后)")
    
    if not added_param:
        print("[FAIL] 找不到 mobilePlayback 参数")
        return False
    
    lines = new_lines
    new_lines = []
    
    # === 阶段3: 在 LazyColumn( 之前插入自定义 text style 定义 ===
    #    找第一个出现的 LazyColumn(（在歌词显示区域内的那个）
    added_styles = False
    lazycolumn_count = 0
    
    for i, line in enumerate(lines):
        # 检测 LazyColumn(
        if "LazyColumn(" in line.strip() and "modifier" in lines[i+1] if i+1 < len(lines) else False:
            lazycolumn_count += 1
            # 第一个 LazyColumn 就是歌词列表，在它前面插入 text style 定义
            if lazycolumn_count == 1 and not added_styles:
                # 找这一行的缩进
                indent = line[:len(line) - len(line.lstrip())]
                
                # 插入自定义 text style 定义
                new_lines.append(indent + "val lyricsHighlightTextStyle = TextStyle(\n")
                new_lines.append(indent + "    fontSize = (24f * lyricsTextScale).sp,\n")
                new_lines.append(indent + "    lineHeight = (32f * lyricsTextScale).sp,\n")
                new_lines.append(indent + ")\n")
                new_lines.append(indent + "val lyricsNormalTextStyle = TextStyle(\n")
                new_lines.append(indent + "    fontSize = (22f * lyricsTextScale).sp,\n")
                new_lines.append(indent + "    lineHeight = (30f * lyricsTextScale).sp,\n")
                new_lines.append(indent + ")\n")
                new_lines.append(indent + "val lyricsTranslationTextStyle = TextStyle(\n")
                new_lines.append(indent + "    fontSize = (16f * lyricsTextScale).sp,\n")
                new_lines.append(indent + "    lineHeight = (22f * lyricsTextScale).sp,\n")
                new_lines.append(indent + ")\n")
                new_lines.append("\n")
                
                added_styles = True
                print(f"[OK] 在 LazyColumn 前插入 text style 定义 (第 {i+1} 行)")
        
        new_lines.append(line)
    
    if not added_styles:
        print("[FAIL] 找不到 LazyColumn 插入位置")
        return False
    
    lines = new_lines
    
    # === 阶段4: 替换 MaterialTheme.typography 引用 ===
    #    把每一行中的引用替换掉
    replace_count_highlight = 0
    replace_count_normal = 0
    replace_count_translation = 0
    
    result_lines = []
    for line in lines:
        new_line = line
        
        # 注意：先替换长的模式，避免短的先匹配了
        if "style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge" in new_line:
            new_line = new_line.replace(
                "style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge",
                "style = lyricsTranslationTextStyle"
            )
            replace_count_translation += 1
        
        if "style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge" in new_line:
            new_line = new_line.replace(
                "style = if (isHighlighted) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge",
                "style = if (isHighlighted) lyricsHighlightTextStyle else lyricsNormalTextStyle"
            )
            replace_count_normal += 1
        
        if "style = MaterialTheme.typography.headlineSmall" in new_line:
            new_line = new_line.replace(
                "style = MaterialTheme.typography.headlineSmall",
                "style = lyricsHighlightTextStyle"
            )
            replace_count_highlight += 1
        
        result_lines.append(new_line)
    
    print(f"[OK] 替换 headlineSmall -> lyricsHighlightTextStyle ({replace_count_highlight} 处)")
    print(f"[OK] 替换 isHighlighted headlineSmall/titleLarge ({replace_count_normal} 处)")
    print(f"[OK] 替换翻译行 titleMedium/bodyLarge ({replace_count_translation} 处)")
    
    with open(LYRICS_FILE, 'w', encoding='utf-8') as f:
        f.writelines(result_lines)
    
    print(f"\n[OK] Lyrics 文件修改成功 ({original_line_count} -> {len(result_lines)} 行)")
    return True


def modify_automotive_ui():
    with open(AUTOMOTIVE_FILE, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    original_line_count = len(lines)
    new_lines = []
    added = False
    
    for i, line in enumerate(lines):
        new_lines.append(line)
        # 在 pure = true, 后面插入 lyricsTextScale（注意排除 modifier 行）
        if not added and "pure = true," in line and "modifier" not in line:
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append(indent + "lyricsTextScale = " + str(SCALE) + "f,\n")
            added = True
            print(f"[OK] Automotive 添加 lyricsTextScale 参数 (第 {i+1} 行后)")
    
    if not added:
        print("[SKIP] Automotive lyricsTextScale 已存在或未找到位置")
    
    with open(AUTOMOTIVE_FILE, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    print(f"[OK] Automotive 文件处理完成 ({original_line_count} -> {len(new_lines)} 行)")
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
