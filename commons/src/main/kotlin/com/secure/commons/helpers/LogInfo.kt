package com.secure.commons.helpers

class LogInfo {
    companion object {
        private var instance: LogInfo? = null
        
        fun getInstance(): LogInfo {
            if (instance == null) {
                synchronized(LogInfo::class) {
                    if (instance == null) {
                        instance = LogInfo()
                    }
                }
            }
            return instance!!
        }
    }

    private val debugLogInfo = ArrayList<String>()

    fun addDebugLog(log: String) {
        debugLogInfo.add(log)
    }

    fun getDebugLogs(): ArrayList<String> {
        return debugLogInfo
    }

}
