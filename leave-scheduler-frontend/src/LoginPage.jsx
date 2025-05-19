import React, { useState } from 'react';
import axios from 'axios';
import './Login.css';

const LoginPage = ({ onLogin, onRegister }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      const { data } = await axios.post(
        'http://localhost:8080/api/auth/login',
        { username, password }
      );
      const { token, id, username: user, email, roles } = data;
      localStorage.setItem('token', token);
      setSuccess('Login successful! Redirecting...');
      setTimeout(() => {
        onLogin({ id, user, email, roles, token });
      }, 1500);
    } catch (err) {
      const message = err.response?.data?.message || 'Invalid username or password';
      setError(message);
    }
  };

  return (
    <div className="login-container">
      <button className="top-right-register" onClick={onRegister}>
        Register
      </button>

      <div className="login-card">
        <h2>Employee Login</h2>
        {error && <p className="error-text">{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}

        <form onSubmit={handleSubmit}>
          <label>Employee ID:</label>
          <input
            type="text"
            value={username}
            placeholder="Enter your ID"
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <label>Password:</label>
          <input
            type="password"
            value={password}
            placeholder="Enter your password"
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
