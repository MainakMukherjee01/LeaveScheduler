// src/manager/ApprovedLeaves.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ApprovedLeaves = () => {
  const [leaves, setLeaves] = useState([]);
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchApproved = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/leave-approvals/approved', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setLeaves(res.data);
      } catch (err) {
        console.error('Error fetching approved leaves');
      }
    };

    fetchApproved();
  }, []);

  return (
    <div>
      <h3>Approved Leave History</h3>
      <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
        <thead>
          <tr>
            <th>ID</th><th>Employee</th><th>Type</th><th>Start</th><th>End</th>
            <th>Days</th><th>Status</th><th>Approved On</th><th>Remark</th>
          </tr>
        </thead>
        <tbody>
          {leaves.map((leave) => (
            <tr key={leave.id}>
              <td>{leave.id}</td>
              <td>{leave.username}</td>
              <td>{leave.leaveType}</td>
              <td>{leave.startDate}</td>
              <td>{leave.endDate}</td>
              <td>{leave.numberOfDays}</td>
              <td>{leave.status}</td>
              <td>{leave.approvedOn || '—'}</td>
              <td>{leave.remarks || '—'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ApprovedLeaves;
