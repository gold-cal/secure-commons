package com.secure.commons.activities

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.content.res.AppCompatResources
import com.secure.commons.R
import com.secure.commons.databinding.ActivityInfoBinding
import com.secure.commons.databinding.ItemCardBinding
import com.secure.commons.extensions.*
import com.secure.commons.helpers.APP_FAQ
import com.secure.commons.helpers.APP_ICON_IDS
import com.secure.commons.helpers.APP_LAUNCHER_NAME
import com.secure.commons.helpers.NavigationIcon
import com.secure.commons.models.FAQItem

class FAQActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    private val binding by viewBinding(ActivityInfoBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateMaterialActivityViews(binding.infoCoordinator, binding.infoHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        //setupMaterialScrollListener(binding.faqNestedScrollview, binding.faqToolbar)

        val textColor = getProperTextColor()
        val backgroundColor = getProperBackgroundColor()
        val accentColor = getProperAccentColor()

        //val inflater = LayoutInflater.from(this)
        val faqItems = intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        faqItems.forEach { faq ->
            val itemFaq = ItemCardBinding.inflate(layoutInflater).apply {
                itemCard.setCardBackgroundColor(backgroundColor)
                //faqCard.background = AppCompatResources.getDrawable(applicationContext, R.drawable.section_holder_stroke)
                itemLayout.background = AppCompatResources.getDrawable(applicationContext, R.drawable.section_holder_stroke)
                itemTitle.apply {
                    text = if (faq.title is Int) getString(faq.title) else faq.title as String
                    setTextColor(accentColor)
                }

                itemText.apply {
                    text = if (faq.text is Int) Html.fromHtml(getString(faq.text)) else faq.text as String
                    setTextColor(textColor)
                    setLinkTextColor(accentColor)

                    movementMethod = LinkMovementMethod.getInstance()
                    removeUnderlines()
                }
            }
            /*inflater.inflate(R.layout.item_faq, null).apply {
                faq_card.setCardBackgroundColor(backgroundColor)
                faq_title.apply {
                    text = if (faq.title is Int) getString(faq.title) else faq.title as String
                    setTextColor(accentColor)
                }

                faq_text.apply {
                    text = if (faq.text is Int) Html.fromHtml(getString(faq.text)) else faq.text as String
                    setTextColor(textColor)
                    setLinkTextColor(accentColor)

                    movementMethod = LinkMovementMethod.getInstance()
                    removeUnderlines()
                }

                faq_holder.addView(this)
            }*/
            binding.infoHolder.addView(itemFaq.root)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.infoToolbar.title = getString(R.string.frequently_asked_questions)
        setupToolbar(binding.infoToolbar, NavigationIcon.Arrow)
    }
}
