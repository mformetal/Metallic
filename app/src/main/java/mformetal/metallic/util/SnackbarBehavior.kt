package mformetal.metallic.util;

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * @author - mbpeele on 11/23/17
 */
class SnackbarBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<ViewGroup>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ViewGroup, dependency: View): Boolean =
            dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewGroup, dependency: View): Boolean {
        val translationY = Math.max(0f, dependency.height - dependency.translationY)
        child.translationY = -translationY
        return true
    }
}