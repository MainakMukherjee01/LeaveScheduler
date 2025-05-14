// src/manager/PendingLeaves.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ApproveRejectModal from './ApproveRejectModal';

const PendingLeaves = () => {
  const [leaves, setLeaves] = useState([]);
  const [selectedLeave, setSelectedLeave] = useState(null);
  const [action, setAction] = useState(''); // 'approve' or 'reject'
  const token = localStorage.getItem('token');

  const fetchPendingLeaves = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/leave-approvals/pending', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setLeaves(res.data);
    } catch (err) {
      console.error('Error fetching pending leaves');
    }
  };

  useEffect(() => {
    fetchPendingLeaves();
  }, []);

  const handleDecision = (updatedId) => {
    setLeaves((prev) => prev.filter((leave) => leave.id !== updatedId));
    setSelectedLeave(null);
    setAction('');
  };

  return (
    <div>
      <h3>Pending Leave Applications</h3>
      <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
        <thead>
          <tr>
            <th>ID</th><th>Employee</th><th>Type</th><th>Start</th><th>End</th>
            <th>Days</th><th>Reason</th><th>Action</th>
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
              <td>{leave.reason}</td>
              <td>
                <button onClick={() => { setSelectedLeave(leave); setAction('approve'); }}>Approve</button>
                <button onClick={() => { setSelectedLeave(leave); setAction('reject'); }} style={{ marginLeft: '5px', color: 'red' }}>Reject</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedLeave && (
        <ApproveRejectModal
          leave={selectedLeave}
          action={action}
          onClose={() => setSelectedLeave(null)}
          onSuccess={handleDecision}
        />
      )}
    </div>
  );
};

export default PendingLeaves;
