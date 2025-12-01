package com.example.womensafetyapp.ui.emergency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ===========================================================
// DATA MODELS
// ===========================================================

data class EmergencyContact(
    val id: Long,
    val contactName: String,
    val phoneNumber: String,
    val relationship: String?,
    val isPrimary: Boolean?
)

data class EmergencyContactDTO(
    val contactName: String,
    val phoneNumber: String,
    val relationship: String?,
    val isPrimary: Boolean
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

    // =======================================================
    // Get Token Helper
    // =======================================================

    private fun getToken(): String {
        val token = appContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("token", "") ?: ""

        return "Bearer $token"
    }

    // =======================================================
    // FETCH CONTACTS
    // =======================================================

    fun fetchContacts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val response = apiClient.api.getEmergencyContacts(getToken())
                _contacts.value = response

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch contacts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =======================================================
    // ADD CONTACT
    // =======================================================

    fun addContact(
        name: String,
        phone: String,
        relationship: String,
        isPrimary: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val contactData = EmergencyContactDTO(
                    contactName = name.trim(),
                    phoneNumber = phone.trim(),
                    relationship = relationship.trim().ifEmpty { null },
                    isPrimary = isPrimary
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

    // =======================================================
    // UPDATE CONTACT
    // =======================================================

    fun updateContact(
        contactId: Long,
        name: String,
        phone: String,
        relationship: String,
        isPrimary: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val contactData = EmergencyContactDTO(
                    contactName = name.trim(),
                    phoneNumber = phone.trim(),
                    relationship = relationship.trim().ifEmpty { null },
                    isPrimary = isPrimary
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

    // =======================================================
    // DELETE CONTACT
    // =======================================================

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
