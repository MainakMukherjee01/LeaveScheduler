// src/employee/EmployeePanel.jsx
import React, { useState } from 'react';
import LeaveForm from './LeaveForm';
import MyLeaveList from './MyLeaveList';

const EmployeePanel = () => {
  const [tab, setTab] = useState('apply');

  return (
    <div style={{ padding: '20px' }}>
      <h2>Employee Dashboard</h2>
      <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
        <button onClick={() => setTab('apply')}>Apply for Leave</button>
        <button onClick={() => setTab('history')}>My Leave History</button>
      </div>
      <hr />
      {tab === 'apply' && <LeaveForm />}
      {tab === 'history' && <MyLeaveList />}
    </div>
  );
};

export default EmployeePanel;
