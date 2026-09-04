LynMusic is a cross-platform local music player for Windows, Linux, macOS, Android and iOS , built with Kotlin Multiplatform.

LynMusic是基于 Kotlin Multiplatform 的跨平台本地音乐播放器项目，目标平台包括 Android、iOS 和桌面端（JVM），支持 Windows 、 macOS 和 Linux。

![LynMusic](./doc/LynMusic.png)
## 为什么做这个播放器

第一，很多本地播放器搜索歌词太难用了，有些歌还不一定能搜到，比如 Bobby Chen、Mr Li；第二，想学习一下 KMP；第三，因为有了 codex 这样的编程工具，实现难度大大下降；第四，可以自主决策功能和界面。

## 先看 UI
### 电脑或者平板
![pc_main_ui](./doc/pc_main_ui.png)
![pc_player_ui](./doc/pc_player_ui.png)
![pc_player_full_ui](./doc/pc_player_full_ui.png)
![pc_recommend_ui](./doc/pc_recommend_ui.png)
![pc_lrc_apply](./doc/pc_lrc_apply.png)
![pc_lrc_search](./doc/pc_lrc_search.png)
![pc_lrc_share_cover_color](./doc/pc_lrc_share_cover_color.png)
![pc_lrc_share_note](./doc/pc_lrc_share_note.png)
![pc_music_tag_editor](./doc/pc_music_tag_editor.png)
![lyrics-note_share](./doc/lyrics-note_share.png)
![lyrics-share_cover2](./doc/lyrics-share_cover2.png)
![lyrics-share_note2](./doc/lyrics-share_note2.png)
![lyrics-share](./doc/lyrics-share.png)
![lyrics-share2](./doc/lyrics-share2.png)

### 手机
![phone_main_ui](./doc/phone_main_ui.jpg)
![phone_player_ui](./doc/phone_player_ui.png)
![phone_player_lrc_ui](./doc/phone_player_lrc_ui.png)
![phone_player_lrc_share_ui](./doc/phone_player_lrc_share_ui.png)
![phone_player_pop_ui](./doc/phone_player_pop_ui.png)
![phone_player_recommend_ui](./doc/phone_player_recommend_ui.png)
![phone_player_setting_ui2](./doc/phone_player_setting_ui2.png)
![phone_setting_ui](./doc/phone_setting_ui.png)

### TV
![tv_main_ui](./doc/tv_main_ui.png)
![tv_main_ui2](./doc/tv_main_ui2.png)
![tv_main_like_ui](./doc/tv_main_like_ui.png)
![tv_cast_paler_ui](./doc/tv_cast_paler_ui.png)
![tv_player_ui](./doc/tv_player_ui.png)

### 车机
![car_main_ui](./doc/car_main_ui.png)
![car_my_ui](./doc/car_my_ui.png)
![car_player_ui](./doc/car_player_ui.png)


## 介绍

LynMusic 是一款面向个人音乐收藏场景打造的跨平台本地音乐播放器，基于 Kotlin Multiplatform 开发，可运行在 Android、iOS、Windows、macOS 和 Linux。

在功能上，LynMusic 支持本地文件夹导入，也可接入 Samba、WebDAV、Navidrome 等私有音乐来源，帮助用户把分散在硬盘、NAS 和自建音乐服务中的内容汇总到同一套曲库中。应用提供歌曲、专辑、艺人等多维度浏览方式，并支持喜欢、歌单、播放队列等常用管理能力，方便日常收听与整理。当然，为了多端统一数据，推荐使用Navidrome。

除了基础播放控制外，LynMusic 还提供歌词搜索、歌词分享、在线结果回填等增强功能。对于注重资料维护的用户，应用还支持音乐标签编辑，可修改标题、歌手、专辑、歌词和封面等信息，让曲库更加整洁统一。另外，还支持自定义界面主题等。

## 编译

### 编译安卓 APP

- macOS/Linux
  ```shell
  #编译debug版本
  ./gradlew :composeApp:assembleDebug
  #编译release版本
  ./gradlew :composeApp:assembleRelease
  ```
- Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  .\gradlew.bat :composeApp:assembleRelease
  ```

### 直接运行Desktop (JVM)应用

- macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

**打包当前系统的独立安装包**（比如在 Mac 上运行就会打出 Mac 的包）：

```
./gradlew :composeApp:packageDistributionForCurrentOS
```

*或者简写为*：`./gradlew :composeApp:package`

*产物路径*：`composeApp/build/compose/binaries/main/`

### 运行IOS应用

要构建并运行 iOS 应用的开发版，可以使用 IDE 工具栏运行控件中的运行配置；或者直接在 Xcode 中打开 [/iosApp](./iosApp)  目录并从那里启动。

## 许可证

LynMusic 以 GNU General Public License version 3 or later（GPL-3.0-or-later）分发，完整许可证文本见 [LICENSE](./LICENSE)。

项目包含第三方 DLNA/UPnP 组件 Platinum UPnP SDK，该组件使用 GPL-2.0-or-later / commercial 双许可。由于完整应用包含该 GPL 组件，发布 LynMusic 的完整源码或二进制产物时需要遵守 GPL-3.0-or-later 的条款。第三方组件许可证说明见 [THIRD_PARTY_LICENSES.md](./THIRD_PARTY_LICENSES.md)。

## 公众号
 ![/锋风](./doc/weixin.jpeg) 

## Star History
[![Star History Chart](https://api.star-history.com/svg?repos=wesley666/LynMusic&type=Date)](https://star-history.com/#wesley666/LynMusic&Date)

---

## 车机定制版：LynMusic 大字版（本 fork 的改动）

本 fork 面向**车机（Android Automotive）**使用场景做定制，上游为 [wesley666/LynMusic](https://github.com/wesley666/LynMusic)，基线版本 `v1.1.1`。

> 上游面向手机 / TV / 桌面端的原有功能完整保留，**以下改动只作用于车机端**，不影响其他平台。

### 一、构建方式：上游干净源码 + 累积补丁

定制没有直接改源码，而是采用「**上游干净源码 + 累积补丁**」，上游发新版时只要把补丁 rebase 到新 tag 即可升级。

| 分支 | 作用 |
|---|---|
| `car-lyrics-2x-clock` | **主构建分支**。存放累积补丁 `car-lyrics-2x-clock.patch` 与工作流 `.github/workflows/build-car-apk.yml` |
| `car-full-source` | 源码备份分支，含全部定制、可直接编译的完整工程 |
| `main` | 上游源码 + 早期脚本（本页） |

CI 构建流程（`build-car-apk.yml`，推送到 `car-lyrics-2x-clock` 即自动触发，约 8–10 分钟）：

1. 从上游 `git clone` 并 `checkout v1.1.1`，得到干净源码
2. 取本仓库的 `car-lyrics-2x-clock.patch` 并 `git apply`
3. `sed` 改 applicationId 为 `.xl2c`、应用名为「LynMusic 大字版」、写入版本号
4. 用固定 keystore 签名（存于仓库 Secrets `SIGNING_KEYSTORE_BASE64`），产出 universal APK

### 二、安装说明

- **产物**：`LynMusic-Car-XL2C-clock-release-universal`（universal 包，含 arm64-v8a / armeabi-v7a / x86 / x86_64）
- **包名**：`top.iwesley.lyn.music.automotive.xl2c` —— 与原版不同，可与官方版共存
- **签名固定**：v4 及以后各版本签名一致，**可直接覆盖安装，无需卸载**（v3 及以前为 CI 临时签名，需先卸载）
- **目标设备**：卡仕达炫耀版 PLUS（SDM450 / Android 11 / 1280×720）

### 三、功能改动总览（相对上游 v1.1.1）

#### 显示与字号

- **全局字号 ×1.5、行高 ×1.7**：设置、曲库、我的等非播放页面文字放大，增加列表行间距
- **播放页独立字号基准**：播放页不走全局缩放，避免 1.5×1.5 双重放大导致文字超大、歌名截断
- **歌词 2 倍放大**
- **`lineHeight` 随字号同步放大**：修复时间、歌手、歌名的竖向裁切

#### 最大化（纯净）模式布局

- **封面最大化**：高度占比 0.76、边距归零，1280×720 下约 416dp
- **顶部时钟**：居中显示、去掉日期，仅最大化页显示
- **顶槽三段式布局**：收藏钮贴左、清单钮贴右、时钟居中（避免与按钮重叠）
- **顶槽空白区可点** = 退出最大化
- **底部控制条在最大化时收起**，把空间让给封面与歌名

#### 交互与触发区

- **整块歌词区可点**：在「播放页 ↔ 最大化」之间切换，双指手势仍保留
- **封面点按 = 播放/暂停**（普通页与最大化页行为统一）
- **移除播放区整块隐式点击**（原「点按进入最大化」与控制按钮冲突，易误触）
- **删除歌词区上下 64dp 触发带**：歌词区净增高约 128dp（约 +25%）
- **进度条**：最大化页歌词区底部、拉满宽度、**可拖动跳转**（拉歌）；拖动条为独立触控目标，不会误触整屏切换
- **进度条左侧两个大按钮**（64dp）：播放模式（顺序/随机/单曲）、搜索歌词
- **顶部按钮触控区放大** 1.3 倍

#### 新增功能

- **开机自动启动**：设置页新增开关，开机或应用更新后自动拉起应用
  ⚠️ 多数车机系统默认禁止第三方应用自启动，需在车机「应用管理 / 自启动管理」中放行，这是系统级限制
- **扫描本地文件夹置顶入口**：来源页顶部专用按钮（系统选择器 / 内置 / 自动三态）

### 四、版本演进

| 版本 | versionCode | 主要改动 |
|---|---|---|
| v2 | 11106 | 时钟仅最大化页显示并去掉日期；最大化页精简；全局 UI 放大 1.5× |
| v3 | 11107 | 封面填满宽度、去专辑名；非播放页文字放大 1.5× |
| v4 | 11108 | 修复播放页双重放大；时钟行高防裁切；**启用固定签名**（此后可覆盖安装） |
| v5 | 11109 | 触发区重分配防误触；封面点按统一为播放/暂停 |
| v6 | 11110 | 应用名统一为「LynMusic 大字版」；封面 0.76；**签名密钥移入 GitHub Secrets** |
| v7 | 11111 | 删除歌词区上下触发带（歌词区 +128dp）；整块歌词可点切换；进度条可拖动 |
| v8 | 11112 | 最大化页加 84dp 播放列表按钮；顶槽空白区可点退出 |
| v9 | 11113 | 修复时间/歌手/歌名竖向裁切；加收藏钮；全局点击切换 |
| v10 | 11114 | 修复收藏钮与时钟重叠，顶槽改为三段式布局 |
| v11 | 11115 | 进度条左侧加「播放模式」「搜索歌词」两钮；顶部搜索钮触控区 ×1.3 |
| v12 | 11116 | 上述两钮放大至 64dp；顶部最大化钮 ×1.3；**开机自启**；**扫描本地文件夹置顶** |

> `car-lyrics-2x` 与 `car-lyrics-3x` 是 v1 时期的歌词 2×/3× 对比实验分支，已被 `car-lyrics-2x-clock` 取代。

### 五、许可证

沿用上游 GPL-3.0-or-later，完整文本见 [LICENSE](./LICENSE)。
