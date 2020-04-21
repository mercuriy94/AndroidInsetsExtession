package com.insets

import java.util.*

/**
 * Перечисление, содержащее стороны, к которым можно применять инсеты.
 *
 * @author Nikita Marsyukov
 */
enum class InsetDimension {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    companion object {

        fun generateInsetDimensions(
            left: Boolean = false,
            top: Boolean = false,
            right: Boolean = false,
            bottom: Boolean = false
        ): EnumSet<InsetDimension> =
            EnumSet.noneOf(InsetDimension::class.java)
                .apply {
                    if (left) add(LEFT)
                    if (top) add(TOP)
                    if (right) add(RIGHT)
                    if (bottom) add(BOTTOM)
                }

    }
}

