package top.iwesley.lyn.music

// 非 Android 平台（iOS / macOS）无需开机自启，提供空实现以满足 expect/actual 多平台编译要求。
actual fun readAutoStartOnBoot(context: Any): Boolean = false
actual fun writeAutoStartOnBoot(context: Any, enabled: Boolean) = Unit
