import React, { useState } from 'react';
import PendingLeaves from "./PendingLeaves";
import ApprovedLeaves from './ApprovedLeaves';
import HolidayCalendar from '../shared/HolidayCalendar';

const ManagerPanel = () => {
  const [tab, setTab] = useState('pending');

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Manager Dashboard</h2>
      <div style={styles.nav}>
        <button style={tab === 'pending' ? styles.activeBtn : styles.btn} onClick={() => setTab('pending')}>Pending</button>
        <button style={tab === 'approved' ? styles.activeBtn : styles.btn} onClick={() => setTab('approved')}>Approved</button>
        <button style={tab === 'calendar' ? styles.activeBtn : styles.btn} onClick={() => setTab('calendar')}>Calendar</button>
      </div>
      <div style={styles.content}>
        {tab === 'pending' ? <PendingLeaves /> : tab === 'approved' ? <ApprovedLeaves /> : <HolidayCalendar />}
      </div>
    </div>
  );
};

const styles = {
  container: { padding: '30px', fontFamily: 'Arial, sans-serif', backgroundColor: '#f8f9fa', minHeight: '100vh' },
  heading: { textAlign: 'center', marginBottom: '25px', color: '#2c3e50' },
  nav: { display: 'flex', justifyContent: 'center', gap: '20px', marginBottom: '20px' },
  btn: {
    padding: '10px 20px',
    backgroundColor: '#ecf0f1',
    border: '1px solid #bdc3c7',
    borderRadius: '8px',
    cursor: 'pointer'
  },
  activeBtn: {
    padding: '10px 20px',
    backgroundColor: '#3498db',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer'
  },
  content: { backgroundColor: '#fff', padding: '20px', borderRadius: '12px', boxShadow: '0 0 10px rgba(0,0,0,0.1)' }
};

export default ManagerPanel;
