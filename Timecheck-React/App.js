import React, { useState, useEffect } from 'react';

function TimeCheck() {
  const [currentTime, setCurrentTime] = useState('');

  useEffect(() => {
    const timer = setInterval(() => {
      const now = new Date();
      const timeString = now.toLocaleTimeString();
      setCurrentTime(timeString);
    }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, []);

  return (
    <div className="App">
      <h1>Time Check</h1>
      <p>Current Time: {currentTime}</p>
    </div>
  );
}

export default TimeCheck;
