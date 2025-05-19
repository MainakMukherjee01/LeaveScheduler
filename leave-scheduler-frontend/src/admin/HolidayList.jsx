import React from 'react';
import './HolidayForm.css'; // Reuse styles

const HolidayList = ({ holidays, onEdit, onDelete }) => {
  return (
    <div className="table-responsive">
      <table className="table table-bordered table-striped table-hover">
        <thead className="table-primary">
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
                <button className="btn btn-sm btn-outline-primary" onClick={() => onEdit(h)}>Edit</button>
                <button className="btn btn-sm btn-outline-danger ms-2" onClick={() => onDelete(h.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default HolidayList;
