// src/App.js
import React, { useState } from 'react';
import LoginPage from './LoginPage';

function App() {
  const [user, setUser] = useState(null);

  const handleLogin = (userData) => {
    setUser(userData);
  };

  return (
    <div>
      {user ? (
        <h2>Welcome, {user.user}!</h2>
      ) : (
        <LoginPage onLogin={handleLogin} />
      )}
    </div>
  );
}

export default App;
