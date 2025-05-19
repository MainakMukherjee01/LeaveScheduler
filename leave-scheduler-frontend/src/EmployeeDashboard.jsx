import React, { useEffect, useState } from 'react';
import axios from 'axios';

const EmployeeDashboard = () => {
  const [leaves, setLeaves] = useState([]);
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchLeaves = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/leave-applications/user', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setLeaves(res.data);
      } catch (err) {
        setError('⚠️ Failed to fetch leave data. Please try again.');
      }
    };

    fetchLeaves();
  }, []);

  const total = leaves.length;
  const pending = leaves.filter(l => l.status === 'PENDING').length;
  const approved = leaves.filter(l => l.status === 'APPROVED').length;
  const rejected = leaves.filter(l => l.status === 'REJECTED').length;

  return (
    <div
      style={{
        backgroundImage: `url('https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1600&q=80')`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: '100vh',
        padding: '40px',
        fontFamily: 'Segoe UI, sans-serif'
      }}
    >
      <div style={{ backgroundColor: 'white', borderRadius: '12px', padding: '30px', maxWidth: '1000px', margin: '0 auto', boxShadow: '0 6px 24px rgba(0,0,0,0.15)' }}>
        <h2 style={{ textAlign: 'center', marginBottom: '30px' }}>🧾 Employee Leave Dashboard</h2>

        {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}

        <div style={{ display: 'flex', justifyContent: 'space-between', gap: '20px', marginBottom: '30px' }}>
          {summaryCard('Total Leaves', total, '#6c5ce7')}
          {summaryCard('Pending', pending, '#f39c12')}
          {summaryCard('Approved', approved, '#27ae60')}
          {summaryCard('Rejected', rejected, '#c0392b')}
        </div>

        <h4 style={{ marginBottom: '10px' }}>📋 My Leave History</h4>
        <div style={{ overflowX: 'auto' }}>
          <table style={tableStyle}>
            <thead style={{ backgroundColor: '#f0f0f0' }}>
              <tr>
                <th style={cell}>Type</th>
                <th style={cell}>From</th>
                <th style={cell}>To</th>
                <th style={cell}>Days</th>
                <th style={cell}>Status</th>
                <th style={cell}>Reason</th>
              </tr>
            </thead>
            <tbody>
              {leaves.map((leave) => (
                <tr key={leave.id}>
                  <td style={cell}>{leave.leaveType}</td>
                  <td style={cell}>{leave.startDate}</td>
                  <td style={cell}>{leave.endDate}</td>
                  <td style={cell}>{leave.numberOfDays}</td>
                  <td style={{ ...cell, color: getStatusColor(leave.status), fontWeight: 600 }}>{leave.status}</td>
                  <td style={cell}>{leave.reason || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

const summaryCard = (title, count, color) => (
  <div style={{
    flex: 1,
    backgroundColor: color,
    color: 'white',
    padding: '20px',
    borderRadius: '10px',
    textAlign: 'center',
    boxShadow: '0 2px 12px rgba(0,0,0,0.2)'
  }}>
    <h4>{title}</h4>
    <p style={{ fontSize: '28px', margin: '10px 0 0' }}>{count}</p>
  </div>
);

const getStatusColor = (status) => {
  switch (status) {
    case 'PENDING': return '#f39c12';
    case 'APPROVED': return '#27ae60';
    case 'REJECTED': return '#c0392b';
    default: return 'black';
  }
};

const tableStyle = {
  width: '100%',
  borderCollapse: 'collapse',
  fontSize: '15px',
  marginTop: '10px'
};

const cell = {
  padding: '10px',
  border: '1px solid #ddd',
  textAlign: 'center'
};

export default EmployeeDashboard;
