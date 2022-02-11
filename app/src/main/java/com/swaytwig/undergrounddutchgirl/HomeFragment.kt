package com.swaytwig.undergrounddutchgirl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        val versionName = BuildConfig.VERSION_NAME
        val versionCode = getString(R.string.ver_code)
        val verTextHtml = String.format("ver <i>%s %s</i>", versionCode, versionName)

        val verText = v.findViewById<TextView>(R.id.version_text)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N)
            @Suppress("DEPRECATION")
            verText.text = Html.fromHtml(verTextHtml)
        else
            verText.text = Html.fromHtml(verTextHtml, Html.FROM_HTML_MODE_LEGACY)

        val linkText = v.findViewById<TextView>(R.id.link_text)
        linkText.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_link)))
            startActivity(browserIntent)
        }

        return v
    }
}
