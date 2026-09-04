package top.iwesley.lyn.music

// 车机开机自启开关（自包含 SharedPreferences 实现，避免改动跨平台 Preferences 体系）
// 设置页 Switch 与 automotiveApp 的 BootReceiver 共用同一偏好。
expect fun readAutoStartOnBoot(context: Any): Boolean
expect fun writeAutoStartOnBoot(context: Any, enabled: Boolean)
