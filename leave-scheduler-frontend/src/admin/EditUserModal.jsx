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
        <h2 style={styles.title}>Edit User: {user.username}</h2>
        {error && <p style={styles.error}>{error}</p>}
        <form onSubmit={handleSubmit} style={styles.form}>
          <input
            name="fullName"
            value={form.fullName}
            onChange={handleChange}
            placeholder="Full Name"
            style={styles.input}
          />
          <input
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="Email"
            style={styles.input}
          />
          <input
            name="department"
            value={form.department}
            onChange={handleChange}
            placeholder="Department"
            style={styles.input}
          />

          <select
            name="roles"
            value={form.roles[0]}
            onChange={(e) => setForm({ ...form, roles: [e.target.value] })}
            style={styles.select}
          >
            <option value="EMPLOYEE">EMPLOYEE</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>

          <label style={styles.label}>
            Active:
            <select
              name="active"
              value={form.active ? 'true' : 'false'}
              onChange={(e) => setForm({ ...form, active: e.target.value === 'true' })}
              style={{ ...styles.select, marginLeft: '10px' }}
            >
              <option value="true">Yes</option>
              <option value="false">No</option>
            </select>
          </label>

          <div style={styles.buttons}>
            <button type="submit" style={styles.saveButton}>💾 Save</button>
            <button type="button" onClick={onClose} style={styles.cancelButton}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

const styles = {
  overlay: {
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex', justifyContent: 'center', alignItems: 'center',
    zIndex: 999
  },
  modal: {
    backgroundColor: '#ffffff',
    padding: '30px',
    borderRadius: '12px',
    width: '100%',
    maxWidth: '500px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.2)',
  },
  title: {
    fontSize: '1.5rem',
    marginBottom: '20px',
    textAlign: 'center',
    color: '#2c3e50',
  },
  error: {
    color: '#e74c3c',
    textAlign: 'center',
    marginBottom: '15px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px',
  },
  input: {
    padding: '10px 14px',
    borderRadius: '6px',
    border: '1px solid #ccc',
    fontSize: '1rem',
  },
  select: {
    padding: '10px',
    borderRadius: '6px',
    border: '1px solid #ccc',
    fontSize: '1rem',
  },
  label: {
    display: 'flex',
    alignItems: 'center',
    fontWeight: 'bold',
    color: '#2c3e50',
  },
  buttons: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '20px',
  },
  saveButton: {
    backgroundColor: '#2ecc71',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    fontWeight: 'bold',
    cursor: 'pointer',
    transition: '0.3s',
  },
  cancelButton: {
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    fontWeight: 'bold',
    cursor: 'pointer',
    transition: '0.3s',
  },
};

export default EditUserModal;
