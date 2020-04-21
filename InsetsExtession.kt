package com.insets

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import ru.inmoso.core.ui.inset.InsetDimension
import ru.inmoso.core.ui.inset.ViewState
import java.util.*


/**
 * Набор расширений для применения инсетов
 *
 * @author Nikita Marsyukov
 */

/**
 * Обработь инсеты
 *
 * @param block лямбда в которой реализуется обработка инсетов
 *
 * */
fun View.doOnApplyWindowInsets(
    block: (View, insets: WindowInsetsCompat, initialViewState: ViewState) -> Unit
) {
    val initialViewState = ViewState.fromView(this)

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialViewState)
        // Всегда возвращайте оригинальные инсеты, чтобы дочерние вью могли их использовать
        insets
    }
    requestApplyInsetsWhenAttached()
}

/**
 * Запросить инсеты
 * */
fun View.requestApplyInsetsWhenAttached() {
    // Если вью была за аттачена то запросить инсеты
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        // Если вью не была за аттачена то повешать слушатель на аттач, и когада за аттачиться
        // запросить инсеты
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

fun Insets.copy(f: (insets: Rect) -> Rect): Insets {
    val initialRect = Rect(left, top, right, bottom)
    return Insets.of(f(initialRect))
}


/**
 * Обработать инсеты к внутрнним и внешним отсупам
 *
 * @param insets
 * @param initialViewState начальное состояние вью
 * @param paddingSystemWindowInsets набор внутренних стронон (Padding) к котороым нужно применить инстеы
 * @param marginSystemWindowInsets набор внутренних стронон (Margin) к котороым нужно применить инстеы
 * */
fun View.applyInsets(
    insets: Insets,
    initialViewState: ViewState,
    marginSystemWindowInsets: EnumSet<InsetDimension>? = null,
    paddingSystemWindowInsets: EnumSet<InsetDimension>? = null
) {

    if (marginSystemWindowInsets != null) {
        applyInsetsToMargins(insets, initialViewState, marginSystemWindowInsets)
    }

    if (paddingSystemWindowInsets != null) {
        applyInsetsToPadding(insets, initialViewState, paddingSystemWindowInsets)
    }

}

/**
 * Обработать инсеты с примененим их к внутренним отсупам (Padding)
 *
 * @param insets
 * @param initialViewState изначальное состоения вью
 * @param paddingSystemWindowInsets набор сторон к которым требуется применить инсеты
 * */
fun View.applyInsetsToPadding(
    insets: Insets,
    initialViewState: ViewState,
    paddingSystemWindowInsets: EnumSet<InsetDimension>
) {
    var viewPaddingLeft = paddingLeft
    val initialPadding = initialViewState.paddings;

    if (InsetDimension.LEFT in paddingSystemWindowInsets) {
        viewPaddingLeft = initialPadding.left + insets.left
    }

    var viewPaddingTop = paddingTop
    if (InsetDimension.TOP in paddingSystemWindowInsets) {
        viewPaddingTop = initialPadding.top + insets.top
    }

    var viewPaddingRight = paddingRight
    if (InsetDimension.RIGHT in paddingSystemWindowInsets) {
        viewPaddingRight = initialPadding.right + insets.right
    }

    var viewPaddingBottom = paddingBottom
    if (InsetDimension.BOTTOM in paddingSystemWindowInsets) {
        viewPaddingBottom = initialPadding.bottom + insets.bottom
    }

    updatePadding(
        left = viewPaddingLeft,
        top = viewPaddingTop,
        right = viewPaddingRight,
        bottom = viewPaddingBottom
    )

}

/**
 * Обработать инсеты с примененим их к внешним отсупам (Margin)
 *
 * @param insets
 * @param initialViewState изначальное состоения вью
 * @param marginSystemWindowInsets набор сторон к которым требуется применить инсеты
 * */
fun View.applyInsetsToMargins(
    insets: Insets,
    initialViewState: ViewState = ViewState.fromView(this),
    marginSystemWindowInsets: EnumSet<InsetDimension>
) {

    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val initialMargins = initialViewState.margins

        updateLayoutParams<ViewGroup.MarginLayoutParams> {

            if (InsetDimension.LEFT in marginSystemWindowInsets) {
                leftMargin = initialMargins.left + insets.left
            }

            if (InsetDimension.TOP in marginSystemWindowInsets) {
                topMargin = initialMargins.top + insets.top
            }

            if (InsetDimension.RIGHT in marginSystemWindowInsets) {
                rightMargin = initialMargins.right + insets.right
            }

            if (InsetDimension.BOTTOM in marginSystemWindowInsets) {
                bottomMargin = initialMargins.bottom + insets.bottom
            }

        }

    } else require((marginSystemWindowInsets.isNotEmpty())) {
        ("Margin inset handling requested but view LayoutParams do not extend MarginLayoutParams")
    }

}

/**
 * Применить инсеты в внутрнним и внешним отсупам
 *
 * @param paddingSystemWindowInsets набор внутренних стронон (Padding) к котороым нужно применить инстеы
 * @param marginSystemWindowInsets набор внутренних стронон (Margin) к котороым нужно применить инстеы
 * */
fun View.applyInsets(
    paddingSystemWindowInsets: EnumSet<InsetDimension>? = null,
    marginSystemWindowInsets: EnumSet<InsetDimension>? = null
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        view.applyInsets(
            insets = insets.systemWindowInsets,
            initialViewState = initialViewState,
            paddingSystemWindowInsets = paddingSystemWindowInsets,
            marginSystemWindowInsets = marginSystemWindowInsets
        )
    }

}

/**
 * Применить инсеты к внутренним отступам (Padding)
 *
 * @param left  применить в левому отступу если true
 * @param top  применить в верхнему отступу если true
 * @param right  применить в правому отступу если true
 * @param bottom  применить в нижнему отступу если true
 * */
fun View.applySystemWindowInsetsToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        view.applyInsetsToPadding(
            insets.systemWindowInsets,
            initialViewState,
            InsetDimension.generateInsetDimensions(
                left = left,
                top = top,
                right = right,
                bottom = bottom
            )
        )
    }
}

/**
 * Применить инсеты к внешним отступам (Margin)
 *
 * @param left  применить в левому отступу если true
 * @param top  применить в верхнему отступу если true
 * @param right  применить в правому отступу если true
 * @param bottom  применить в нижнему отступу если true
 * */
fun View.applySystemWindowInsetsToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, initialViewState ->
        view.applyInsetsToMargins(
            insets.systemWindowInsets,
            initialViewState,
            InsetDimension.generateInsetDimensions(
                left = left,
                top = top,
                right = right,
                bottom = bottom
            )

        )
    }
}
