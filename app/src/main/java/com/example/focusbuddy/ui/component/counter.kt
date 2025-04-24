package com.example.focusbuddy.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.focusbuddy.R

class counter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private var count = 1
    private val tvCount: TextView
    private val btnMinus: Button
    private val btnPlus: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.counter, this, true)

        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        tvCount = findViewById(R.id.tvCount)

        updateView()

        btnPlus.setOnClickListener {
            count++
            updateView()
        }

        btnMinus.setOnClickListener {
            if (count > 0) {
                count--
                updateView()
            }
        }
    }

    private fun updateView() {
        tvCount.text = count.toString()
    }

    fun getValue(): Int = count
    fun setValue(newCount: Int) {
        count = newCount
        updateView()
    }
}
