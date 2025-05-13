// src/admin/UserManagement.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import EditUserModal from './EditUserModal';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [editingUser, setEditingUser] = useState(null);
  const [error, setError] = useState('');

  const token = localStorage.getItem('token');

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/admin/users', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users');
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleUserUpdate = (updatedUser) => {
    const updatedList = users.map((u) => (u.id === updatedUser.id ? updatedUser : u));
    setUsers(updatedList);
    setEditingUser(null);
  };

  return (
    <div>
      <h3>User Management</h3>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
        <thead>
          <tr>
            <th>ID</th><th>Username</th><th>Full Name</th><th>Email</th>
            <th>Department</th><th>Roles</th><th>Active</th><th>Action</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.fullName}</td>
              <td>{u.email}</td>
              <td>{u.department}</td>
              <td>{u.roles.join(', ')}</td>
              <td>{u.active ? 'Yes' : 'No'}</td>
              <td>
                <button onClick={() => setEditingUser(u)}>Edit</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {editingUser && (
        <EditUserModal
          user={editingUser}
          onClose={() => setEditingUser(null)}
          onUpdate={handleUserUpdate}
        />
      )}
    </div>
  );
};

export default UserManagement;
