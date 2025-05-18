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

      console.log('API Response:', res.data); // Debugging
      if (res.data && res.data.message) {
        setMessage(res.data.message);
      } else {
        setMessage('Annual leave credited successfully!');
      }
    } catch (err) {
      console.error('Error crediting leaves:', err); // Debugging
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to credit annual leaves. Please check your network or permissions.');
      }
    }
  };

  return (
    <div style={styles.backgroundWrapper}>
      <div style={styles.container}>
        <h3 style={styles.heading}>Leave Credit Management</h3>
        {message && <p style={styles.success}>{message}</p>}
        {error && <p style={styles.error}>{error}</p>}

        <button onClick={creditAnnualLeaves} style={styles.blueButton}>
          Credit Annual Leave to All Users
        </button>

        <hr />
        <SpecialLeaveCreditForm />
      </div>
    </div>
  );
};

const styles = {
  backgroundWrapper: {
    minHeight: '100vh',
    backgroundImage: 'url("https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1600&q=80")',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    padding: '50px 0'
  },
  container: {
    maxWidth: '700px',
    margin: '0 auto',
    padding: '30px',
    background: 'rgba(255, 255, 255, 0.95)',
    borderRadius: '12px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.2)'
  },
  heading: {
    textAlign: 'center',
    color: '#333'
  },
  success: {
    color: 'green',
    fontWeight: 'bold'
  },
  error: {
    color: 'red',
    fontWeight: 'bold'
  },
  blueButton: {
    padding: '10px 20px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    marginBottom: '20px',
    cursor: 'pointer'
  }
};

export default LeaveCreditPanel;
