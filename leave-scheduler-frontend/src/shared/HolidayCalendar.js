//import React, { useEffect, useState } from 'react';
//import axios from 'axios';
//import Calendar from 'react-calendar';
//import 'react-calendar/dist/Calendar.css';
//import '../admin/HolidayForm.css';
//
//const HolidayCalendar = () => {
//  const [holidays, setHolidays] = useState([]);
//  const [leaveDates, setLeaveDates] = useState([]);
//  const token = localStorage.getItem('token');
//
//  useEffect(() => {
//    const fetchHolidaysAndLeaves = async () => {
//      try {
//        const [holidayRes, leaveRes] = await Promise.all([
//          axios.get('http://localhost:8080/api/holidays/', {
//            headers: { Authorization: `Bearer ${token}` }
//          }),
//          axios.get('http://localhost:8080/api/leave-applications/history', {
//            headers: { Authorization: `Bearer ${token}` }
//          })
//        ]);
//
//        const holidayDates = holidayRes.data.map(h => new Date(h.date));
//        const leaveDatesList = leaveRes.data.map(l => {
//          const start = new Date(l.startDate);
//          const end = new Date(l.endDate);
//          const range = [];
//          while (start <= end) {
//            range.push(new Date(start));
//            start.setDate(start.getDate() + 1);
//          }
//          return range;
//        }).flat();
//
//        setHolidays(holidayDates);
//        setLeaveDates(leaveDatesList);
//      } catch (err) {
//        console.error('Failed to load data:', err);
//      }
//    };
//
//    fetchHolidaysAndLeaves();
//  }, [token]);
//
//  const tileClassName = ({ date }) => {
//    const isHoliday = holidays.some(d => d.toDateString() === date.toDateString());
//    const isLeave = leaveDates.some(d => d.toDateString() === date.toDateString());
//
//    if (isHoliday && isLeave) return 'highlight-both';
//    if (isHoliday) return 'highlight-holiday';
//    if (isLeave) return 'highlight-leave';
//    return null;
//  };
//
//  return (
//    <div
//      className="holiday-page"
//      style={{
//        display: 'flex',
//        justifyContent: 'center',
//        alignItems: 'flex-start',
//        padding: '80px 0',
//        marginRight: 0,
//        overflowX: 'hidden',
//      }}
//    >
//      <div
//        className="holiday-card"
//        style={{
//          padding: '30px',
//          maxWidth: '100%',
//          boxSizing: 'border-box',
//        }}
//      >
//        <h3 className="text-center mb-4">📅 Holiday & Leave Calendar</h3>
//        <Calendar tileClassName={tileClassName} />
//      </div>
//    </div>
//  );
//};
//
//export default HolidayCalendar;
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../admin/HolidayForm.css';  // Assuming your highlight CSS is here or import your CSS

function isSameDay(d1, d2) {
  return (
    d1.getFullYear() === d2.getFullYear() &&
    d1.getMonth() === d2.getMonth() &&
    d1.getDate() === d2.getDate()
  );
}

const HolidayCalendar = ({ refreshSignal }) => {
  const [holidays, setHolidays] = useState([]);
  const [leaveDates, setLeaveDates] = useState([]);
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchHolidaysAndLeaves = async () => {
      try {
        const [holidayRes, leaveRes] = await Promise.all([
          axios.get('http://localhost:8080/api/holidays/', {
            headers: { Authorization: `Bearer ${token}` }
          }),
          axios.get('http://localhost:8080/api/leave-applications/history', {
            headers: { Authorization: `Bearer ${token}` }
          })
        ]);

        const holidayDates = holidayRes.data.map(h => new Date(h.date));

        const leaveDatesList = leaveRes.data.flatMap(l => {
          const startDate = new Date(l.startDate);
          const endDate = new Date(l.endDate);
          const range = [];
          let current = new Date(startDate);

          while (current <= endDate) {
            range.push(new Date(current));
            current.setDate(current.getDate() + 1);
          }
          return range;
        });

        setHolidays(holidayDates);
        setLeaveDates(leaveDatesList);
      } catch (err) {
        console.error('Failed to load holidays or leaves:', err);
      }
    };

    fetchHolidaysAndLeaves();
  }, [token, refreshSignal]); // Re-fetch when refreshSignal changes

  const tileClassName = ({ date }) => {
    const isHoliday = holidays.some(d => isSameDay(d, date));
    const isLeave = leaveDates.some(d => isSameDay(d, date));

    if (isHoliday && isLeave) return 'highlight-both';
    if (isHoliday) return 'highlight-holiday';
    if (isLeave) return 'highlight-leave';
    return null;
  };

  return (
    <div
      className="holiday-page"
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'flex-start',
        padding: '80px 0',
        marginRight: 0,
        overflowX: 'hidden',
      }}
    >
      <div
        className="holiday-card"
        style={{
          padding: '30px',
          maxWidth: '100%',
          boxSizing: 'border-box',
        }}
      >
        <h3 className="text-center mb-4">📅 Holiday & Leave Calendar</h3>
        <Calendar tileClassName={tileClassName} />
      </div>
    </div>
  );
};

export default HolidayCalendar;
