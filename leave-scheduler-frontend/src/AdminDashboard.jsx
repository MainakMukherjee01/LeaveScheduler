import React, { useEffect, useState } from 'react';
import axios from 'axios';

const AdminDashboard = ({ user }) => {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get('http://localhost:8080/api/admin/dashboard-stats', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setStats(res.data);
      } catch (err) {
        setError('Unauthorized or error loading admin dashboard');
      }
    };

    fetchStats();
  }, []);

  if (error) return <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>;
  if (!stats) return <p style={{ textAlign: 'center' }}>Loading dashboard...</p>;

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>Admin Dashboard</h2>

      <div style={styles.statGrid}>
        <StatCard title="Total Users" value={stats.totalUsers} />
        <StatCard title="Active Users" value={stats.activeUsers} />
        <StatCard title="Pending Leave Requests" value={stats.pendingLeaves} />
      </div>

      <div style={styles.section}>
        <h3 style={styles.sectionTitle}>Role Distribution</h3>
        <ul style={styles.list}>
          {Object.entries(stats.roleDistribution).map(([role, count]) => (
            <li key={role} style={styles.listItem}>
              <span style={styles.badge}>{role}</span> {count}
            </li>
          ))}
        </ul>
      </div>

      <div style={styles.section}>
        <h3 style={styles.sectionTitle}>Recent Leave Applications</h3>
        <ul style={styles.leaveList}>
          {stats.recentLeaveApplications.map((leave) => (
            <li key={leave.id} style={styles.leaveItem}>
              <strong>{leave.leaveType}</strong> - <span>{leave.status}</span>
              <div style={styles.date}>{new Date(leave.createdAt).toLocaleDateString()}</div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

// Reusable stat card component
const StatCard = ({ title, value }) => (
  <div style={styles.statCard}>
    <div style={styles.statValue}>{value}</div>
    <div style={styles.statLabel}>{title}</div>
  </div>
);

const styles = {
  container: {
    maxWidth: '1000px',
    margin: '0 auto',
    padding: '30px',
    backgroundColor: '#fff',
    borderRadius: '12px',
    boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
  },
  title: {
    textAlign: 'center',
    fontSize: '2rem',
    marginBottom: '30px',
    color: '#2c3e50',
  },
  statGrid: {
    display: 'flex',
    gap: '20px',
    marginBottom: '30px',
    justifyContent: 'space-around',
  },
  statCard: {
    flex: 1,
    background: '#ecf0f1',
    borderRadius: '10px',
    padding: '20px',
    textAlign: 'center',
    boxShadow: '0 2px 10px rgba(0,0,0,0.05)',
  },
  statValue: {
    fontSize: '2rem',
    fontWeight: 'bold',
    color: '#27ae60',
  },
  statLabel: {
    marginTop: '10px',
    fontSize: '1rem',
    color: '#555',
  },
  section: {
    marginBottom: '30px',
  },
  sectionTitle: {
    fontSize: '1.4rem',
    borderBottom: '2px solid #ddd',
    paddingBottom: '8px',
    marginBottom: '15px',
    color: '#34495e',
  },
  list: {
    listStyle: 'none',
    paddingLeft: 0,
  },
  listItem: {
    padding: '8px 0',
    borderBottom: '1px solid #eee',
    fontSize: '1rem',
  },
  badge: {
    backgroundColor: '#3498db',
    color: '#fff',
    padding: '3px 10px',
    borderRadius: '12px',
    marginRight: '10px',
    fontSize: '0.9rem',
  },
  leaveList: {
    listStyle: 'none',
    paddingLeft: 0,
  },
  leaveItem: {
    padding: '10px',
    border: '1px solid #eee',
    borderRadius: '8px',
    marginBottom: '10px',
    background: '#fafafa',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  date: {
    fontSize: '0.85rem',
    color: '#888',
  },
};

export default AdminDashboard;
