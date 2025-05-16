/**
 * User Management JavaScript
 *
 * This file contains all the JavaScript functionality needed for managing users:
 * - Loading users from the database
 * - Displaying users in the table
 * - Editing user information
 * - Deleting users
 */

document.addEventListener('DOMContentLoaded', function() {
    // Load users when page loads
    loadUsers();

    // Set up event listeners for forms
    document.getElementById('editUserForm').addEventListener('submit', handleEditUser);
    document.getElementById('deleteUserForm').addEventListener('submit', handleDeleteUser);
});

/**
 * Get CSRF token from meta tag (for Spring Security)
 * @returns {string} The CSRF token
 */
function getCsrfToken() {
    const metaTag = document.querySelector('meta[name="_csrf"]');
    return metaTag ? metaTag.getAttribute('content') : '';
}

/**
 * Get CSRF header name from meta tag (for Spring Security)
 * @returns {string} The CSRF header name
 */
function getCsrfHeaderName() {
    const metaTag = document.querySelector('meta[name="_csrf_header"]');
    return metaTag ? metaTag.getAttribute('content') : 'X-CSRF-TOKEN';
}

/**
 * Create request headers including CSRF token if available
 * @param {boolean} includeContentType - Whether to include Content-Type header
 * @returns {Object} Headers object for fetch request
 */
function createHeaders(includeContentType = true) {
    const headers = {};

    if (includeContentType) {
        headers['Content-Type'] = 'application/json';
    }

    const csrfHeader = getCsrfHeaderName();
    const csrfToken = getCsrfToken();

    if (csrfHeader && csrfToken) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}

/**
 * Load all users with role USER from the database and display them in the table
 */
function loadUsers() {
    fetch('/api/users', {
        method: 'GET',
        headers: createHeaders(false)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(users => {
            displayUsers(users);
        })
        .catch(error => {
            console.error('Error fetching users:', error);
            showToast('Error loading users. Please try again later.', 'error');
        });
}

/**
 * Display users in the table
 * @param {Array} users - Array of user objects
 */
function displayUsers(users) {
    const tableBody = document.getElementById('usersTableBody');
    tableBody.innerHTML = '';

    if (users.length === 0) {
        // Show a message if no users found
        const row = document.createElement('tr');
        row.innerHTML = `
            <td colspan="3" class="text-center">No users found</td>
        `;
        tableBody.appendChild(row);
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');
        row.dataset.userId = user.id;

        // Create username display - use first and last name if available, otherwise username
        const displayName = (user.firstName && user.lastName)
            ? `${user.firstName} ${user.lastName}`
            : user.username;

        row.innerHTML = `
            <td>
                <div class="d-flex">
                    <div>
                        <img src="/assets/img/profile.png" alt="Avatar"/>
                    </div>
                    <div class="pl-3">
                        <h6 class="mb-0">${displayName}</h6>
                        <small>@${user.username}</small>
                    </div>
                </div>
            </td>
            <td>${user.email}</td>
            <td>
                <div class="dropdown">
                    <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                            <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>
                        </svg>
                    </button>
                    <div class="dropdown-menu">
                        <a class="dropdown-item view-user" href="javascript:void(0);" data-user-id="${user.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-fill" viewBox="0 0 16 16">
                                <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0"/>
                                <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8m8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7"/>
                            </svg> View Profile
                        </a>
                        <a class="dropdown-item edit-user" href="javascript:void(0);" data-bs-toggle="modal" data-bs-target="#editModal" data-user-id="${user.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="green" class="bi bi-pencil-square" viewBox="0 0 16 16">
                                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                                <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
                            </svg> Edit User
                        </a>
                        <a class="dropdown-item delete-user" href="javascript:void(0);" data-bs-toggle="modal" data-bs-target="#modalTop" data-user-id="${user.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red" class="bi bi-trash3" viewBox="0 0 16 16">
                                <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5M11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47M8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5"/>
                            </svg> Delete User
                        </a>
                    </div>
                </div>
            </td>
        `;

        tableBody.appendChild(row);
    });

    // Add event listeners for action buttons
    addActionButtonListeners();
}

/**
 * Add event listeners to the action buttons in each row
 */
function addActionButtonListeners() {
    // Edit user buttons
    document.querySelectorAll('.edit-user').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.dataset.userId;
            loadUserForEditing(userId);
        });
    });

    // Delete user buttons
    document.querySelectorAll('.delete-user').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.dataset.userId;
            document.getElementById('deleteUserId').value = userId;
        });
    });

    // View user profile buttons (if needed)
    document.querySelectorAll('.view-user').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.dataset.userId;
            viewUserProfile(userId);
        });
    });
}

/**
 * Load user data for editing
 * @param {string} userId - The ID of the user to edit
 */
function loadUserForEditing(userId) {
    fetch(`/api/users/${userId}`, {
        method: 'GET',
        headers: createHeaders(false)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(user => {
            // Populate the edit form with user data
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editUsername').value = user.username;
            document.getElementById('editEmail').value = user.email;
            document.getElementById('editFirstName').value = user.firstName || '';
            document.getElementById('editLastName').value = user.lastName || '';
            document.getElementById('editPassword').value = ''; // Clear password field
        })
        .catch(error => {
            console.error('Error fetching user details:', error);
            showToast('Error loading user details. Please try again later.', 'error');
        });
}

/**
 * Handle submitting the edit user form
 * @param {Event} event - The form submit event
 */
function handleEditUser(event) {
    event.preventDefault();

    const userId = document.getElementById('editUserId').value;
    const username = document.getElementById('editUsername').value;
    const email = document.getElementById('editEmail').value;
    const firstName = document.getElementById('editFirstName').value;
    const lastName = document.getElementById('editLastName').value;
    const password = document.getElementById('editPassword').value;

    // Validate required fields
    if (!username || !email) {
        showToast('Username and email are required fields.', 'error');
        return;
    }

    const userData = {
        id: userId,
        username: username,
        email: email,
        firstName: firstName,
        lastName: lastName
    };

    // Only include password if it was entered
    if (password && password.trim() !== '') {
        userData.password = password;
    }

    fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: createHeaders(),
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(() => {
            // Close the modal
            const editModal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
            editModal.hide();

            // Reload users to refresh the table
            loadUsers();

            // Show success message
            showToast('User updated successfully!', 'success');
        })
        .catch(error => {
            console.error('Error updating user:', error);
            showToast('Error updating user. Please try again later.', 'error');
        });
}

/**
 * Handle submitting the delete user form
 * @param {Event} event - The form submit event
 */
function handleDeleteUser(event) {
    event.preventDefault();

    const userId = document.getElementById('deleteUserId').value;

    fetch(`/api/users/${userId}`, {
        method: 'DELETE',
        headers: createHeaders(false)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            // Close the modal
            const deleteModal = bootstrap.Modal.getInstance(document.getElementById('modalTop'));
            deleteModal.hide();

            // Reload users to refresh the table
            loadUsers();

            // Show success message
            showToast('User deleted successfully!', 'success');
        })
        .catch(error => {
            console.error('Error deleting user:', error);
            showToast('Error deleting user. Please try again later.', 'error');
        });
}

/**
 * View user profile (if needed - you can customize this function)
 * @param {string} userId - The ID of the user to view
 *//**
 * View user profile - Shows user details in a modal
 * @param {string} userId - The ID of the user to view
 */
function viewUserProfile(userId) {
    fetch(`/api/users/${userId}`, {
        method: 'GET',
        headers: createHeaders(false)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(user => {
            // Populate the view modal with user data
            const displayName = (user.firstName && user.lastName)
                ? `${user.firstName} ${user.lastName}`
                : user.username;

            document.getElementById('viewUserName').textContent = displayName;
            document.getElementById('viewUserUsername').textContent = `@${user.username}`;
            document.getElementById('viewUserEmail').textContent = user.email || 'Not provided';
            document.getElementById('viewUserFirstName').textContent = user.firstName || 'Not provided';
            document.getElementById('viewUserLastName').textContent = user.lastName || 'Not provided';

            // Show the modal
            const viewModal = new bootstrap.Modal(document.getElementById('viewUserModal'));
            viewModal.show();
        })
        .catch(error => {
            console.error('Error fetching user details:', error);
            showToast('Error loading user profile. Please try again later.', 'error');
        });
}

/**
 * Show a toast notification to the user
 * @param {string} message - The message to display
 * @param {string} type - The type of toast (success, error, info, warning)
 */
function showToast(message, type = 'info') {
    // Create toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }

    // Create the toast element
    const toastId = 'toast-' + Date.now();
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'}`;
    toast.id = toastId;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    toastContainer.appendChild(toast);

    // Initialize and show the toast
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    // Remove toast after it's hidden
    toast.addEventListener('hidden.bs.toast', function() {
        toast.remove();
    });
}