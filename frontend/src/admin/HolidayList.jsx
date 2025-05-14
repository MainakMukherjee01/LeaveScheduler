// src/admin/HolidayList.jsx
import React from 'react';

const HolidayList = ({ holidays, onEdit, onDelete }) => {
  return (
    <table border="1" cellPadding="10" style={{ width: '100%', marginTop: '10px' }}>
      <thead>
        <tr>
          <th>ID</th><th>Name</th><th>Date</th><th>Description</th><th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {holidays.map((h) => (
          <tr key={h.id}>
            <td>{h.id}</td>
            <td>{h.name}</td>
            <td>{h.date}</td>
            <td>{h.description}</td>
            <td>
              <button onClick={() => onEdit(h)}>Edit</button>
              <button onClick={() => onDelete(h.id)} style={{ color: 'red', marginLeft: '10px' }}>Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default HolidayList;
