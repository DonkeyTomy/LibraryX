package com.tomy.compose.navigation

object NavUtils {

    fun getRoute(route: String, vararg params: Any): String {
        val buffer = StringBuffer()
        params.forEach {
            buffer.append("/$it")
        }
        return "$route$buffer"
    }

}