package com.tomy.lib.ui.preference.widget

/**@author Tomy
 * Created by Tomy on 21/7/2020.
 */
interface WidgetController {

    fun setupView()

    fun teardownView()

    /**
     * Start listening to switch toggling.
     */
    fun startListening()

    /**
     * Stop listening to switch toggling.
     */
    fun stopListening()

    /**
     * Set the enabled state for the switch.
     *
     * @param enabled whether the switch should be enabled or not.
     */
    fun setEnabled(enabled: Boolean)

}