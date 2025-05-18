import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ApproveRejectModal from './ApproveRejectModal';

const PendingLeaves = () => {
  const [leaves, setLeaves] = useState([]);
  const [selectedLeave, setSelectedLeave] = useState(null);
  const [error, setError] = useState('');

  const fetchPendingLeaves = async () => {
    try {
      const { data } = await axios.get('http://localhost:8080/api/leave-approvals/pending');
      setLeaves(data);
    } catch (err) {
      setError('Failed to fetch pending leaves');
    }
  };

  useEffect(() => {
    fetchPendingLeaves();
  }, []);

  const handleOpenModal = (leave) => {
    setSelectedLeave(leave);
  };

  const handleCloseModal = () => {
    setSelectedLeave(null);
  };

  const handleDecision = async (id, decision) => {
    try {
      const url = `http://localhost:8080/api/leave-approvals/${id}/${decision}`;
      await axios.put(url);
      setLeaves((prev) => prev.filter((leave) => leave.id !== id));
      setSelectedLeave(null);
    } catch {
      setError('Failed to process the leave decision');
    }
  };

  return (
    <div className="container mt-4">
      <h2>Pending Leave Requests</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      {leaves.length === 0 ? (
        <p>No pending leaves.</p>
      ) : (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Employee Name</th>
              <th>Leave Type</th>
              <th>From</th>
              <th>To</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {leaves.map((leave) => (
              <tr key={leave.id}>
                <td>{leave.employeeName}</td>
                <td>{leave.leaveType}</td>
                <td>{leave.startDate}</td>
                <td>{leave.endDate}</td>
                <td>
                  <button className="btn btn-primary btn-sm" onClick={() => handleOpenModal(leave)}>
                    Review
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {selectedLeave && (
        <ApproveRejectModal
          leave={selectedLeave}
          onClose={handleCloseModal}
          onDecision={handleDecision}
        />
      )}
    </div>
  );
};

export default PendingLeaves;
