package com.secure.commons.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import com.secure.commons.R
import com.secure.commons.databinding.ActivityAboutBinding
import com.secure.commons.databinding.ItemAboutBinding
import com.secure.commons.dialogs.ConfirmationAdvancedDialog
import com.secure.commons.extensions.*
import com.secure.commons.helpers.*
import com.secure.commons.models.FAQItem

class AboutActivity : BaseSimpleActivity() {
    private var appName = ""
    private var primaryColor = 0
    private var accentColor = 0
    private var textColor = 0
    private var backgroundColor = 0
    private var inflater: LayoutInflater? = null
    private lateinit var faqItems: ArrayList<FAQItem>

    private val binding by viewBinding(ActivityAboutBinding::inflate)

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        primaryColor = getProperPrimaryColor()
        accentColor = getProperAccentColor()
        textColor = getProperTextColor()
        backgroundColor = getProperBackgroundColor()
        inflater = LayoutInflater.from(this)
        faqItems = intent.serializable<ArrayList<FAQItem>>(APP_FAQ)

        updateMaterialActivityViews(binding.aboutCoordinator, binding.aboutHolder,
            useTransparentNavigation = true, useTopSearchMenu = false)
        //setupMaterialScrollListener(binding.aboutNestedScrollview, binding.aboutToolbar)

        appName = intent.getStringExtra(APP_NAME) ?: ""

        arrayOf(binding.aboutSupport, binding.aboutHelpUs, binding.aboutSocial, binding.aboutOther).forEach {
            it.setTextColor(accentColor)
        }
        binding.aboutHelpUs.beGone()
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(binding.aboutNestedScrollview)
        setupToolbar(binding.aboutToolbar, NavigationIcon.Arrow)

        // deprecated in api level 33
        val showItems = intent.getBooleanArrayExtra(ABOUT_ITEMS_TO_SHOW) as BooleanArray
        val urls = intent.getStringArrayListExtra(ABOUT_ITEMS_URIS) as ArrayList<String>
        val showBugReport = showItems[0]
        val showNewFeature = showItems[1]
        val forkedUri = urls[0]
        val codeUri = urls[1]


        binding.apply {
            if (faqItems.isNotEmpty() || showBugReport || showNewFeature) {
                aboutSupportLayout.removeAllViews()
            } else {
                aboutSupportLayout.beGone()
            }
            //aboutHelpUsLayout.removeAllViews()
            aboutHelpUsLayout.beGone()
            if (forkedUri.isNotEmpty() || codeUri.isNotEmpty()) {
                aboutSocialLayout.removeAllViews()
            } else {
                aboutSocialLayout.beGone()
            }
            aboutOtherLayout.removeAllViews()
        }
        //binding.aboutSupportLayout.removeAllViews()
        //binding.aboutHelpUsLayout.removeAllViews()
        //binding.aboutSocialLayout.removeAllViews()
        //about_other_layout.removeAllViews()

        setupFAQ()
        if (showBugReport) setupBugReport()
        if (showNewFeature) setupEmail()
        if (forkedUri.isNotEmpty()) setupForkedFromGitHub(forkedUri)
        if (codeUri.isNotEmpty()) setupSourceCode(codeUri)
        //setupPrivacyPolicy()
        setupLicense()
        setupVersion()
    }

    private fun setupFAQ() {
        //val faqItems = intent.serializable<ArrayList<FAQItem>>(APP_FAQ) // as ArrayList<FAQItem>
        if (faqItems.isNotEmpty()) {
            val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
            setupAboutItem(itemBinding, R.drawable.ic_question_mark_vector, R.string.frequently_asked_questions)
            binding.aboutSupportLayout.addView(itemBinding.root)
            itemBinding.root.setOnClickListener {
                launchFAQActivity()
            }
        }
    }

    private fun launchFAQActivity() {
        //val faqItems = intent.serializable<ArrayList<FAQItem>>(APP_FAQ)// as ArrayList<FAQItem>
            //intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        Intent(applicationContext, FAQActivity::class.java).apply {
            putExtra(APP_ICON_IDS, getAppIconIDs())
            putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
            putExtra(APP_FAQ, faqItems)
            startActivity(this)
        }
    }

    private fun setupBugReport() {
        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
        setupAboutItem(itemBinding, R.drawable.ic_bug_vector, R.string.report_bug)
        binding.aboutSupportLayout.addView(itemBinding.root)
        itemBinding.root.setOnClickListener {
            launchBugReportIntent()
        }
    }

    private fun launchBugReportIntent() {
        val appVersion = String.format(getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME)))
        val deviceOS = String.format(getString(R.string.device_os), Build.VERSION.RELEASE)
        val newline = "\n"
        val separator = "------------------------------"
        val bugReport = "BUG REPORT:"
        val note = "Please describe what you were doing and the steps to reproduce the problem!"
        val body = "$appVersion$newline$deviceOS$newline$separator$newline$bugReport$newline$note$newline$newline"
        val address = getString(R.string.my_email)

        val selectorIntent = Intent(ACTION_SENDTO)
            .setData("mailto:$address".toUri())
        val emailIntent = Intent(ACTION_SEND).apply {
            putExtra(EXTRA_EMAIL, arrayOf(address))
            putExtra(EXTRA_SUBJECT, appName)
            putExtra(EXTRA_TEXT, body)
            selector = selectorIntent
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            val chooser = createChooser(emailIntent, getString(R.string.send_email))
            try {
                startActivity(chooser)
            } catch (e: Exception) {
                toast(R.string.no_email_client_found)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    private fun setupEmail() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            if (binding.aboutSupportLayout.isEmpty()) {
                binding.aboutSupport.beGone()
            }

            return
        }

        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
        setupAboutItem(itemBinding, R.drawable.ic_mail_vector, R.string.request_feature)
        binding.aboutSupportLayout.addView(itemBinding.root)
        itemBinding.root.setOnClickListener {
            val msg = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}"
            if (intent.getBooleanExtra(SHOW_FAQ_BEFORE_MAIL, false) && !baseConfig.wasBeforeAskingShown) {
                baseConfig.wasBeforeAskingShown = true
                ConfirmationAdvancedDialog(this@AboutActivity, msg, 0, R.string.read_faq, R.string.skip) { success ->
                    if (success) {
                        launchFAQActivity()
                    } else {
                        launchEmailIntent()
                    }
                }
            } else {
                launchEmailIntent()
            }
        }
    }

    private fun launchEmailIntent() {
        val appVersion = String.format(getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME)))
        val deviceOS = String.format(getString(R.string.device_os), Build.VERSION.RELEASE)
        val newline = "\n"
        val separator = "------------------------------"
        val body = "$appVersion$newline$deviceOS$newline$separator$newline$newline"

        val address = if (packageName.startsWith("com.liturgical")) {
            getString(R.string.my_email)
        } else {
            getString(R.string.my_fake_email)
        }

        val selectorIntent = Intent(ACTION_SENDTO)
            .setData("mailto:$address".toUri())
        val emailIntent = Intent(ACTION_SEND).apply {
            putExtra(EXTRA_EMAIL, arrayOf(address))
            putExtra(EXTRA_SUBJECT, appName)
            putExtra(EXTRA_TEXT, body)
            selector = selectorIntent
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            val chooser = createChooser(emailIntent, getString(R.string.send_email))
            try {
                startActivity(chooser)
            } catch (e: Exception) {
                toast(R.string.no_email_client_found)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    private fun setupForkedFromGitHub(url: String) {
        if (resources.getBoolean(R.bool.hide_all_external_links)) return

        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
        setupAboutItem(itemBinding, R.drawable.ic_github_vector, R.string.forked_from)
        binding.aboutSocialLayout.addView(itemBinding.root)
        itemBinding.root.setOnClickListener {
            launchViewIntent(url)
        }
    }

    private fun setupSourceCode(url: String) {
        if (resources.getBoolean(R.bool.hide_all_external_links)) return

        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
        setupAboutItem(itemBinding, R.drawable.ic_code_vector, R.string.source_code)
        binding.aboutSocialLayout.addView(itemBinding.root)
        itemBinding.root.setOnClickListener {
            launchViewIntent(url)
        }
    }

    // TODO: Need to update this
    /*private fun setupPrivacyPolicy() {
        if (resources.getBoolean(R.bool.hide_all_external_links)) {
            return
        }

        inflater?.inflate(R.layout.item_about, null)?.apply {
            setupAboutItem(this, R.drawable.ic_unhide_vector, R.string.privacy_policy)
            about_other_layout.addView(this)*/

            /*setOnClickListener {
                val appId = baseConfig.appId.removeSuffix(".debug").removeSuffix(".pro").removePrefix("com.liturgical.")
                val url = "https://simplemobiletools.com/privacy/$appId.txt"
                launchViewIntent(url)
            }*/
/*        }
    }*/

    private fun setupLicense() {
        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false)
        setupAboutItem(itemBinding, R.drawable.ic_article_vector, R.string.third_party_licences)
        binding.aboutOtherLayout.addView(itemBinding.root)
        itemBinding.root.setOnClickListener {
            Intent(applicationContext, LicenseActivity::class.java).apply {
                putExtra(APP_ICON_IDS, getAppIconIDs())
                putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
                putExtra(APP_LICENSES, intent.getLongExtra(APP_LICENSES, 0))
                startActivity(this)
            }
        }
    }

    private fun setupVersion() {
        val version = intent.getStringExtra(APP_VERSION_NAME) ?: ""

        val itemBinding = ItemAboutBinding.inflate(layoutInflater, null, false).apply {
            aboutItemIcon.setImageDrawable(resources.getColoredDrawableWithColor(R.drawable.ic_info_vector, textColor))
            val fullVersion = String.format(getString(R.string.version_placeholder, version))
            aboutItemLabel.text = fullVersion
            aboutItemLabel.setTextColor(textColor)
        }

        binding.aboutOtherLayout.addView(itemBinding.root)
    }

    private fun setupAboutItem(itemAbout: ItemAboutBinding, drawableId: Int, textId: Int) {
        itemAbout.apply {
            aboutItemIcon.setImageDrawable(
                resources.getColoredDrawableWithColor(drawableId, textColor))
            aboutItemLabel.setText(textId)
            aboutItemLabel.setTextColor(textColor)
        }

    }
}
