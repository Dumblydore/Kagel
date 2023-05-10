package me.mauricee.kagel.sample

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import me.mauricee.kagel.BindView

@BindView
@Composable
fun ComposableText(text: String) = BasicText(text = text)
