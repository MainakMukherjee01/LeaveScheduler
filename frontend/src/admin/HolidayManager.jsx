// src/admin/HolidayManager.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import HolidayForm from './HolidayForm';
import HolidayList from './HolidayList';

const HolidayManager = () => {
  const [holidays, setHolidays] = useState([]);
  const [selectedHoliday, setSelectedHoliday] = useState(null);
  const token = localStorage.getItem('token');

  const fetchHolidays = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/holidays/', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setHolidays(res.data);
    } catch (err) {
      alert('Failed to fetch holidays');
    }
  };

  useEffect(() => {
    fetchHolidays();
  }, []);

  const handleSaved = () => {
    fetchHolidays();
    setSelectedHoliday(null);
  };

  const handleEdit = (holiday) => {
    setSelectedHoliday(holiday);
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/holidays/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchHolidays();
    } catch (err) {
      alert('Failed to delete holiday');
    }
  };

  return (
    <div>
      <h3>Holiday Management</h3>
      <HolidayList holidays={holidays} onEdit={handleEdit} onDelete={handleDelete} />
      <HolidayForm holiday={selectedHoliday} onSaved={handleSaved} onCancel={() => setSelectedHoliday(null)} />
    </div>
  );
};

export default HolidayManager;
