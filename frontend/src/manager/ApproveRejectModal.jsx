// src/manager/ApproveRejectModal.jsx
import React, { useState } from 'react';
import axios from 'axios';

const ApproveRejectModal = ({ leave, action, onClose, onSuccess }) => {
  const [remark, setRemark] = useState('');
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await axios.put(
        `http://localhost:8080/api/leave-approvals/${leave.id}/${action}`,
        { remarks: remark },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      onSuccess(leave.id);
    } catch (err) {
      setError(`Failed to ${action} leave.`);
    }
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <h3>{action === 'approve' ? 'Approve' : 'Reject'} Leave</h3>
        <p><strong>Employee:</strong> {leave.username}</p>
        <p><strong>Leave Type:</strong> {leave.leaveType}</p>
        <p><strong>Start:</strong> {leave.startDate} → <strong>End:</strong> {leave.endDate}</p>

        {error && <p style={{ color: 'red' }}>{error}</p>}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <textarea
            placeholder="Enter remark..."
            value={remark}
            onChange={(e) => setRemark(e.target.value)}
            required
            rows={3}
          />
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <button type="submit">{action === 'approve' ? 'Approve' : 'Reject'}</button>
            <button type="button" onClick={onClose} style={{ backgroundColor: 'gray', color: 'white' }}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

const styles = {
  overlay: {
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.3)', display: 'flex', justifyContent: 'center', alignItems: 'center'
  },
  modal: {
    backgroundColor: 'white', padding: '20px', borderRadius: '10px', width: '400px'
  }
};

export default ApproveRejectModal;
