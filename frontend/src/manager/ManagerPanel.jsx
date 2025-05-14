// src/manager/ManagerPanel.jsx
import React, { useState } from 'react';
import PendingLeaves from './PendingLeaves';
import ApprovedLeaves from './ApprovedLeaves';
import HolidayCalendar from '../shared/HolidayCalendar';

const ManagerPanel = () => {
  const [tab, setTab] = useState('pending');

  return (
    <div style={{ padding: '20px' }}>
      <h2>Manager Dashboard</h2>
      <div style={styles.nav}>
        <button onClick={() => setTab('pending')}>Pending Approvals</button>
        <button onClick={() => setTab('approved')}>Approved Leaves</button>
        <button onClick={() => setTab('calendar')}>Holiday Calendar</button>
      </div>
      <hr />
      {tab === 'pending' ? <PendingLeaves /> : <ApprovedLeaves />}
      {tab === 'calendar' && <HolidayCalendar />}
    </div>
  );
};

const styles = {
  nav: {
    display: 'flex',
    gap: '10px',
    marginBottom: '15px'
  }
};

export default ManagerPanel;
