package com.insets

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * Класс для харнения состояния отступов вью [View]
 *
 * [Rect] paddings соответсвуют внутрениним отсупам
 * [Rect] margins соответсвуют внешним отсупам
 *
 * @author Nikita Marsyukov
 */
data class ViewState(val paddings: Rect, val margins: Rect) {

    companion object {

        fun fromView(view: View): ViewState = ViewState(
            paddings = recordInitialPaddingForView(view),
            margins = recordInitialMarginsForView(view)
        )

    }

}

fun recordInitialPaddingForView(view: View): Rect =
    Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

fun recordInitialMarginsForView(view: View): Rect =
    when (val lp = view.layoutParams) {
        is ViewGroup.MarginLayoutParams -> {
            Rect(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        }
        else -> Rect()
    }
