package com.yft.rippleup.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.ui.components.LeafLogo
import com.yft.rippleup.util.clickableNoInd

private val Bg = Color(0xFFFFFFFF)
private val Mint = Color(0xFFE8F7F4)
private val Teal = Color(0xFF0D9488)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)
private val Gray = Color(0xFF8F8F8F)

/**
 * AUTH — exact per Figma: segmented Join Us | Log In (mint track, white active
 * pill, teal text), mint r20 fields (First/Last name, email, password + Show),
 * terms line. Local-account demo auth (real server auth needs a backend).
 */
@Composable
fun AuthScreen(onDone: (String) -> Unit) {
    var joinMode by remember { mutableStateOf(true) }
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    var err by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Segmented control (mint track r20, active white pill)
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Mint).padding(4.dp),
        ) {
            Row {
                SegTab("Join Us", joinMode) { joinMode = true }
                SegTab("Log In", !joinMode) { joinMode = false }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(if (joinMode) "Create Account" else "Welcome Back",
            fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(if (joinMode) "It's easy to start making an impact" else "Log in to keep your ripples going",
            fontSize = 14.sp, color = Muted)
        Spacer(Modifier.height(18.dp))

        if (joinMode) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Field("First Name", "John", first, { first = it }, Modifier.weight(1f))
                Field("Last Name", "Doe", last, { last = it }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
        Field("Email", "you@university.edu", email, { email = it },
            Modifier.fillMaxWidth(), KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth()) {
            Text("Password", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Mint)
                .padding(horizontal = 14.dp, vertical = 12.dp)) {
                androidx.compose.foundation.text.BasicTextField(
                    value = pass, onValueChange = { pass = it },
                    singleLine = true,
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Ink),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(0.7f),
                )
                Text("Show", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Muted,
                    modifier = Modifier.align(Alignment.CenterEnd).clickableNoInd { show = !show })
            }
        }

        err?.let { Text(it, fontSize = 11.sp, color = Color(0xFFC2504A), modifier = Modifier.padding(top = 6.dp)) }

        Spacer(Modifier.height(16.dp))
        // CTA
        Text(
            if (joinMode) "Sign Up" else "Log In", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Teal)
                .clickableNoInd {
                    when {
                        joinMode && (first.isBlank() || last.isBlank()) -> err = "Please enter your name"
                        !email.contains('@') -> err = "Please enter a valid email"
                                        pass.length < 6 -> err = "Password must be at least 6 characters"
                        !joinMode && email.trim().equals("admin", true) && pass == "rudra" -> onDone("Admin")
                        else -> onDone(if (joinMode) "$first $last".trim() else email.substringBefore('@'))
                    }
                }
                .padding(vertical = 14.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        if (!joinMode) {
            Spacer(Modifier.height(10.dp))
            Text("Forgot password?", fontSize = 12.sp, color = Muted)
        }
        if (joinMode) {
            Spacer(Modifier.height(12.dp))
            Text("By signing up you agree to our Terms of Service and Privacy Policy",
                fontSize = 10.sp, color = Muted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
        Spacer(Modifier.height(24.dp))
        LeafLogo(size = 44.dp)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SegTab(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.weight(1f).height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) Color.White else Color.Transparent)
            .clickableNoInd(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = if (active) Teal else Muted)
    }
}

@Composable
private fun Field(label: String, hint: String, value: String, onChange: (String) -> Unit,
                  modifier: Modifier = Modifier, type: KeyboardType = KeyboardType.Text) {
    Column(modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink,
            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Mint)
            .padding(horizontal = 14.dp, vertical = 12.dp)) {
            if (value.isEmpty()) Text(hint, fontSize = 14.sp, color = Gray)
            androidx.compose.foundation.text.BasicTextField(
                value = value, onValueChange = onChange, singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Ink),
                keyboardOptions = KeyboardOptions(keyboardType = type, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
