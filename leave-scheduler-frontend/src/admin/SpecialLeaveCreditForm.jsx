import React, { useState } from 'react';
import axios from 'axios';

const SpecialLeaveCreditForm = () => {
  const [userIds, setUserIds] = useState('');
  const [leaveType, setLeaveType] = useState('SICK');
  const [amount, setAmount] = useState('');
  const [reason, setReason] = useState('');
  const [response, setResponse] = useState([]);
  const token = localStorage.getItem('token');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setResponse([]);
    try {
      const res = await axios.post(
        `http://localhost:8080/api/admin/credit-special-leave`,
        null,
        {
          params: {
            userIds: userIds.split(',').map(id => id.trim()),
            leaveType,
            amount,
            reason
          },
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      setResponse(res.data);
    } catch (err) {
      setResponse([{ success: false, message: 'Error crediting special leave' }]);
    }
  };

  return (
    <div style={styles.container}>
      <h4 style={styles.heading}>Credit Special Leave</h4>
      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          placeholder="User IDs (comma separated)"
          value={userIds}
          onChange={(e) => setUserIds(e.target.value)}
          required
        />
        <select value={leaveType} onChange={(e) => setLeaveType(e.target.value)}>
          <option value="SICK">SICK</option>
          <option value="CASUAL">CASUAL</option>
          <option value="VACATION">VACATION</option>
          <option value="EMERGENCY">EMERGENCY</option>
        </select>
        <input
          type="number"
          placeholder="Leave Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          required
        />
        <input
          placeholder="Reason"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          required
        />
        <button type="submit" style={styles.button}>Credit Special Leave</button>
      </form>

      {response.length > 0 && (
        <div style={styles.response}>
          <h5>Response:</h5>
          <ul>
            {response.map((r, idx) => (
              <li key={idx} style={{ color: r.success ? 'green' : 'red' }}>{r.message}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    background: '#fff',
    padding: '20px',
    borderRadius: '10px',
    boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
    marginTop: '20px'
  },
  heading: {
    marginBottom: '15px',
    color: '#333'
  },
  form: {
    display: 'grid',
    gap: '10px',
    maxWidth: '400px'
  },
 button: {
   backgroundColor: '#007bff',
   color: 'white',
   padding: '10px',
   border: 'none',
   borderRadius: '5px',
   cursor: 'pointer',
   transition: 'background-color 0.3s ease'
 },
 buttonHover: {
   backgroundColor: '#0056b3' // dark blue on hover
 },

  response: {
    marginTop: '20px'
  }

};

export default SpecialLeaveCreditForm;
