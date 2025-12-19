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
import androidx.compose.ui.draw.alpha
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
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF1C1C1E),
            unfocusedTextColor = Color(0xFF3A3A3C),
            disabledTextColor = Color(0xFF8E8E93),
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFD1D1D6),
            disabledBorderColor = Color(0xFFE5E5EA),
            focusedLabelColor = Color(0xFF1976D2),
            unfocusedLabelColor = Color(0xFF8E8E93),
            disabledLabelColor = Color(0xFFC7C7CC),
            cursorColor = Color(0xFF1976D2)
        ),
        keyboardOptions = KeyboardOptions.Default
    )
}

@Composable
fun PasswordTextField(
    labelValue: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = labelValue) },
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF1C1C1E),
            unfocusedTextColor = Color(0xFF3A3A3C),
            disabledTextColor = Color(0xFF8E8E93),
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFD1D1D6),
            disabledBorderColor = Color(0xFFE5E5EA),
            focusedLabelColor = Color(0xFF1976D2),
            unfocusedLabelColor = Color(0xFF8E8E93),
            disabledLabelColor = Color(0xFFC7C7CC),
            cursorColor = Color(0xFF1976D2)
        ),
        trailingIcon = {
            Icon(
                imageVector = if (showPassword) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                },
                contentDescription = if (showPassword) {
                    "Hide password"
                } else {
                    "Show password"
                },
                tint = Color(0xFF8E8E93),
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
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1976D2),
                            Color(0xFF42A5F5)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .then(
                    if (!enabled) Modifier.alpha(0.5f) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 0.5.sp
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
