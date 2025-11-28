document.addEventListener('DOMContentLoaded', () => {
    const tbody = document.getElementById('users-table-body');
    if (!tbody) {
        console.error('Не найден tbody с id="users-table-body"');
        return;
    }

    const addUserBtn = document.getElementById('add-user-btn');
    const addUserModalEl = document.getElementById('addUserModal');
    const addUserForm = document.getElementById('add-user-form');
    const addSaveBtn = document.getElementById('add-save-btn');

    const editSaveBtn = document.getElementById('edit-save-btn');

    function renderRow(user) {
        const tr = document.createElement('tr');
        tr.dataset.userId = user.id;
        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>
                ${(user.roles || [])
            .map(r => `<span class="badge text-bg-secondary me-1">${r.name}</span>`)
            .join('')}
            </td>
            <td>
                <button class="btn btn-sm btn-primary me-2 js-edit-user" data-user-id="${user.id}">
                    Edit
                </button>
                <button class="btn btn-sm btn-outline-danger js-delete-user" data-user-id="${user.id}">
                    Delete
                </button>
            </td>
        `;
        return tr;
    }

    function loadUsers() {
        fetch('/api/users')
            .then(response => response.json())
            .then(users => {
                tbody.innerHTML = '';
                users.forEach(user => {
                    tbody.appendChild(renderRow(user));
                });
            })
            .catch(error => {
                console.error('Ошибка при загрузке пользователей:', error);
            });
    }

    loadUsers();

    tbody.addEventListener('click', event => {

        const deleteButton = event.target.closest('.js-delete-user');
        if (deleteButton) {
            const userId = deleteButton.dataset.userId;
            const row = deleteButton.closest('tr');

            if (!confirm(`Delete user with id=${userId}?`)) {
                return;
            }

            fetch(`/api/users/${userId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Не удалось удалить пользователя');
                    }
                    row.remove();
                })
                .catch(error => {
                    console.error('Ошибка при удалении пользователя:', error);
                    alert('Ошибка при удалении пользователя');
                });

            return;
        }

        const editButton = event.target.closest('.js-edit-user');
        if (editButton) {
            const userId = editButton.dataset.userId;

            fetch(`/api/users/${userId}`)
                .then(response => response.json())
                .then(user => {
                    document.getElementById('edit-id').value = user.id;
                    document.getElementById('edit-firstName').value = user.firstName || '';
                    document.getElementById('edit-lastName').value = user.lastName || '';
                    document.getElementById('edit-age').value = user.age ?? '';
                    document.getElementById('edit-email').value = user.email || '';

                    const modalElement = document.getElementById('editUserModal');
                    const modal = new bootstrap.Modal(modalElement);
                    modal.show();
                })
                .catch(error => {
                    console.error('Ошибка при загрузке пользователя:', error);
                    alert('Ошибка при загрузке пользователя');
                });
        }
    });

    if (editSaveBtn) {
        editSaveBtn.addEventListener('click', () => {
            const id = document.getElementById('edit-id').value;
            const firstName = document.getElementById('edit-firstName').value.trim();
            const lastName  = document.getElementById('edit-lastName').value.trim();
            const ageValue  = document.getElementById('edit-age').value;
            const email     = document.getElementById('edit-email').value.trim();

            const age = ageValue === '' ? null : Number(ageValue);

            const payload = {
                firstName,
                lastName,
                age,
                email
            };

            fetch(`/api/users/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
                .then(async response => {
                    if (response.ok) {
                        return response.json();
                    }

                    const errText = await response.text();
                    throw new Error(errText || 'Не удалось обновить пользователя');
                })
                .then(updatedUser => {
                    const row = tbody.querySelector(`tr[data-user-id="${updatedUser.id}"]`);
                    if (row) {
                        const cells = row.querySelectorAll('td');
                        cells[1].textContent = updatedUser.firstName;
                        cells[2].textContent = updatedUser.lastName;
                        cells[3].textContent = updatedUser.age;
                        cells[4].textContent = updatedUser.email;
                        cells[5].innerHTML = (updatedUser.roles || [])
                            .map(r => `<span class="badge text-bg-secondary me-1">${r.name}</span>`)
                            .join('');
                    }

                    const modalElement = document.getElementById('editUserModal');
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    if (modal) {
                        modal.hide();
                    }
                })
                .catch(error => {
                    console.error('Ошибка при обновлении пользователя:', error);
                    alert(error.message || 'Ошибка при сохранении пользователя');
                });
        });
    }

    if (addUserBtn && addUserModalEl && addUserForm && addSaveBtn) {
        const addModal = new bootstrap.Modal(addUserModalEl);

        addUserBtn.addEventListener('click', () => {
            addUserForm.reset();
            addModal.show();
        });

        addSaveBtn.addEventListener('click', () => {
            const formData = new FormData(addUserForm);

            const firstName = (formData.get('firstName') || '').trim();
            const lastName  = (formData.get('lastName')  || '').trim();
            const ageValue  = (formData.get('age')       || '').trim();
            const email     = (formData.get('email')     || '').trim();
            const password  = (formData.get('password')  || '').trim();
            const roleIds   = formData.getAll('roleIds').map(Number);

            if (roleIds.length === 0) {
                alert('Выберите хотя бы одну роль');
                const sel = document.getElementById('add-roleIds');
                if (sel) sel.focus();
                return;
            }

            const age = ageValue === '' ? null : Number(ageValue);

            const payload = {
                firstName,
                lastName,
                age,
                email,
                password,
                roleIds
            };

            fetch('/api/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
                .then(async response => {
                    if (response.ok) {
                        return response.json();
                    }

                    const errText = await response.text();
                    throw new Error(errText || 'Не удалось создать пользователя');
                })
                .then(() => {
                    addModal.hide();
                    loadUsers();
                })
                .catch(error => {
                    console.error('Ошибка при создании пользователя:', error);
                    alert(error.message || 'Ошибка при создании пользователя');
                });
        });
    }
});