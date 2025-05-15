// src/RegisterPage.jsx
import React, { useState } from 'react';
import axios from 'axios';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    fullName: '',
    email: '',
    password: '',
    department: '',
    roles: ['EMPLOYEE'],
    phone: '',
    emergencyContact: '',
    joiningDate: '',
    managerId: ''
  });

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        ...formData,
        managerId: formData.managerId ? parseInt(formData.managerId) : null,
        roles: [formData.roles]  // wrap in array
      });

      setMessage('Registration successful!');
      setFormData({
        username: '',
        fullName: '',
        email: '',
        password: '',
        department: '',
        roles: ['EMPLOYEE'],
        phone: '',
        emergencyContact: '',
        joiningDate: '',
        managerId: ''
      });
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Registration failed';
      setError(errMsg);
    }
  };

  return (
    <div style={styles.container}>
      <h2>Register</h2>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit} style={styles.form}>
        <input name="username" placeholder="Username" value={formData.username} onChange={handleChange} required />
        <input name="fullName" placeholder="Full Name" value={formData.fullName} onChange={handleChange} required />
        <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Password" value={formData.password} onChange={handleChange} required />
        <input name="department" placeholder="Department" value={formData.department} onChange={handleChange} required />
        <select name="roles" value={formData.roles} onChange={handleChange} required>
          <option value="EMPLOYEE">EMPLOYEE</option>
          <option value="MANAGER">MANAGER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
        <input name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} required />
        <input name="emergencyContact" placeholder="Emergency Contact" value={formData.emergencyContact} onChange={handleChange} required />
        <input name="joiningDate" type="date" placeholder="Joining Date" value={formData.joiningDate} onChange={handleChange} required />
        <input name="managerId" type="number" placeholder="Manager ID (optional)" value={formData.managerId} onChange={handleChange}/>

        <button type="submit" style={styles.button}>Register</button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: '500px',
    margin: '40px auto',
    padding: '20px',
    backgroundColor: '#f0f0f0',
    borderRadius: '8px'
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '10px'
  },
  button: {
    padding: '10px',
    fontSize: '16px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '5px'
  }
};

export default RegisterPage;
