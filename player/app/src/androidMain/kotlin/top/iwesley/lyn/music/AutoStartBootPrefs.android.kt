package top.iwesley.lyn.music

import android.content.Context

// 车机开机自启开关（自包含实现，与 automotiveApp 的 BootReceiver 共用同一偏好）
private const val AUTO_START_PREF_NAME = "lyn_auto_start"
private const val AUTO_START_PREF_KEY = "auto_start_on_boot"

actual fun readAutoStartOnBoot(context: Any): Boolean {
    val ctx = context as Context
    return ctx.getSharedPreferences(AUTO_START_PREF_NAME, Context.MODE_PRIVATE)
        .getBoolean(AUTO_START_PREF_KEY, false)
}

actual fun writeAutoStartOnBoot(context: Any, enabled: Boolean) {
    val ctx = context as Context
    ctx.getSharedPreferences(AUTO_START_PREF_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(AUTO_START_PREF_KEY, enabled)
        .apply()
}
