package com.swaytwig.undergrounddutchgirl

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.swaytwig.undergrounddutchgirl.TextureData.TextureData


class TextureAdapter(context: Context, data: Array<TextureData>, listener: ListSwitchChangedListener) : BaseAdapter() {
    interface ListSwitchChangedListener {
        fun onListSwitchChanged(position: Int, checked: Boolean)
    }

    private val ctx: Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    private var list: Array<TextureData> = data
    private val listSwitchChangedListener: ListSwitchChangedListener = listener

    override fun getCount(): Int = list.size
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): TextureData = list[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        @SuppressLint("InflateParams")
        val view = convertView ?: inflater.inflate(R.layout.layout_texture_item, null, true)
        val item = list[position]

        val text = view.findViewById<TextView>(R.id.item_display)
        text.text = item.text

        val switch = view.findViewById<SwitchMaterial>(R.id.item_switch)
        switch.setOnClickListener {
            val checked = (it as SwitchMaterial).isChecked
            item.useOneStore = checked
            listSwitchChangedListener.onListSwitchChanged(position, checked)
        }
        switch.isChecked = item.useOneStore

        view.findViewById<LinearLayout>(R.id.item_switch_false).setOnClickListener {
            switch.isChecked = false
            listSwitchChangedListener.onListSwitchChanged(position, false)
        }
        view.findViewById<LinearLayout>(R.id.item_switch_true).setOnClickListener {
            switch.isChecked = true
            listSwitchChangedListener.onListSwitchChanged(position, true)
        }

        return view
    }
}