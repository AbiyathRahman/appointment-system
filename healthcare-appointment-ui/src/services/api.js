import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with defaults
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add request interceptor to include auth token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        console.log('Request config:', config); // Debug line
        return config;
    },
    (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
    }
);

// Add response interceptor to handle errors
api.interceptors.response.use(
    (response) => {
        console.log('Response received:', response); // Debug line
        return response;
    },
    (error) => {
        console.error('Response error:', error); // Debug line

        // Handle 401 (Unauthorized) - redirect to login
        if (error.response && error.response.status === 401) {
            console.log('Unauthorized access - removing token and redirecting');
            localStorage.removeItem('token');
            window.location.href = '/login';
        }

        return Promise.reject(error);
    }
);


// Auth services
export const authService ={
    login: (credentials) =>api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData)
};

// User Service - Fix the endpoint path
export const userService = {
    getCurrentUser: () => api.get('/user/me'), // This matches your controller mapping
    updateProfile: (userData) => api.put('/user/me', userData)
};


// Doctor Service
export const doctorService = {
    getAllDoctors: () => api.get('/doctors'),
    getDoctorsById: (id) => api.get(`/doctors/${id}`),
    getDoctorAvailabilities: (id) => api.get(`/availabilities/doctors/${id}`),
};



// Appointment Service
export const appointmentService = {
    getAppointments: () => api.get('/appointments'),
    getAppointmentsByPatient: () => api.get('/appointments/patient'),
    getAppointmentsByDoctor: () => api.get('/appointments/doctor'),
    getAppointmentById: (id) => api.get(`/appointments/${id}`),
    createAppointment: (appointment) => api.post('/appointments', appointment),
    updateAppointment: (appointment, id) => api.put(`/appointments/${id}`, appointment),
    cancelAppointment: (id) => api.put(`/appointments/${id}/status`, {status: 'CANCELLED'}),
    getAvailableTimeSlots: (doctorId, date) => api.get(`/availabilities/doctor/${doctorId}/slots`, {params: {date}}),
};

// Availability Service
export const availabilityService = {
    getAvailabilities: () => api.get('/availabilities'),
    createAvailability: (availability) => api.post('/availabilities', availability),
    updateAvailability: (availability, id) => api.put(`/availabilities/${id}`, availability),
    deleteAvailability: (id) => api.delete(`/availabilities/${id}`),
};

export default api;