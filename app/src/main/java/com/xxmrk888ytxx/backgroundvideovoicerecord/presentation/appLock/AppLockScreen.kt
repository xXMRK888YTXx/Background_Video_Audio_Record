package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.appLock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AppLockScreen(
    isBiometricEnabled: Boolean,
    onVerifyPin: suspend (String) -> Boolean,
    onUnlockSuccess: () -> Unit,
    onTriggerBiometric: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (isBiometricEnabled) {
            delay(300)
            onTriggerBiometric()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header & Lock Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.5.dp, Color(0xFF6366F1), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Lock",
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.enter_pin_code),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = if (isError) stringResource(R.string.wrong_pin_try_again) else stringResource(R.string.to_access_app),
                    fontSize = 14.sp,
                    color = if (isError) Color(0xFFEF4444) else Color(0xFF94A3B8)
                )

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                ) {
                    repeat(4) { index ->
                        val isFilled = index < enteredPin.length
                        val dotColor = when {
                            isError -> Color(0xFFEF4444)
                            isFilled -> Color(0xFF818CF8)
                            else -> Color.Transparent
                        }
                        val borderColor = when {
                            isError -> Color(0xFFEF4444)
                            isFilled -> Color(0xFF818CF8)
                            else -> Color(0xFF475569)
                        }

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .border(2.dp, borderColor, CircleShape)
                        )
                    }
                }
            }

            // Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("bio", "0", "del")
                )

                for (row in keypadRows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (key in row) {
                            when (key) {
                                "bio" -> {
                                    if (isBiometricEnabled) {
                                        KeypadButton(
                                            onClick = onTriggerBiometric,
                                            content = {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_fingerprint),
                                                    contentDescription = "Biometrics",
                                                    tint = Color(0xFFA5B4FC),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(72.dp))
                                    }
                                }
                                "del" -> {
                                    KeypadButton(
                                        onClick = {
                                            if (enteredPin.isNotEmpty() && !isChecking) {
                                                enteredPin = enteredPin.dropLast(1)
                                                isError = false
                                            }
                                        },
                                        content = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_backspace),
                                                contentDescription = "Delete",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    )
                                }
                                else -> {
                                    KeypadButton(
                                        onClick = {
                                            if (enteredPin.length < 4 && !isChecking) {
                                                isError = false
                                                val newPin = enteredPin + key
                                                enteredPin = newPin

                                                if (newPin.length == 4) {
                                                    isChecking = true
                                                    scope.launch {
                                                        val verified = onVerifyPin(newPin)
                                                        if (verified) {
                                                            onUnlockSuccess()
                                                        } else {
                                                            isError = true
                                                            shakeOffset.animateTo(
                                                                targetValue = 0f,
                                                                animationSpec = keyframes {
                                                                    durationMillis = 400
                                                                    -20f at 50
                                                                    20f at 100
                                                                    -15f at 150
                                                                    15f at 200
                                                                    -10f at 250
                                                                    10f at 300
                                                                    0f at 400
                                                                }
                                                            )
                                                            delay(200)
                                                            enteredPin = ""
                                                            isChecking = false
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        content = {
                                            Text(
                                                text = key,
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 36.dp, color = Color(0xFF818CF8)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
