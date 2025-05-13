// src/admin/LeavePolicyForm.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const LeavePolicyForm = ({ policy, onSaved, onCancel }) => {
  const token = localStorage.getItem('token');

  const initialState = {
    leaveType: '',
    description: '',
    annualCredit: 0,
    minDuration: 0,
    maxDuration: 0,
    noticeRequired: 0,
    isCarryForward: false,
    maxAccumulation: 0,
    applicableRoles: ['EMPLOYEE'],
    isActive: true
  };

  const [form, setForm] = useState(initialState);

  useEffect(() => {
    if (policy) {
      setForm({ ...policy });
    } else {
      setForm(initialState);
    }
  }, [policy]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    const val = type === 'checkbox' ? checked : value;
    setForm((prev) => ({ ...prev, [name]: val }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/admin/leave-policies', form, {
        headers: { Authorization: `Bearer ${token}` }
      });
      onSaved();
    } catch (err) {
      alert('Error saving policy');
    }
  };

  return (
    <div style={{ marginTop: '20px' }}>
      <h4>{policy ? 'Edit Policy' : 'Create New Policy'}</h4>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '10px', maxWidth: '500px' }}>
        <input name="leaveType" value={form.leaveType} onChange={handleChange} placeholder="Leave Type" required />
        <input name="description" value={form.description} onChange={handleChange} placeholder="Description" />
        <input name="annualCredit" type="number" value={form.annualCredit} onChange={handleChange} placeholder="Annual Credit" />
        <input name="minDuration" type="number" value={form.minDuration} onChange={handleChange} placeholder="Min Duration" />
        <input name="maxDuration" type="number" value={form.maxDuration} onChange={handleChange} placeholder="Max Duration" />
        <input name="noticeRequired" type="number" value={form.noticeRequired} onChange={handleChange} placeholder="Notice Required (days)" />
        <input name="maxAccumulation" type="number" value={form.maxAccumulation} onChange={handleChange} placeholder="Max Accumulation" />
        <label>
          Carry Forward:
          <input type="checkbox" name="isCarryForward" checked={form.isCarryForward} onChange={handleChange} />
        </label>
        <label>
          Applicable Roles:
          <select name="applicableRoles" onChange={(e) => setForm({ ...form, applicableRoles: [e.target.value] })}>
            <option value="EMPLOYEE">EMPLOYEE</option>
            <option value="MANAGER">MANAGER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </label>
        <label>
          Active:
          <select name="isActive" value={form.isActive} onChange={handleChange}>
            <option value={true}>Yes</option>
            <option value={false}>No</option>
          </select>
        </label>
        <div>
          <button type="submit">{policy ? 'Update' : 'Create'}</button>
          {policy && <button onClick={onCancel} type="button" style={{ marginLeft: '10px' }}>Cancel</button>}
        </div>
      </form>
    </div>
  );
};

export default LeavePolicyForm;
