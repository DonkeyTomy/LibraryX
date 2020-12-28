package com.tomy.lib.ui.preference.widget

/**@author Tomy
 * Created by Tomy on 21/7/2020.
 */
interface IBaseEnabler {

    fun setupController()

    fun tearDownController()

    fun resume()

    fun pause()

    fun handleStateChanged(state: Int)

    fun startListening()

    fun stopListening()

}