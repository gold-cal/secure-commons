package com.secure.commons.samples.activities

import android.os.Bundle
import com.secure.commons.activities.BaseSimpleActivity
//import com.secure.commons.dialogs.BottomSheetChooserDialog
import com.secure.commons.extensions.appLaunched
//import com.secure.commons.extensions.toast
import com.secure.commons.helpers.LICENSE_JODA
import com.secure.commons.models.FAQItem
import com.secure.commons.R.string
import com.secure.commons.models.AboutItems
import com.secure.commons.samples.BuildConfig
import com.secure.commons.samples.R
import com.secure.commons.samples.databinding.ActivityMainBinding

class MainActivity : BaseSimpleActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun getAppLauncherName() = getString(R.string.smtco_app_name)

    override fun getAppIconIDs(): ArrayList<Int> {
        val ids = ArrayList<Int>()
        ids.add(R.mipmap.commons_launcher)
        return ids
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appLaunched(BuildConfig.APPLICATION_ID)

        updateMaterialActivityViews(binding.mainCoordinator, binding.mainHolder,
            useTransparentNavigation = true, useTopSearchMenu = false)
        //setupMaterialScrollListener(binding.mainNestedScrollview, binding.mainToolbar)

        binding.aboutActivity.setOnClickListener {
            val licenses = LICENSE_JODA
            val faqItems = arrayListOf(
                FAQItem(string.faq_1_title_commons, string.faq_1_text_commons),
                FAQItem(string.faq_4_title_commons, string.faq_4_text_commons)
            )
            val showItems = AboutItems(false, false, "", "")
            startAboutActivity(R.string.smtco_app_name,licenses,BuildConfig.VERSION_NAME, faqItems,true, showItems)
        }

        binding.mainColorCustomization.setOnClickListener {
            startCustomizationActivity()
        }

        //startCustomizationActivity()
        //startAboutActivity(R.string.smtco_app_name, 3, "0.2", arrayListOf(FAQItem(R.string.faq_1_title_commons, R.string.faq_1_text_commons)), false)

        /*val letters = arrayListOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q")
        StringsAdapter(this, letters, media_grid, media_refresh_layout) {
        }.apply {
            media_grid.adapter = this
        }

        media_refresh_layout.setOnRefreshListener {
            Handler().postDelayed({
                media_refresh_layout.isRefreshing = false
            }, 1000L)
        }*/
    }

    /*private fun launchBottomSheetDemo() {
        BottomSheetChooserDialog.createChooser(
            fragmentManager = supportFragmentManager,
            title = R.string.please_select_destination,
            items = arrayOf(
                SimpleListItem(1, R.string.record_video, R.drawable.ic_camera_vector),
                SimpleListItem(2, R.string.record_audio, R.drawable.ic_microphone_vector, selected = true),
                SimpleListItem(4, R.string.choose_contact, R.drawable.ic_add_person_vector)
            )
        ) {
            toast("Clicked ${it.id}")
        }
    }*/

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.mainToolbar)
    }
}
