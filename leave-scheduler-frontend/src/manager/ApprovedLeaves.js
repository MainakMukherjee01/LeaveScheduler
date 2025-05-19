import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ApprovedLeaves = () => {
  const [leaves, setLeaves] = useState([]);
  const [error, setError] = useState('');

  const fetchApprovedLeaves = async () => {
    try {
      const { data } = await axios.get('http://localhost:8080/api/leave-approvals/approved');
      setLeaves(data);
    } catch (err) {
      setError('Failed to fetch approved leaves');
    }
  };

  useEffect(() => {
    fetchApprovedLeaves();
  }, []);

  return (
    <div className="container mt-4">
      <h2>Approved Leave Requests</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      {leaves.length === 0 ? (
        <p>No approved leaves.</p>
      ) : (
        <table className="table table-bordered">
          <thead>
            <tr>
              <th>Employee Name</th>
              <th>Leave Type</th>
              <th>Start Date</th>
              <th>End Date</th>
            </tr>
          </thead>
          <tbody>
            {leaves.map((leave) => (
              <tr key={leave.id}>
                <td>{leave.employeeName}</td>
                <td>{leave.leaveType}</td>
                <td>{leave.startDate}</td>
                <td>{leave.endDate}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ApprovedLeaves;
