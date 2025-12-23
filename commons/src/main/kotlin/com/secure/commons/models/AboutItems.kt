package com.secure.commons.models

data class AboutItems(val showBugReport: Boolean = true, val showNewFeature: Boolean = true, val forkedUrl: String = "",
                      val showForkedUrl: Boolean, val codeUrl: String = "", val showCodeUrl: Boolean)
