import React, { useState } from 'react';
import axios from 'axios';
import './Login.css';

const RegisterPage = ({ onRegisterSuccess }) => {
  const [formData, setFormData] = useState({
    username: '',
    fullName: '',
    email: '',
    password: '',
    department: '',
    roles: 'EMPLOYEE',
    phone: '',
    emergencyContact: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((p) => ({ ...p, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      await axios.post('http://localhost:8080/api/auth/register', {
        ...formData,
        roles: [formData.roles]
      });
      setMessage('Registration successful! Redirecting...');
      setTimeout(onRegisterSuccess, 2000);
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.response?.data?.error && err.response.data.error.message) ||
        'Registration failed. Please try again.';
      setError(message);
    }
  };

  return (
    <div className="login-container">
      <header className="login-header">
        <h1>Leave Scheduler</h1>
      </header>

      <div className="login-card">
        <h2>Register</h2>
        {message && <p style={{ color: 'green' }}>{message}</p>}
        {error && <p className="error-text">{error}</p>}

        <form onSubmit={handleSubmit}>
          <input
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
          />
          <input
            name="fullName"
            placeholder="Full Name"
            value={formData.fullName}
            onChange={handleChange}
            required
          />
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
          />
          <input
            name="department"
            placeholder="Department"
            value={formData.department}
            onChange={handleChange}
          />
          <select
            name="roles"
            value={formData.roles}
            onChange={handleChange}
            required
          >
            <option value="EMPLOYEE">EMPLOYEE</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
          <input
            name="phone"
            placeholder="Phone"
            value={formData.phone}
            onChange={handleChange}
          />
          <input
            name="emergencyContact"
            placeholder="Emergency Contact"
            value={formData.emergencyContact}
            onChange={handleChange}
          />

          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
