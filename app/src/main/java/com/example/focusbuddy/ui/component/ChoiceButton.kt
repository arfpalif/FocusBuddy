package com.example.focusbuddy.ui.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.focusbuddy.R

class ChoiceButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    var isSelectedChoice = false
        set(value) {
            field = value
            updateStyle()
        }

    init {
        setBackgroundResource(R.drawable.bg_choice_unselected)
        setTextColor(ContextCompat.getColor(context, R.color.primary))
        textSize = 16f
        setPadding(32, 16, 32, 16)

        setOnClickListener {
            val parent = parent
            if (parent is android.view.ViewGroup) {
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    if (child is ChoiceButton) {
                        child.isSelectedChoice = false
                    }
                }
            }
            isSelectedChoice = true
            updateStyle()
        }
    }

    private fun updateStyle() {
        if (isSelectedChoice) {
            setBackgroundResource(R.drawable.bg_choice_selected)
            setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            setBackgroundResource(R.drawable.bg_choice_unselected)
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}
