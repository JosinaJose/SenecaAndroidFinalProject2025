package com.safemail.safemailapp.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal
        ),
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TextFields(
    labelValue: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedLabelColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions.Default,
        value = value,
        onValueChange = onValueChange,
        singleLine = true
    )
}

@Composable
fun PasswordTextField(
    labelValue: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = labelValue) },
        singleLine = true,
        visualTransformation =
            if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Icon(
                imageVector =
                    if (showPassword) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff,
                contentDescription =
                    if (showPassword) "Hide password" else "Show password",
                modifier = Modifier.clickable {
                    showPassword = !showPassword
                }
            )
        }
    )
}
@Composable
fun ButtonComponent(
    value: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Blue, Color.White)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun TextButtons(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    TextButton(onClick = onClick) {
        content()
    }
}
