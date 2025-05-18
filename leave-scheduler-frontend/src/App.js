import React, { useState } from 'react';
import LoginPage from './LoginPage';
import RegisterPage from './RegisterPage';
import AdminPanel from './admin/AdminPanel';
import ManagerPanel from './manager/ManagerPanel';

function App() {
  const [user, setUser] = useState(null);
  const [page, setPage] = useState('login'); // 'login' | 'register'

  const handleLogin = (userData) => {
    setUser(userData);
    setPage('dashboard');
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setPage('login');
  };

  const handleRegisterRedirect = () => {
    setPage('register');
  };

  const handleRegisterSuccess = () => {
    setPage('login');
  };

  if (!user) {
    return (
      <>
        {page === 'login' ? (
          <LoginPage onLogin={handleLogin} onRegister={handleRegisterRedirect} />
        ) : (
          <RegisterPage onRegisterSuccess={handleRegisterSuccess} />
        )}
      </>
    );
  }

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

const styles = {
  logoutButton: {
    padding: '10px 15px',
    backgroundColor: '#f44336',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer'
  }
};

export default App;
