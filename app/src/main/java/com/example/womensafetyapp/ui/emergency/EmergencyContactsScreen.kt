package com.example.womensafetyapp.ui.emergency

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context

// ===========================================================
// DATA MODELS
// ===========================================================

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

// ===========================================================
// VIEWMODEL
// ===========================================================

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
        val token = appContext.getSharedPreferences("user_token", Context.MODE_PRIVATE)
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
                        id = it.id,
                        contactName = it.contactName,
                        phoneNumber = it.phoneNumber,
                        relationship = it.relationship
                    )
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch contacts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addContact(
        name: String,
        phone: String,
        relationship: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val contactData = EmergencyContactDTO(
                    contactName = name.trim(),
                    phoneNumber = phone.trim(),
                    relationship = relationship.trim().ifEmpty { null }
                )

                apiClient.api.addEmergencyContact(
                    contact = contactData,
                    token = getToken()
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

    fun updateContact(
        contactId: Long,
        name: String,
        phone: String,
        relationship: String
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val contactData = EmergencyContactDTO(
                    contactName = name.trim(),
                    phoneNumber = phone.trim(),
                    relationship = relationship.trim().ifEmpty { null }
                )

                apiClient.api.updateEmergencyContact(
                    contactId = contactId,
                    contact = contactData,
                    token = getToken()
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

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.deleteEmergencyContact(
                    contactId = contactId,
                    token = getToken()
                )

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

// ===========================================================
// UI SCREEN
// ===========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
<<<<<<< HEAD
fun EmergencyContactsScreen() {
=======
fun EmergencyContactsScreen(
    onBack: () -> Unit = {}
) {
>>>>>>> a9a0289 (Implemented smart alert)
    val context = LocalContext.current
    val viewModel: EmergencyContactsViewModel = remember {
        EmergencyContactsViewModel(context)
    }
<<<<<<< HEAD

=======
>>>>>>> a9a0289 (Implemented smart alert)
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Emergency Contacts",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${contacts.size}/10 contacts",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
<<<<<<< HEAD
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF3B30),
=======
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3B82F6),
>>>>>>> a9a0289 (Implemented smart alert)
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (contacts.size < 10) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFFFF3B30)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Contact",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFDF2F2))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFF3B30)
                    )
                }

                errorMessage.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "⚠️",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Error loading contacts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            errorMessage,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchContacts() },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF3B30)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }

                contacts.isEmpty() -> {
                    EmptyContactsState()
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(contacts) { contact ->
                            EmergencyContactCard(
                                contact = contact,
                                onEdit = {
                                    editingContact = contact
                                    showAddDialog = true
                                },
                                onDelete = { viewModel.deleteContact(contact.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Success message snackbar
    if (successMessage.isNotEmpty()) {
        LaunchedEffect(successMessage) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        AddEditContactDialog(
            contact = editingContact,
            onDismiss = {
                showAddDialog = false
                editingContact = null
            },
            onSave = { name, phone, relationship ->
                if (editingContact != null) {
                    viewModel.updateContact(
                        editingContact!!.id,
                        name, phone, relationship
                    )
                } else {
                    viewModel.addContact(name, phone, relationship)
                }
                showAddDialog = false
                editingContact = null
            }
        )
    }
}

@Composable
fun EmptyContactsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "📱",
            fontSize = 80.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No Emergency Contacts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Add trusted contacts who will be notified during emergencies",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Name
            Text(
                contact.contactName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    contact.phoneNumber,
                    fontSize = 15.sp,
                    color = Color(0xFF555555)
                )
            }

            // Relationship
            contact.relationship?.let { rel ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        rel,
                        fontSize = 14.sp,
                        color = Color(0xFF777777)
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF2563EB)
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF3B30)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Delete Contact?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete ${contact.contactName} from your emergency contacts?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3B30)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactDialog(
    contact: EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relationship: String) -> Unit
) {
    var contactName by remember { mutableStateOf(contact?.contactName ?: "") }
    var phoneNumber by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var relationship by remember { mutableStateOf(contact?.relationship ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (contact != null) "Edit Contact" else "Add Emergency Contact",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Contact Name *") },
                    placeholder = { Text("e.g., Mom, John Doe") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF3B30),
                        focusedLabelColor = Color(0xFFFF3B30)
                    )
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number *") },
                    placeholder = { Text("+919876543210") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF3B30),
                        focusedLabelColor = Color(0xFFFF3B30)
                    )
                )

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (Optional)") },
                    placeholder = { Text("e.g., Mother, Friend") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF3B30),
                        focusedLabelColor = Color(0xFFFF3B30)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (contactName.isNotBlank() && phoneNumber.isNotBlank()) {
                        onSave(contactName, phoneNumber, relationship)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3B30)
                ),
                enabled = contactName.isNotBlank() && phoneNumber.isNotBlank()
            ) {
                Text(if (contact != null) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF666666))
            }
        }
    )
}