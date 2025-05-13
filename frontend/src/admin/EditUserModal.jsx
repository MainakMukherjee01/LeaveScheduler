// src/admin/EditUserModal.jsx
import React, { useState } from 'react';
import axios from 'axios';

const EditUserModal = ({ user, onClose, onUpdate }) => {
  const [form, setForm] = useState({ ...user });
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await axios.put(`http://localhost:8080/api/admin/users/${user.id}`, form, {
        headers: { Authorization: `Bearer ${token}` }
      });
      onUpdate(res.data);
    } catch (err) {
      setError('Failed to update user');
    }
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <h3>Edit User: {user.username}</h3>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <form onSubmit={handleSubmit} style={styles.form}>
          <input name="fullName" value={form.fullName} onChange={handleChange} placeholder="Full Name" />
          <input name="email" value={form.email} onChange={handleChange} placeholder="Email" />
          <input name="department" value={form.department} onChange={handleChange} placeholder="Department" />
          <select name="roles" onChange={(e) => setForm({ ...form, roles: [e.target.value] })}>
            <option value="EMPLOYEE">EMPLOYEE</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <label>
            Active:
            <select name="active" value={form.active} onChange={(e) => setForm({ ...form, active: e.target.value === 'true' })}>
              <option value="true">Yes</option>
              <option value="false">No</option>
            </select>
          </label>
          <div style={styles.buttons}>
            <button type="submit">Save</button>
            <button type="button" onClick={onClose} style={{ backgroundColor: 'red', color: 'white' }}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

const styles = {
  overlay: {
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.3)', display: 'flex', justifyContent: 'center', alignItems: 'center'
  },
  modal: {
    backgroundColor: 'white', padding: '20px', borderRadius: '8px', width: '400px'
  },
  form: {
    display: 'flex', flexDirection: 'column', gap: '10px'
  },
  buttons: {
    display: 'flex', justifyContent: 'space-between'
  }
};

export default EditUserModal;
