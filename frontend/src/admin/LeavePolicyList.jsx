// src/admin/LeavePolicyList.jsx
import React from 'react';

const LeavePolicyList = ({ policies, onEdit, onDelete }) => {
  return (
    <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
      <thead>
        <tr>
          <th>ID</th><th>Type</th><th>Annual Credit</th><th>Roles</th><th>Active</th><th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {policies.map((p) => (
          <tr key={p.id}>
            <td>{p.id}</td>
            <td>{p.leaveType}</td>
            <td>{p.annualCredit}</td>
            <td>{p.applicableRoles.join(', ')}</td>
            <td>{p.isActive ? 'Yes' : 'No'}</td>
            <td>
              <button onClick={() => onEdit(p)}>Edit</button>
              <button onClick={() => onDelete(p.id)} style={{ color: 'red' }}>Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default LeavePolicyList;
