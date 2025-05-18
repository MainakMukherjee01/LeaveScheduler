import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './HolidayForm.css'; // this should already include your card styles

const HolidayForm = ({ holiday, onSaved, onCancel }) => {
  const [name, setName] = useState('');
  const [date, setDate] = useState('');
  const [description, setDescription] = useState('');
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (holiday) {
      setName(holiday.name);
      setDate(holiday.date);
      setDescription(holiday.description || '');
    } else {
      setName('');
      setDate('');
      setDescription('');
    }
  }, [holiday]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = { name, date, description };

    try {
      if (holiday) {
        await axios.put(`http://localhost:8080/api/holidays/${holiday.id}`, data, {
          headers: { Authorization: `Bearer ${token}` }
        });
      } else {
        await axios.post('http://localhost:8080/api/holidays', data, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
      onSaved();
    } catch (err) {
      alert('Failed to save holiday');
    }
  };

  return (
    <div className="holiday-form-container">
      <div className="holiday-form-card">
        <h4 className="form-title">{holiday ? 'Edit Holiday' : 'Add Holiday'}</h4>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Holiday Name</label>
            <input
              type="text"
              className="form-control"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              placeholder="Enter holiday name"
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Date</label>
            <input
              type="date"
              className="form-control"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Description</label>
            <textarea
              className="form-control"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Optional description"
              rows={3}
            />
          </div>
          <div className="d-flex justify-content-between">
            <button type="submit" className="btn btn-success">
              {holiday ? 'Update Holiday' : 'Add Holiday'}
            </button>
            {holiday && (
              <button type="button" className="btn btn-secondary" onClick={onCancel}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default HolidayForm;
