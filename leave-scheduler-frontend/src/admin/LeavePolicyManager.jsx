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

  const handleSave = () => {
    fetchPolicies();
    setSelectedPolicy(null);
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
    <div style={styles.wrapper}>
      <h3 style={styles.heading}>Leave Policy Management</h3>
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

const styles = {
  wrapper: {
    padding: '30px',
    backgroundImage: 'url("https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1600&q=80")',
    backgroundSize: 'cover',
    minHeight: '100vh',
    backgroundAttachment: 'fixed'
  },
  heading: {
    textAlign: 'center',
    color: 'white',
    marginBottom: '20px'
  }
};

export default LeavePolicyManager;

