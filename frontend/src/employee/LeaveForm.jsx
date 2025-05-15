// src/employee/LeaveForm.jsx
import React, { useState } from 'react';
import axios from 'axios';

const LeaveForm = () => {
  const token = localStorage.getItem('token');
  const [form, setForm] = useState({
    leaveType: 'SICK',
    startDate: '',
    endDate: '',
    reason: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const calculateDays = () => {
    const start = new Date(form.startDate);
    const end = new Date(form.endDate);
    const timeDiff = end - start;
    if (isNaN(timeDiff) || timeDiff < 0) return 0;
    return Math.floor(timeDiff / (1000 * 60 * 60 * 24)) + 1;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/leave-applications', form, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessage('Leave application submitted successfully!');
      setForm({ leaveType: 'SICK', startDate: '', endDate: '', reason: '' });
    } catch (err) {
      setError('Failed to submit leave application');
    }
  };

  return (
    <div>
      <h3>Apply for Leave</h3>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '10px', maxWidth: '400px' }}>
        <label>
          Leave Type:
          <select name="leaveType" value={form.leaveType} onChange={handleChange}>
            <option value="SICK">SICK</option>
            <option value="CASUAL">CASUAL</option>
            <option value="VACATION">VACATION</option>
            <option value="EMERGENCY">EMERGENCY</option>
          </select>
        </label>
        <label>
          Start Date:
          <input type="date" name="startDate" value={form.startDate} onChange={handleChange} required />
        </label>
        <label>
          End Date:
          <input type="date" name="endDate" value={form.endDate} onChange={handleChange} required />
        </label>
        <label>
          Reason:
          <textarea name="reason" value={form.reason} onChange={handleChange} required />
        </label>
        <p><strong>Total Days:</strong> {calculateDays()}</p>
        <button type="submit">Submit Application</button>
      </form>
    </div>
  );
};

export default LeaveForm;
