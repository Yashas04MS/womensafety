package com.example.womensafetyapp.ui.emergency

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------- DATA MODELS --------------------

data class EmergencyContact(
    val id: Long,
    val contactName: String,
    val phoneNumber: String,
    val relationship: String?
)

data class EmergencyContactDTO(
    val contactName: String,
    val phoneNumber: String,
    val relationship: String?
)

// -------------------- VIEWMODEL --------------------

class EmergencyContactsViewModel(
    private val appContext: Context
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

    private fun getToken(): String {
        val token = appContext
            .getSharedPreferences("user_token", Context.MODE_PRIVATE)
            .getString("jwt", "") ?: ""
        return "Bearer $token"
    }

    fun fetchContacts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val response = apiClient.api.getEmergencyContacts(getToken())
                _contacts.value = response.map {
                    EmergencyContact(
                        it.id,
                        it.contactName,
                        it.phoneNumber,
                        it.relationship
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch contacts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addContact(name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.addEmergencyContact(
                    EmergencyContactDTO(
                        name.trim(),
                        phone.trim(),
                        relationship.trim().ifEmpty { null }
                    ),
                    getToken()
                )

                _successMessage.value = "Contact added successfully!"
                fetchContacts()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to add contact"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateContact(id: Long, name: String, phone: String, relationship: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.updateEmergencyContact(
                    id,
                    EmergencyContactDTO(
                        name.trim(),
                        phone.trim(),
                        relationship.trim().ifEmpty { null }
                    ),
                    getToken()
                )

                _successMessage.value = "Contact updated successfully!"
                fetchContacts()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to update contact"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.deleteEmergencyContact(id, getToken())
                _successMessage.value = "Contact deleted successfully!"
                fetchContacts()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to delete contact"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
}

// -------------------- UI --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { EmergencyContactsViewModel(context) }

    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchContacts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Emergency Contacts", fontWeight = FontWeight.Bold)
                        Text("${contacts.size}/10 contacts", fontSize = 13.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3B82F6),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (contacts.size < 10) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color(0xFFFF3B30)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDF2F2))
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage.isNotEmpty() -> Text(errorMessage, modifier = Modifier.align(Alignment.Center))
                contacts.isEmpty() -> EmptyContactsState()
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(contacts) {
                        EmergencyContactCard(
                            it,
                            onEdit = {
                                editingContact = it
                                showDialog = true
                            },
                            onDelete = { viewModel.deleteContact(it.id) }
                        )
                    }
                }
            }
        }
    }

    if (successMessage.isNotEmpty()) {
        LaunchedEffect(successMessage) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    if (showDialog) {
        AddEditContactDialog(
            editingContact,
            onDismiss = {
                showDialog = false
                editingContact = null
            },
            onSave = { n, p, r ->
                if (editingContact == null)
                    viewModel.addContact(n, p, r)
                else
                    viewModel.updateContact(editingContact!!.id, n, p, r)

                showDialog = false
                editingContact = null
            }
        )
    }
}

// -------------------- SUPPORT COMPOSABLES --------------------

@Composable
fun EmptyContactsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📱", fontSize = 80.sp)
        Text("No Emergency Contacts", fontWeight = FontWeight.Bold)
        Text(
            "Add trusted contacts for emergencies",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(contact.contactName, fontWeight = FontWeight.Bold)
            Text(contact.phoneNumber)
            contact.relationship?.let { Text(it) }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactDialog(
    contact: EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(contact?.contactName ?: "") }
    var phone by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var relation by remember { mutableStateOf(contact?.relationship ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (contact == null) "Add Contact" else "Edit Contact") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })
                OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                OutlinedTextField(relation, { relation = it }, label = { Text("Relationship") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, phone, relation) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
