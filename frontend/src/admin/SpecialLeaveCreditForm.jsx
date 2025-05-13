// src/admin/SpecialLeaveCreditForm.jsx
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
    <div>
      <h4>Credit Special Leave</h4>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '10px', maxWidth: '400px' }}>
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
        <button type="submit">Credit Special Leave</button>
      </form>

      {response.length > 0 && (
        <div style={{ marginTop: '20px' }}>
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

export default SpecialLeaveCreditForm;
