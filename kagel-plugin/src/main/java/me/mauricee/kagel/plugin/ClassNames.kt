package me.mauricee.kagel.plugin

import com.squareup.kotlinpoet.ClassName

object ClassNames {
    val androidContext = ClassName("android.content", "Context")
    val androidView = ClassName("android.view", "View")
    val composeView = ClassName("androidx.compose.ui.platform", "AbstractComposeView")
    val mutableState = ClassName("androidx.compose.runtime", "MutableState")
    val mutableStateOf = ClassName("androidx.compose.runtime", "mutableStateOf")
    val composable = ClassName("androidx.compose.runtime", "Composable")
}