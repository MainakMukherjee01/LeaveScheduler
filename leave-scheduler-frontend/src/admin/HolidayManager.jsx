import React, { useEffect, useState } from 'react';
import axios from 'axios';
import HolidayForm from './HolidayForm';
import './HolidayForm.css'; // or SharedStyles if applicable

const HolidayManager = () => {
  const [holidays, setHolidays] = useState([]);
  const [selectedHoliday, setSelectedHoliday] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
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
    setSuccessMessage('✅ Holiday added successfully!');

    // Hide the message after 3 seconds
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
  };

  return (
    <div className="holiday-page">
      <div className="holiday-card" style={{ padding: '30px' }}>
        <h3 className="text-center mb-4">Holiday Management</h3>

        {successMessage && (
          <div className="alert alert-success text-center" role="alert">
            {successMessage}
          </div>
        )}

        <HolidayForm
          holiday={selectedHoliday}
          onSaved={handleSaved}
          onCancel={() => setSelectedHoliday(null)}
        />
      </div>
    </div>
  );
};

export default HolidayManager;
