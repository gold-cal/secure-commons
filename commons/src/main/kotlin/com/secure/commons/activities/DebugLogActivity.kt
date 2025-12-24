package com.secure.commons.activities

import android.os.Bundle
import com.secure.commons.databinding.ActivityDebugLogBinding
import com.secure.commons.databinding.ItemLogBinding
import com.secure.commons.helpers.LogInfo
import com.secure.commons.extensions.getProperTextColor
import com.secure.commons.extensions.viewBinding
import com.secure.commons.helpers.APP_ICON_IDS
import com.secure.commons.helpers.APP_LAUNCHER_NAME
import com.secure.commons.helpers.NavigationIcon

class DebugLogActivity : BaseSimpleActivity() {
    private val binding by viewBinding(ActivityDebugLogBinding::inflate)

    override fun getAppIconIDs(): ArrayList<Int> = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName(): String = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.debugLogToolbar, NavigationIcon.Arrow)
        loadDebugInfo()
    }

    private fun loadDebugInfo() {
        val logInfo = LogInfo.getInstance()
        val logs = logInfo.getDebugLogs()
        if (logs.isEmpty()) {
            val view = createLog()
            view.itemDebugInfo.apply {
                text = "No logs to Show!!"
                setTextColor(getProperTextColor())
            }
            binding.debugLogHolder.addView(view.root)
        } else {
            for (log in logs) {
                val view = createLog()
                view.itemDebugInfo.apply {
                    text = log
                    setTextColor(getProperTextColor())
                }
                binding.debugLogHolder.addView(view.root)
            }
        }
    }

    private fun createLog() = ItemLogBinding.inflate(layoutInflater, null, false)

}
