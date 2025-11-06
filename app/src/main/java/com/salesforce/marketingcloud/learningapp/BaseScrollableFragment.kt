/**
 * Copyright 2019 Salesforce, Inc.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.marketingcloud.learningapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.LayoutRes

/**
 * Base fragment that provides consistent scrollable layout with proper bottom padding.
 * Extend this class for screens that need scrollable content with consistent spacing.
 */
abstract class BaseScrollableFragment : SdkFragment() {

    override val layoutId: Int get() = R.layout.base_scrollable_content

    protected lateinit var contentContainer: LinearLayout
    private lateinit var scrollView: ScrollView

    /**
     * Override this to provide the layout resource for your screen content.
     * This layout will be inflated and added to the scrollable container.
     */
    @get:LayoutRes
    abstract val contentLayoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Find the content container in the base layout
        scrollView = view!!.findViewById(R.id.scroll_view) ?: view as ScrollView
        contentContainer = view.findViewById<LinearLayout>(android.R.id.content)
            ?: view.findViewById(R.id.content_container)
                    ?: (view as? ViewGroup)?.getChildAt(0) as LinearLayout

        // Inflate and add the specific content layout
        val contentView = inflater.inflate(contentLayoutId, contentContainer, false)
        contentContainer.addView(contentView)

        return view
    }

    /**
     * Convenience method to add proper bottom margin to the last card in your layout.
     * Call this in your setupUI() method for the last card/view.
     */
    protected fun applyLastCardStyle(view: View) {
        val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParams?.bottomMargin =
            resources.getDimensionPixelSize(R.dimen.last_card_bottom_margin)
        view.layoutParams = layoutParams
    }

    /**
     * Convenience method to create a card with consistent styling.
     */
    protected fun createStyledCard(
        context: android.content.Context,
        isLastCard: Boolean = false
    ): com.google.android.material.card.MaterialCardView {
        return com.google.android.material.card.MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = if (isLastCard) {
                    resources.getDimensionPixelSize(R.dimen.last_card_bottom_margin)
                } else {
                    resources.getDimensionPixelSize(R.dimen.card_bottom_margin)
                }
            }

            // Apply Material Design 3 card styling
            cardElevation = resources.getDimension(R.dimen.card_elevation)
            radius = resources.getDimension(R.dimen.card_corner_radius)
            setCardBackgroundColor(context.getColor(R.color.md_theme_light_surface))
        }
    }
}

