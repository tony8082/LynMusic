package top.iwesley.lyn.music.automotive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import top.iwesley.lyn.music.readAutoStartOnBoot

// 车机开机自启广播接收器：开机完成或应用更新后，若用户在设置页开启“开机自动启动”，则拉起 MainActivity。
// 与 player/app 中 SettingsUi 的“开机自动启动”开关共用同一份 SharedPreferences 偏好。
class AutoStartBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                if (readAutoStartOnBoot(context)) {
                    val launch = Intent(context, MainActivity::class.java).apply {
                        addFlags(FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(launch)
                }
            }
        }
    }
}
