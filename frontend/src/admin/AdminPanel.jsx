// src/admin/AdminPanel.jsx
import React, { useState } from 'react';
import AdminDashboard from '../AdminDashboard';
import UserManagement from './UserManagement';
import LeavePolicyManager from './LeavePolicyManager';
import LeaveCreditPanel from './LeaveCreditPanel';

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
      </nav>
      <hr />
      {tab === 'dashboard' && <AdminDashboard />}
      {tab === 'users' && <UserManagement />}
      {tab === 'policies' && <LeavePolicyManager />}
      {tab === 'credits' && <LeaveCreditPanel />}
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
