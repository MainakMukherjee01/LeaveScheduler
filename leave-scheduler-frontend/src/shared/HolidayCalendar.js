// src/shared/HolidayCalendar.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../admin/HolidayForm.css';

const HolidayCalendar = () => {
  const [holidays, setHolidays] = useState([]);
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchHolidays = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/holidays/calendar', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setHolidays(res.data);
      } catch (err) {
        console.error('Failed to load holidays');
      }
    };

    fetchHolidays();
  }, []);

  return (
    <div className="holiday-page">
      <div className="holiday-card" style={{ padding: '30px' }}>
        <h3 className="text-center mb-4">📅 Holiday Calendar</h3>
        <div className="table-responsive">
          <table className="table table-bordered table-striped table-hover">
            <thead className="table-primary">
              <tr>
                <th>Date</th>
                <th>Name</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              {holidays.length === 0 ? (
                <tr>
                  <td colSpan="3" className="text-center text-muted">
                    No holidays found.
                  </td>
                </tr>
              ) : (
                holidays.map((h) => (
                  <tr key={h.id}>
                    <td>{h.date}</td>
                    <td>{h.name}</td>
                    <td>{h.description || '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default HolidayCalendar;
