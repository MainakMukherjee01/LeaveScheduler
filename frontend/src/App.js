import React, { useState } from 'react';
import LoginPage from './LoginPage';
import RegisterPage from './RegisterPage';
import AdminPanel from './admin/AdminPanel';
import ManagerPanel from './manager/ManagerPanel';

function App() {
  const [user, setUser] = useState(null);
  const [page, setPage] = useState('login');

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setPage('login');
  };

  if (user) {
    const isAdmin = user.roles.includes('ADMIN');
    const isManager = user.roles.includes('MANAGER');

    return (
      <div style={{ padding: '20px' }}>
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>Welcome, {user.user}!</h2>
          <button onClick={handleLogout} style={styles.logoutButton}>Logout</button>
        </header>
        <hr />
        {isAdmin && <AdminPanel />}
        {isManager && <ManagerPanel />}
        {!isAdmin && !isManager && (
          <p>You are logged in as EMPLOYEE. No dashboard available yet.</p>
        )}
      </div>
    );
  }

  return (
    <div style={styles.authContainer}>
      <h1>Leave Scheduler</h1>
      <div style={styles.switch}>
        <button onClick={() => setPage('login')}>Login</button>
        <button onClick={() => setPage('register')}>Register</button>
      </div>
      {page === 'login' ? (
        <LoginPage onLogin={handleLogin} />
      ) : (
        <RegisterPage />
      )}
    </div>
  );
}

const styles = {
  authContainer: {
    maxWidth: '500px',
    margin: '40px auto',
    textAlign: 'center',
    backgroundColor: '#f8f8f8',
    padding: '20px',
    borderRadius: '10px'
  },
  switch: {
    marginBottom: '20px',
    display: 'flex',
    justifyContent: 'center',
    gap: '10px'
  },
  logoutButton: {
    padding: '8px 16px',
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px'
  }
};

export default App;
