import React, { createContext, useState, useEffect, useContext } from 'react';
import {jwtDecode} from 'jwt-decode';
import { authService, userService } from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Check if token exists and is valid on startup
  useEffect(() => {
    const checkLoggedIn = async () => {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          // Check if token is expired
          const decodedToken = jwtDecode(token);
          const currentTime = Date.now() / 1000;
          
          if (decodedToken.exp < currentTime) {
            // Token expired
            localStorage.removeItem('token');
            setCurrentUser(null);
          } else {
            // Token valid, fetch user data
            const response = await userService.getCurrentUser();
            setCurrentUser(response.data);
          }
        }
      } catch (error) {
        console.error('Authentication error:', error);
        localStorage.removeItem('token');
      } finally {
        setLoading(false);
      }
    };
    
    checkLoggedIn();
  }, []);
  
  // Login function
  const login = async (username, password) => {
    try {
      const response = await authService.login({ username, password });
      localStorage.setItem('token', response.data.token);
      setCurrentUser(response.data);
      return response.data;
    } catch (error) {
      throw error;
    }
  };
  
  // Register function
  const register = async (userData) => {
    return await authService.register(userData);
  };
  
  // Logout function
  const logout = () => {
    localStorage.removeItem('token');
    setCurrentUser(null);
  };
  
  const value = {
    currentUser,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!currentUser,
    isDoctor: currentUser?.role === 'ROLE_DOCTOR',
    isPatient: currentUser?.role === 'ROLE_PATIENT',
    isAdmin: currentUser?.role === 'ROLE_ADMIN',
  };
  
  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

// Custom hook to use auth context
export const useAuth = () => useContext(AuthContext);