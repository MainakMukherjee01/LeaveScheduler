// src/shared/HolidayCalendar.jsx
import React, { useEffect, useState } from 'react';
import axios from 'axios';

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
    <div>
      <h3>Holiday Calendar</h3>
      <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
        <thead>
          <tr>
            <th>Date</th><th>Name</th><th>Description</th>
          </tr>
        </thead>
        <tbody>
          {holidays.map((h) => (
            <tr key={h.id}>
              <td>{h.date}</td>
              <td>{h.name}</td>
              <td>{h.description}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default HolidayCalendar;
