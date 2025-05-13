// src/AdminDashboard.jsx
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

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!stats) return <p>Loading dashboard...</p>;

  return (
    <div style={styles.container}>
      <h2>Admin Dashboard</h2>
      <p>Total Users: {stats.totalUsers}</p>
      <p>Active Users: {stats.activeUsers}</p>
      <p>Pending Leave Requests: {stats.pendingLeaves}</p>

      <h3>Role Distribution</h3>
      <ul>
        {Object.entries(stats.roleDistribution).map(([role, count]) => (
          <li key={role}>{role}: {count}</li>
        ))}
      </ul>

      <h3>Recent Leave Applications</h3>
      <ul>
        {stats.recentLeaveApplications.map((leave) => (
          <li key={leave.id}>{leave.leaveType} - {leave.status} - {leave.createdAt}</li>
        ))}
      </ul>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: '600px',
    margin: '30px auto',
    padding: '20px',
    backgroundColor: '#f4f4f4',
    borderRadius: '10px'
  }
};

export default AdminDashboard;
