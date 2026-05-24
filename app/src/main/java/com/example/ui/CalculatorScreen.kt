package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isAdvancedMode by viewModel.isAdvancedMode.collectAsState()
    val isHistoryOpen by viewModel.isHistoryOpen.collectAsState()
    val history = viewModel.history.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SMART CALC", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onAction(CalculatorAction.ToggleHistory) }) {
                        Icon(Icons.Filled.History, contentDescription = "Riwayat")
                    }
                    IconButton(onClick = { viewModel.onAction(CalculatorAction.ToggleAdvanced) }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Mode Lanjut")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Display Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = expression,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines = 3,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }

                // Advanced Controls
                AnimatedVisibility(
                    visible = isAdvancedMode,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdvancedButton("sin", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("sin")) }
                        AdvancedButton("cos", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("cos")) }
                        AdvancedButton("tan", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("tan")) }
                        AdvancedButton("ln", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("ln")) }
                        AdvancedButton("log", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("log")) }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdvancedButton("sqrt", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Function("sqrt")) }
                        AdvancedButton("^", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("^")) }
                        AdvancedButton("π", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Constant("π")) }
                        AdvancedButton("e", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Constant("e")) }
                        AdvancedButton("MOD", Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("%")) }
                    }
                }
                
                // Keypad
                val keypadSpacing = 12.dp
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                    ) {
                        ActionButton("AC", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Clear) }
                        ActionButton("(", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Parenthesis("(")) }
                        ActionButton(")", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Parenthesis(")")) }
                        ActionButton("÷", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("÷")) }
                    }
                    Spacer(modifier = Modifier.height(keypadSpacing))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                    ) {
                        NumberButton("7", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(7)) }
                        NumberButton("8", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(8)) }
                        NumberButton("9", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(9)) }
                        ActionButton("×", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("×")) }
                    }
                    Spacer(modifier = Modifier.height(keypadSpacing))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                    ) {
                        NumberButton("4", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(4)) }
                        NumberButton("5", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(5)) }
                        NumberButton("6", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(6)) }
                        ActionButton("-", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("-")) }
                    }
                    Spacer(modifier = Modifier.height(keypadSpacing))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                    ) {
                        NumberButton("1", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(1)) }
                        NumberButton("2", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(2)) }
                        NumberButton("3", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(3)) }
                        ActionButton("+", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operator("+")) }
                    }
                    Spacer(modifier = Modifier.height(keypadSpacing))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                    ) {
                        NumberButton("0", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(0)) }
                        NumberButton(".", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Decimal) }
                        NumberButton("⌫", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Delete) }
                        ActionButton("=", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Calculate) }
                    }
                }
            }

            // History Bottom Sheet Wrapper (simplified custom implementation)
            if (isHistoryOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                        .clickable { viewModel.onAction(CalculatorAction.ToggleHistory) }
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Riwayat",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = { viewModel.onAction(CalculatorAction.ClearHistory) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Hapus Riwayat", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (history.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Belum ada riwayat", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn {
                                    items(history) { item ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.onAction(CalculatorAction.LoadHistory(item)) }
                                                .padding(vertical = 12.dp)
                                        ) {
                                            Text(item.expression, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text(
                                                "=${item.result}",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
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
fun RowScope.NumberButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 28.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun RowScope.ActionButton(text: String, containerColor: androidx.compose.ui.graphics.Color, contentColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 28.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AdvancedButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
