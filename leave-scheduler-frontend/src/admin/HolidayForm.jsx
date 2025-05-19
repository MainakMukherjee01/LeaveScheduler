
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const HolidayForm = ({ holiday, onSaved, onCancel }) => {
  const token = localStorage.getItem('token');
  const [form, setForm] = useState({
    name: '',
    date: '',
    description: ''
  });

  const [loading, setLoading] = useState(false);

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
    setLoading(true);
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
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4">
      <div className="card shadow-sm">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">{holiday ? 'Edit Holiday' : 'Add New Holiday'}</h5>
        </div>
        <div className="card-body">
          <form onSubmit={handleSubmit} className="needs-validation" noValidate>
            <div className="mb-3">
              <label htmlFor="name" className="form-label">Holiday Name</label>
              <input
                type="text"
                className="form-control"
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="date" className="form-label">Date</label>
              <input
                type="date"
                className="form-control"
                id="date"
                name="date"
                value={form.date}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="description" className="form-label">Description</label>
              <textarea
                className="form-control"
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                rows="3"
              />
            </div>

            <div className="d-flex justify-content-end">
              {holiday && (
                <button
                  type="button"
                  className="btn btn-secondary me-2"
                  onClick={onCancel}
                >
                  Cancel
                </button>
              )}
              <button type="submit" className="btn btn-success" disabled={loading}>
                {loading ? 'Saving...' : holiday ? 'Update Holiday' : 'Create Holiday'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default HolidayForm;
