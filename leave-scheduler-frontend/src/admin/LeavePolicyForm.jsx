import React, { useState, useEffect } from 'react';
import axios from 'axios';

const LeavePolicyManagement = () => {
  const [policies, setPolicies] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingPolicy, setEditingPolicy] = useState(null);

  useEffect(() => {
    fetchPolicies();
  }, []);

  const fetchPolicies = async () => {
    const token = localStorage.getItem('token');
    const res = await axios.get('http://localhost:8080/api/admin/leave-policies', {
      headers: { Authorization: `Bearer ${token}` }
    });
    setPolicies(res.data);
  };

  const handleEdit = (policy) => {
    setEditingPolicy(policy);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    const token = localStorage.getItem('token');
    await axios.delete(`http://localhost:8080/api/admin/leave-policies/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    fetchPolicies();
  };

  const handleSaved = () => {
    fetchPolicies();
    setShowForm(false);
    setEditingPolicy(null);
  };

  return (
    <div style={styles.wrapper}>
      <div style={styles.overlay}>
        <h1 style={styles.heading}>Leave Policy Management</h1>

        <button
          onClick={() => {
            setShowForm(true);
            setEditingPolicy(null);
          }}
          style={styles.createBtn}
        >
          + Create New
        </button>

        {policies.length > 0 && (
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>ID</th>
                <th style={styles.th}>Type</th>
                <th style={styles.th}>Annual Credit</th>
                <th style={styles.th}>Roles</th>
                <th style={styles.th}>Active</th>
                <th style={styles.th}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {policies.map((policy) => (
                <tr key={policy.id}>
                  <td style={styles.td}>{policy.id}</td>
                  <td style={styles.td}>{policy.leaveType}</td>
                  <td style={styles.td}>{policy.annualCredit}</td>
                  <td style={styles.td}>{policy.applicableRoles}</td>
                  <td style={styles.td}>{policy.isActive ? 'Yes' : 'No'}</td>
                  <td style={styles.td}>
                    <button style={styles.editBtn} onClick={() => handleEdit(policy)}>Edit</button>
                    <button style={styles.deleteBtn} onClick={() => handleDelete(policy.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {showForm && (
          <LeavePolicyForm
            policy={editingPolicy}
            onSaved={handleSaved}
            onCancel={() => {
              setShowForm(false);
              setEditingPolicy(null);
            }}
          />
        )}
      </div>
    </div>
  );
};

const LeavePolicyForm = ({ policy, onSaved, onCancel }) => {
  const isEdit = Boolean(policy);
  const [form, setForm] = useState({
    leaveType: '',
    description: '',
    annualCredit: '',
    maxAccumulation: '',
    minDuration: '',
    maxDuration: '',
    noticeRequired: '',
    carryForward: false,
    applicableRoles: 'EMPLOYEE',
    isActive: true
  });

  useEffect(() => {
    if (policy) {
      setForm(policy);
    }
  }, [policy]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    const url = isEdit
      ? `http://localhost:8080/api/admin/leave-policies/${policy.id}`
      : 'http://localhost:8080/api/admin/leave-policies';
    const method = isEdit ? 'put' : 'post';

    await axios[method](url, form, {
      headers: { Authorization: `Bearer ${token}` }
    });
    onSaved();
  };

  return (
    <form onSubmit={handleSubmit} style={styles.formCard}>
      <h2 style={styles.formTitle}>{isEdit ? 'Edit Policy' : 'Create New Policy'}</h2>
      <input name="leaveType" placeholder="Leave Type" value={form.leaveType} onChange={handleChange} required />
      <input name="description" placeholder="Description" value={form.description} onChange={handleChange} required />
      <input name="annualCredit" type="number" placeholder="Annual Credit" value={form.annualCredit} onChange={handleChange} required />
      <input name="maxAccumulation" type="number" placeholder="Max Accumulation Allowed" value={form.maxAccumulation} onChange={handleChange} />
      <input name="minDuration" type="number" placeholder="Minimum Duration (days)" value={form.minDuration} onChange={handleChange} />
      <input name="maxDuration" type="number" placeholder="Maximum Duration (days)" value={form.maxDuration} onChange={handleChange} />
      <input name="noticeRequired" type="number" placeholder="Notice Required (days)" value={form.noticeRequired} onChange={handleChange} />
      <label>
        <input type="checkbox" name="carryForward" checked={form.carryForward} onChange={handleChange} />
        Carry Forward
      </label>
      <label>
        Applicable Roles:
        <select name="applicableRoles" value={form.applicableRoles} onChange={handleChange}>
          <option value="EMPLOYEE">EMPLOYEE</option>
          <option value="MANAGER">MANAGER</option>
          <option value="ADMIN">ADMIN</option>
        </select>
      </label>
      <label>
        Active:
        <select name="isActive" value={form.isActive} onChange={(e) => setForm({ ...form, isActive: e.target.value === 'true' })}>
          <option value="true">Yes</option>
          <option value="false">No</option>
        </select>
      </label>
      <div style={styles.buttonRow}>
        <button type="submit" style={styles.submitBtn}>Save</button>
        <button type="button" onClick={onCancel} style={styles.cancelBtn}>Cancel</button>
      </div>
    </form>
  );
};

const styles = {
  wrapper: {
    backgroundImage: 'url("https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1600&q=80")',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    minHeight: '100vh',
    padding: '40px'
  },
  overlay: {
    backgroundColor: 'rgba(255,255,255,0.9)',
    borderRadius: '16px',
    padding: '30px',
    maxWidth: '1000px',
    margin: '0 auto'
  },
  heading: {
    fontSize: '28px',
    marginBottom: '20px',
    textAlign: 'center',
    color: '#333'
  },
  createBtn: {
    backgroundColor: '#007bff',
    color: '#fff',
    padding: '10px 20px',
    fontSize: '16px',
    borderRadius: '8px',
    border: 'none',
    cursor: 'pointer',
    marginBottom: '20px'
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    marginBottom: '30px'
  },
  th: {
    backgroundColor: '#343a40',
    color: '#fff',
    padding: '10px',
    textAlign: 'left'
  },
  td: {
    padding: '10px',
    borderBottom: '1px solid #ccc'
  },
  editBtn: {
    backgroundColor: '#007bff',
    color: '#fff',
    padding: '6px 12px',
    marginRight: '8px',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer'
  },
  deleteBtn: {
    backgroundColor: '#dc3545',
    color: '#fff',
    padding: '6px 12px',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer'
  },
  formCard: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  formTitle: {
    textAlign: 'center',
    marginBottom: '12px',
    fontSize: '22px'
  },
  buttonRow: {
    display: 'flex',
    justifyContent: 'space-between'
  },
  submitBtn: {
    backgroundColor: '#28a745',
    color: 'white',
    padding: '10px 20px',
    borderRadius: '6px',
    border: 'none',
    cursor: 'pointer'
  },
  cancelBtn: {
    backgroundColor: '#6c757d',
    color: 'white',
    padding: '10px 20px',
    borderRadius: '6px',
    border: 'none',
    cursor: 'pointer'
  }
};

export default LeavePolicyManagement;
