package com.example.beaceful.ui.screens.authen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.beaceful.R

@Composable
fun SignUpScreen() {
    var usernameInput by rememberSaveable { mutableStateOf("") }
    var signUpInput by rememberSaveable { mutableStateOf("") }
    var passwordInput by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val icon = if (passwordVisibility)
        Icons.Default.Visibility
    else
        Icons.Default.VisibilityOff

    Column(
        Modifier.padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Image(
            painterResource(R.drawable.logo_with_name),
            contentDescription = null,
            modifier = Modifier.size(210.dp)
        )
        Text(
            "Đăng ký",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {}, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(Icons.Default.Mail, contentDescription = null)
                Text("Với email")
            }
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Facebook, contentDescription = null)
            }
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Pin, contentDescription = null)
            }
        }
        HorizontalDivider(Modifier.fillMaxWidth())
        Text("Hoặc",style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            placeholder = { Text("Tên người dùng",style = MaterialTheme.typography.bodyMedium) },
            shape = RoundedCornerShape(60.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            )
        )
        OutlinedTextField(
            value = signUpInput,
            onValueChange = { signUpInput = it },
            placeholder = { Text("Số di động hoặc email",style = MaterialTheme.typography.bodyMedium) },
            shape = RoundedCornerShape(60.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            )
        )
        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            placeholder = { Text("Mật khẩu",style = MaterialTheme.typography.bodyMedium) },
            shape = RoundedCornerShape(60.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            ),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )
        Box(Modifier.fillMaxHeight().padding(bottom = 12.dp)) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Đăng ký")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text("Đã có tài khoản?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = {}) {
                    Text("Đăng nhập", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}