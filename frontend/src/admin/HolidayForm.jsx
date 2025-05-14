// src/admin/HolidayForm.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const HolidayForm = ({ holiday, onSaved, onCancel }) => {
  const token = localStorage.getItem('token');
  const [form, setForm] = useState({
    name: '',
    date: '',
    description: ''
  });

  useEffect(() => {
    if (holiday) setForm({ ...holiday });
    else setForm({ name: '', date: '', description: '' });
  }, [holiday]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (holiday?.id) {
        await axios.put(`http://localhost:8080/api/holidays/${holiday.id}`, form, {
          headers: { Authorization: `Bearer ${token}` }
        });
      } else {
        await axios.post(`http://localhost:8080/api/holidays`, form, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
      onSaved();
    } catch (err) {
      alert('Failed to save holiday');
    }
  };

  return (
    <div style={{ marginTop: '20px' }}>
      <h4>{holiday ? 'Edit Holiday' : 'Add New Holiday'}</h4>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '10px', maxWidth: '400px' }}>
        <input
          name="name"
          placeholder="Holiday Name"
          value={form.name}
          onChange={handleChange}
          required
        />
        <input
          name="date"
          type="date"
          value={form.date}
          onChange={handleChange}
          required
        />
        <input
          name="description"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
        />
        <div>
          <button type="submit">{holiday ? 'Update' : 'Create'}</button>
          {holiday && <button onClick={onCancel} type="button" style={{ marginLeft: '10px' }}>Cancel</button>}
        </div>
      </form>
    </div>
  );
};

export default HolidayForm;
