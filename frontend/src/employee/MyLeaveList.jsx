// src/employee/MyLeaveList.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const MyLeaveList = () => {
  const [leaves, setLeaves] = useState([]);
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchLeaves = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/leaves/my-leaves', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setLeaves(res.data);
      } catch (err) {
        console.error('Failed to fetch leave history');
      }
    };

    fetchLeaves();
  }, []);

  return (
    <div>
      <h3>My Leave History</h3>
      <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
        <thead>
          <tr>
            <th>ID</th><th>Type</th><th>Start</th><th>End</th><th>Days</th>
            <th>Status</th><th>Manager Remark</th>
          </tr>
        </thead>
        <tbody>
          {leaves.map((l) => (
            <tr key={l.id}>
              <td>{l.id}</td>
              <td>{l.leaveType}</td>
              <td>{l.startDate}</td>
              <td>{l.endDate}</td>
              <td>{l.numberOfDays}</td>
              <td>{l.status}</td>
              <td>{l.remarks || '—'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MyLeaveList;
