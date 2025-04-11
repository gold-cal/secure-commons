package com.secure.commons.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.secure.commons.databinding.CrashActivityBinding
import com.secure.commons.R

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = CrashActivityBinding.inflate(layoutInflater, null, false)

        setContentView(binding.root)

        val error = intent.getStringExtra("report")

        binding.txtCrash.text = error
        binding.txtCrash.setTextColor(resources.getColor(R.color.default_text_color, null))

        binding.closeApp.setOnClickListener {
            this.finishAffinity()
        }
    }
}
