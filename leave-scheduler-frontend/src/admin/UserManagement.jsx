import React, { useEffect, useState } from 'react';
import axios from 'axios';
import EditUserModal from './EditUserModal';
import './UserManagement.css'; // 👈 Create and use this CSS for background

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
    <div className="user-page">
      <div className="user-card">
        <h2 className="user-title">👥 User Management</h2>
        {error && <p className="error-message">{error}</p>}

        <div className="table-container">
          <table className="user-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Department</th>
                <th>Roles</th>
                <th>Active</th>
                <th>Action</th>
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
                  <td>
                    <span
                      style={{
                        padding: '4px 8px',
                        borderRadius: '8px',
                        backgroundColor: u.active ? '#d4edda' : '#f8d7da',
                        color: u.active ? '#155724' : '#721c24',
                        fontWeight: 'bold',
                        fontSize: '0.9rem'
                      }}
                    >
                      {u.active ? 'Yes' : 'No'}
                    </span>
                  </td>
                  <td>
                    <button className="edit-button" onClick={() => setEditingUser(u)}>
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {editingUser && (
          <EditUserModal
            user={editingUser}
            onClose={() => setEditingUser(null)}
            onUpdate={handleUserUpdate}
          />
        )}
      </div>
    </div>
  );
};

export default UserManagement;
