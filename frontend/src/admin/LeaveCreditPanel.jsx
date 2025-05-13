// src/admin/LeaveCreditPanel.jsx
import React, { useState } from 'react';
import axios from 'axios';
import SpecialLeaveCreditForm from './SpecialLeaveCreditForm';

const LeaveCreditPanel = () => {
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');

  const creditAnnualLeaves = async () => {
    setMessage('');
    setError('');
    try {
      const res = await axios.post('http://localhost:8080/api/admin/credit-leaves', {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessage(res.data.message || 'Annual leave credited successfully!');
    } catch (err) {
      setError('Failed to credit annual leaves');
    }
  };

  return (
    <div>
      <h3>Leave Credit Management</h3>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={creditAnnualLeaves} style={styles.button}>
        Credit Annual Leave to All Users
      </button>

      <hr />
      <SpecialLeaveCreditForm />
    </div>
  );
};

const styles = {
  button: {
    padding: '10px 20px',
    backgroundColor: '#4caf50',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    marginBottom: '20px'
  }
};

export default LeaveCreditPanel;
