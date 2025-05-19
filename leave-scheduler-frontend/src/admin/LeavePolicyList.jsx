import React from 'react';

const LeavePolicyList = ({ policies, onEdit, onDelete }) => {
  return (
    <div style={styles.container}>
      <table style={styles.table}>
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
                <button onClick={() => onEdit(p)} style={styles.editBtn}>Edit</button>
                <button onClick={() => onDelete(p.id)} style={styles.deleteBtn}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const styles = {
  container: {
    overflowX: 'auto',
    marginBottom: '20px'
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    marginTop: '10px',
    background: '#fff',
    boxShadow: '0 2px 6px rgba(0,0,0,0.1)'
  },
  editBtn: {
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '4px',
    marginRight: '5px'
  },
  deleteBtn: {
    backgroundColor: 'red',
    color: 'white',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '4px'
  }
};

export default LeavePolicyList;