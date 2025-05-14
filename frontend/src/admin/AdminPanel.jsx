// src/admin/AdminPanel.jsx
import React, { useState } from 'react';
import AdminDashboard from '../AdminDashboard';
import UserManagement from './UserManagement';
import LeavePolicyManager from './LeavePolicyManager';
import LeaveCreditPanel from './LeaveCreditPanel';
import HolidayManager from './HolidayManager';
import HolidayCalendar from '../shared/HolidayCalendar';

const AdminPanel = () => {
  const [tab, setTab] = useState('dashboard');

  return (
    <div style={{ padding: '20px' }}>
      <h1>Admin Panel</h1>
      <nav style={styles.nav}>
        <button onClick={() => setTab('dashboard')}>Dashboard</button>
        <button onClick={() => setTab('users')}>Users</button>
        <button onClick={() => setTab('policies')}>Leave Policies</button>
        <button onClick={() => setTab('credits')}>Leave Credit</button>
        <button onClick={() => setTab('holidays')}>Holiday Management</button>
        <button onClick={() => setTab('calendar')}>Holiday Calendar</button>

      </nav>
      <hr />
      {tab === 'dashboard' && <AdminDashboard />}
      {tab === 'users' && <UserManagement />}
      {tab === 'policies' && <LeavePolicyManager />}
      {tab === 'credits' && <LeaveCreditPanel />}
      {tab === 'holidays' && <HolidayManager />}
      {tab === 'calendar' && <HolidayCalendar />}
    </div>
  );
};

const styles = {
  nav: {
    display: 'flex',
    gap: '10px',
    marginBottom: '10px'
  }
};

export default AdminPanel;
