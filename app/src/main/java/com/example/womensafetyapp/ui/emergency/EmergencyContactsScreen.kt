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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    viewModel: EmergencyContactsViewModel = viewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${contacts.size}/10 contacts",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF3B30),
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
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Error loading contacts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            errorMessage,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { viewModel.fetchContacts() },
                            modifier = Modifier.padding(top = 16.dp)
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

    // Add/Edit Dialog
    if (showAddDialog) {
        AddEditContactDialog(
            contact = editingContact,
            onDismiss = {
                showAddDialog = false
                editingContact = null
            },
            onSave = { name, phone, relationship, isPrimary ->
                if (editingContact != null) {
                    viewModel.updateContact(
                        editingContact!!.id,
                        name, phone, relationship, isPrimary
                    )
                } else {
                    viewModel.addContact(name, phone, relationship, isPrimary)
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
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "No Emergency Contacts",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Add trusted contacts who will be notified during emergencies",
            fontSize = 16.sp,
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        contact.contactName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (contact.isPrimary == true) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "PRIMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Phone
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📞 ", fontSize = 16.sp)
                Text(contact.phoneNumber, fontSize = 16.sp, color = Color(0xFF555555))
            }

            // Relationship
            contact.relationship?.let { rel ->
                Spacer(modifier = Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👤 ", fontSize = 14.sp)
                    Text(rel, fontSize = 14.sp, color = Color(0xFF777777))
                }
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit", color = Color(0xFF2563EB))
                }
                Spacer(modifier = Modifier.width(10.dp))
                TextButton(onClick = { showDeleteDialog = true }) {
                    Text("Delete", color = Color(0xFFFF3B30))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Contact") },
            text = { Text("Are you sure you want to delete this emergency contact?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF3B30))
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
    onSave: (name: String, phone: String, relationship: String, isPrimary: Boolean) -> Unit
) {
    var contactName by remember { mutableStateOf(contact?.contactName ?: "") }
    var phoneNumber by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var relationship by remember { mutableStateOf(contact?.relationship ?: "") }
    var isPrimary by remember { mutableStateOf(contact?.isPrimary ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (contact != null) "Edit Contact" else "Add Emergency Contact",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Contact Name *") },
                    placeholder = { Text("e.g., Mom, John Doe") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Phone
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number *") },
                    placeholder = { Text("+919876543210") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Relationship
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (Optional)") },
                    placeholder = { Text("e.g., Mother, Friend") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Primary checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFF3B30)
                        )
                    )
                    Text("Set as Primary Contact")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (contactName.isNotBlank() && phoneNumber.isNotBlank()) {
                        onSave(contactName, phoneNumber, relationship, isPrimary)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3B30)
                )
            ) {
                Text(if (contact != null) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Data class for Emergency Contact
//data class EmergencyContact(
//    val id: Long,
//    val contactName: String,
//    val phoneNumber: String,
//    val relationship: String?,
//    val isPrimary: Boolean?
//)