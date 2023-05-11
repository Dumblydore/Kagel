package me.mauricee.kagel.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import me.mauricee.kagel.BindView

@BindView
@Composable
fun ComposableText(text: String) = Box(modifier = Modifier.background(Color.Red).padding(16.dp)) {
    BasicText(
        text = text,
        style = TextStyle(color = Color.White),
        modifier = Modifier.align(Alignment.Center),
    )
}

@BindView
@Composable
fun ComposableText2(text: String, text2: String) =
    Column(modifier = Modifier.background(Color.Red).padding(16.dp)) {
        BasicText(
            text = text,
            style = TextStyle(color = Color.White),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        BasicText(
            text = text2,
            style = TextStyle(color = Color.White),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }

//@BindView
//@Composable
//fun ComposableText3(texts: List<String>) =
//    Column(modifier = Modifier.background(Color.Red).padding(16.dp)) {
//        for (text in texts) {
//            BasicText(
//                text = text,
//                style = TextStyle(color = Color.White),
//                modifier = Modifier.align(Alignment.CenterHorizontally),
//            )
//        }
//    }
