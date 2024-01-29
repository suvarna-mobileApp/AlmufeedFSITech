package com.almufeed.technician.business.domain.utils.exceptionView

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.almufeed.technician.R
import com.google.android.material.textview.MaterialTextView

fun createExceptionView(
    type: String,
    layoutInflater: LayoutInflater,
    container: FrameLayout,
    message: String
) {

    when (type) {
        ExceptionItems.EMPTY.label -> {

            val emptyView: View =
                LayoutInflater.from(layoutInflater.context)
                    .inflate(R.layout.layout_no_data, container, false)

            val messageView: MaterialTextView =
                emptyView.findViewById<View>(R.id.text_message) as MaterialTextView

            if (message.isNotEmpty())
                messageView.text = message

            container.addView(emptyView)

        }
        ExceptionItems.NO_NETWORK.label -> {

        }
        ExceptionItems.SOMETHING_WENT_WRONG.label -> {

        }
    }
}