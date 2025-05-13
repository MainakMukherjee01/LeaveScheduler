// src/admin/LeavePolicyManager.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import LeavePolicyList from './LeavePolicyList';
import LeavePolicyForm from './LeavePolicyForm';

const LeavePolicyManager = () => {
  const [policies, setPolicies] = useState([]);
  const [selectedPolicy, setSelectedPolicy] = useState(null);
  const token = localStorage.getItem('token');

  const fetchPolicies = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/admin/leave-policies', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setPolicies(response.data);
    } catch (err) {
      console.error('Failed to fetch policies', err);
    }
  };

  useEffect(() => {
    fetchPolicies();
  }, []);

  const handleSave = (savedPolicy) => {
    fetchPolicies(); // Refresh list after create/update
    setSelectedPolicy(null); // Close form
  };

  const handleEdit = (policy) => {
    setSelectedPolicy(policy);
  };

  const handleDelete = async (policyId) => {
    try {
      await axios.delete(`http://localhost:8080/api/admin/leave-policies/${policyId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchPolicies();
    } catch (err) {
      alert('Failed to delete policy');
    }
  };

  return (
    <div>
      <h3>Leave Policy Management</h3>
      <LeavePolicyList
        policies={policies}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
      <LeavePolicyForm
        policy={selectedPolicy}
        onSaved={handleSave}
        onCancel={() => setSelectedPolicy(null)}
      />
    </div>
  );
};

export default LeavePolicyManager;
