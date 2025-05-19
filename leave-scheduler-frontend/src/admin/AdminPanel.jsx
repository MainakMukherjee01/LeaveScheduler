// // src/admin/AdminPanel.jsx
// import React, { useState } from 'react';
// import AdminDashboard from '../AdminDashboard';
// import UserManagement from './UserManagement';
// import LeavePolicyManager from './LeavePolicyManager';
// import LeaveCreditPanel from './LeaveCreditPanel';
// import HolidayManager from './HolidayManager';
// import HolidayCalendar from '../shared/HolidayCalendar';
//
// const AdminPanel = () => {
//   const [tab, setTab] = useState('dashboard');
//
//   return (
//     <div style={{ padding: '20px' }}>
//       <h1>Admin Panel</h1>
//       <nav style={styles.nav}>
//         <button onClick={() => setTab('dashboard')}>Dashboard</button>
//         <button onClick={() => setTab('users')}>Users</button>
//         <button onClick={() => setTab('policies')}>Leave Policies</button>
//         <button onClick={() => setTab('credits')}>Leave Credit</button>
//         <button onClick={() => setTab('holidays')}>Holiday Management</button>
//         <button onClick={() => setTab('calendar')}>Holiday Calendar</button>
//
//       </nav>
//       <hr />
//       {tab === 'dashboard' && <AdminDashboard />}
//       {tab === 'users' && <UserManagement />}
//       {tab === 'policies' && <LeavePolicyManager />}
//       {tab === 'credits' && <LeaveCreditPanel />}
//       {tab === 'holidays' && <HolidayManager />}
//       {tab === 'calendar' && <HolidayCalendar />}
//     </div>
//   );
// };
//
// const styles = {
//   nav: {
//     display: 'flex',
//     gap: '10px',
//     marginBottom: '10px'
//   }
// };
//
// export default AdminPanel;

import React, { useState } from 'react';
import AdminDashboard from '../AdminDashboard';
import UserManagement from './UserManagement';
import LeavePolicyManager from './LeavePolicyManager';
import LeaveCreditPanel from './LeaveCreditPanel';
import HolidayManager from './HolidayManager';
import HolidayCalendar from '../shared/HolidayCalendar';

const AdminPanel = () => {
  const [tab, setTab] = useState('dashboard');

  const renderTabContent = () => {
    switch (tab) {
      case 'dashboard': return <AdminDashboard />;
      case 'users': return <UserManagement />;
      case 'policies': return <LeavePolicyManager />;
      case 'credits': return <LeaveCreditPanel />;
      case 'holidays': return <HolidayManager />;
      case 'calendar': return <HolidayCalendar />;
      default: return null;
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.sidebar}>
        <h2 style={styles.logo}>Admin Panel</h2>
        <button onClick={() => setTab('dashboard')} style={getBtnStyle(tab === 'dashboard')}>Dashboard</button>
        <button onClick={() => setTab('users')} style={getBtnStyle(tab === 'users')}>Users</button>
        <button onClick={() => setTab('policies')} style={getBtnStyle(tab === 'policies')}>Leave Policies</button>
        <button onClick={() => setTab('credits')} style={getBtnStyle(tab === 'credits')}>Leave Credit</button>
        <button onClick={() => setTab('holidays')} style={getBtnStyle(tab === 'holidays')}>Holiday Management</button>
        <button onClick={() => setTab('calendar')} style={getBtnStyle(tab === 'calendar')}>Holiday Calendar</button>
      </div>
      <div style={styles.content}>
        {renderTabContent()}
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    minHeight: '100vh',
    fontFamily: 'Arial, sans-serif',
    backgroundColor: '#f4f6f8',
  },
  sidebar: {
    width: '240px',
    background: '#2c3e50',
    color: '#fff',
    display: 'flex',
    flexDirection: 'column',
    padding: '20px 10px',
  },
  logo: {
    marginBottom: '20px',
    textAlign: 'center',
    fontSize: '1.5rem',
  },
  content: {
    flexGrow: 1,
    padding: '30px',
  },
};

const getBtnStyle = (active) => ({
  padding: '12px 16px',
  margin: '5px 0',
  background: active ? '#1abc9c' : 'transparent',
  border: 'none',
  color: '#fff',
  textAlign: 'left',
  cursor: 'pointer',
  fontSize: '1rem',
  borderRadius: '6px',
  transition: '0.3s',
  fontWeight: active ? 'bold' : 'normal',
});

export default AdminPanel;
