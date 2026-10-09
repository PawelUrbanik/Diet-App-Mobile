package pl.pawel.diet_app_mobile.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import pl.pawel.diet_app_mobile.domain.model.MealReminder
import pl.pawel.diet_app_mobile.ui.components.mealCategoryColor
import pl.pawel.diet_app_mobile.ui.components.mealCategoryIcon

private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val reminders by viewModel.reminders.collectAsState()
    val canPostNotifications by viewModel.canPostNotifications.collectAsState()
    val exactAlarmsAllowed by viewModel.exactAlarmsAllowed.collectAsState()
    val message by viewModel.message.collectAsState()

    LifecycleResumeEffect(Unit) {
        viewModel.refreshPermissions()
        onPauseOrDispose { }
    }

    SettingsScreen(
        reminders = reminders,
        canPostNotifications = canPostNotifications,
        exactAlarmsAllowed = exactAlarmsAllowed,
        message = message,
        onBack = onBack,
        onEnabledChange = viewModel::setEnabled,
        onTimeChange = viewModel::setTime,
        onPermissionResult = { viewModel.refreshPermissions() },
        onSendTest = viewModel::sendTestNotification,
        onConsumeMessage = viewModel::consumeMessage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    reminders: List<MealReminder>,
    canPostNotifications: Boolean,
    exactAlarmsAllowed: Boolean,
    message: String?,
    onBack: () -> Unit,
    onEnabledChange: (String, Boolean) -> Unit,
    onTimeChange: (String, LocalTime) -> Unit,
    onPermissionResult: () -> Unit,
    onSendTest: () -> Unit,
    onConsumeMessage: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var editingTimeFor by remember { mutableStateOf<MealReminder?>(null) }
    // Typ posiłku, który użytkownik próbował włączyć przed udzieleniem zgody.
    var pendingEnable by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onPermissionResult()
        val mealType = pendingEnable
        pendingEnable = null
        if (granted && mealType != null) onEnabledChange(mealType, true)
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            onConsumeMessage()
        }
    }

    val anyEnabled = reminders.any { it.enabled }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Przypomnienia o posiłkach",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "O wybranej godzinie dostaniesz powiadomienie z posiłkiem zaplanowanym " +
                    "na dziś. Puste sloty i oznaczone „jem na mieście” są pomijane.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (anyEnabled && !canPostNotifications) {
                WarningCard(
                    text = "Powiadomienia są wyłączone dla aplikacji, więc przypomnienia nie będą widoczne.",
                    actionLabel = "Włącz powiadomienia",
                    onAction = {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
                        )
                    },
                )
            }

            Card {
                reminders.forEachIndexed { index, reminder ->
                    ReminderRow(
                        reminder = reminder,
                        onClick = { editingTimeFor = reminder },
                        onEnabledChange = { enabled ->
                            if (enabled && !canPostNotifications) {
                                pendingEnable = reminder.mealType
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onEnabledChange(reminder.mealType, enabled)
                            }
                        },
                    )
                    if (index < reminders.lastIndex) HorizontalDivider()
                }
            }

            if (anyEnabled && !exactAlarmsAllowed) {
                WarningCard(
                    text = "Bez zgody na dokładne alarmy system może dostarczać przypomnienia " +
                        "z kilkuminutowym opóźnieniem.",
                    actionLabel = "Zezwól na dokładne godziny",
                    onAction = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                Uri.parse("package:${context.packageName}"),
                            ),
                        )
                    },
                )
            }

            OutlinedButton(
                onClick = onSendTest,
                enabled = canPostNotifications,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Text("Wyślij powiadomienie testowe", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    editingTimeFor?.let { reminder ->
        ReminderTimeDialog(
            reminder = reminder,
            onConfirm = { time ->
                onTimeChange(reminder.mealType, time)
                editingTimeFor = null
            },
            onDismiss = { editingTimeFor = null },
        )
    }
}

@Composable
private fun ReminderRow(
    reminder: MealReminder,
    onClick: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = mealCategoryIcon(reminder.mealType),
                contentDescription = null,
                tint = mealCategoryColor(reminder.mealType),
            )
        },
        headlineContent = { Text(reminder.mealType) },
        supportingContent = {
            Text(
                text = reminder.time.format(TIME_FORMATTER),
                style = MaterialTheme.typography.titleSmall,
                color = if (reminder.enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        },
        trailingContent = {
            Switch(checked = reminder.enabled, onCheckedChange = onEnabledChange)
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeDialog(
    reminder: MealReminder,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = reminder.time.hour,
        initialMinute = reminder.time.minute,
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(reminder.mealType) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@Composable
private fun WarningCard(
    text: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 4.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}
