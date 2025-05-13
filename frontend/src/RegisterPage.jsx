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
    roles: ['EMPLOYEE'], // or ['MANAGER'], ['ADMIN']
    phone: '',
    emergencyContact: ''
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
        roles: [formData.roles] // Backend expects an array
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
        emergencyContact: ''
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
        <input name="username" placeholder="Username" value={formData.username} onChange={handleChange} required style={styles.input} />
        <input name="fullName" placeholder="Full Name" value={formData.fullName} onChange={handleChange} required style={styles.input} />
        <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleChange} required style={styles.input} />
        <input name="password" type="password" placeholder="Password" value={formData.password} onChange={handleChange} required style={styles.input} />
        <input name="department" placeholder="Department" value={formData.department} onChange={handleChange} style={styles.input} />
        <select name="roles" value={formData.roles} onChange={handleChange} required style={styles.input}>
          <option value="EMPLOYEE">EMPLOYEE</option>
          <option value="MANAGER">MANAGER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
        <input name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} style={styles.input} />
        <input name="emergencyContact" placeholder="Emergency Contact" value={formData.emergencyContact} onChange={handleChange} style={styles.input} />
        <button type="submit" style={styles.button}>Register</button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: '400px',
    margin: '50px auto',
    padding: '30px',
    border: '1px solid #ccc',
    borderRadius: '8px',
    textAlign: 'center',
    backgroundColor: '#f9f9f9'
  },
  form: {
    display: 'flex',
    flexDirection: 'column'
  },
  input: {
    padding: '10px',
    margin: '10px 0',
    fontSize: '16px'
  },
  button: {
    padding: '10px',
    fontSize: '16px',
    backgroundColor: '#2196f3',
    color: 'white',
    border: 'none',
    borderRadius: '4px'
  }
};

export default RegisterPage;
