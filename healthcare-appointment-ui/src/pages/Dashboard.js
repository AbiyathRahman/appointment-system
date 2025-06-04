// src/pages/Dashboard.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { appointmentService } from '../services/api';
import {
    Typography, Box, Card, CardContent, Grid, Button,
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, Chip
} from '@mui/material';

const Dashboard = () => {
    const { currentUser, isDoctor, isAdmin } = useAuth();
    const navigate = useNavigate();
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchAppointments = async () => {
            try {
                let response;
                if (isDoctor) {
                    response = await appointmentService.getAppointmentsByDoctor(currentUser.id);
                } else if (isAdmin) {
                    response = await appointmentService.getAppointments();
                } else {
                    response = await appointmentService.getAppointmentsByPatient(currentUser.id);
                }

                // Handle both 200 with data and 204 no content
                setAppointments(response.data || []);
            } catch (err) {
                // Only show error for actual errors, not empty results
                if (err.response?.status === 404) {
                    setError('User not found or invalid request');
                } else {
                    setError('Failed to fetch appointments');
                }
                console.error(err);

            } finally {
                setLoading(false);
            }
        };

        fetchAppointments();
    }, [isDoctor, isAdmin]);

    const getStatusChipColor = (status) => {
        switch (status) {
            case 'SCHEDULED': return 'primary';
            case 'COMPLETED': return 'success';
            case 'CANCELLED': return 'error';
            case 'IN_PROGRESS': return 'warning';
            default: return 'default';
        }
    };

    const formatDateTime = (dateTimeStr) => {
        const date = new Date(dateTimeStr);
        return date.toLocaleString();
    };

    const handleBookAppointment = () => {
        navigate('/appointments/book');
    };

    const handleViewAppointment = (id) => {
        navigate(`/appointments/${id}`);
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Dashboard
                </Typography>

                {!isDoctor && !isAdmin && (
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleBookAppointment}
                    >
                        Book New Appointment
                    </Button>
                )}
            </Box>

            <Grid container spacing={4}>
                {/* Summary Cards */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" component="div">
                                Total Appointments
                            </Typography>
                            <Typography variant="h3" component="div">
                                {appointments.length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" component="div">
                                Upcoming Appointments
                            </Typography>
                            <Typography variant="h3" component="div">
                                {appointments.filter(a => a.status === 'SCHEDULED').length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" component="div">
                                Completed Appointments
                            </Typography>
                            <Typography variant="h3" component="div">
                                {appointments.filter(a => a.status === 'COMPLETED').length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Appointments Table */}
                <Grid item xs={12}>
                    <Typography variant="h5" component="h2" gutterBottom>
                        {isDoctor ? 'Your Schedule' : 'Your Appointments'}
                    </Typography>

                    {loading ? (
                        <Typography>Loading appointments...</Typography>
                    ) : error ? (
                        <Typography color="error">{error}</Typography>
                    ) : appointments.length === 0 ? (
                        <Typography>No appointments found.</Typography>
                    ) : (
                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Date & Time</TableCell>
                                        {!isDoctor && <TableCell>Doctor</TableCell>}
                                        {isDoctor && <TableCell>Patient</TableCell>}
                                        <TableCell>Reason</TableCell>
                                        <TableCell>Status</TableCell>
                                        <TableCell>Actions</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {appointments.map((appointment) => (
                                        <TableRow key={appointment.id}>
                                            <TableCell>{formatDateTime(appointment.appointmentDateTime)}</TableCell>
                                            {!isDoctor && (
                                                <TableCell>
                                                    {appointment.doctor.firstName} {appointment.doctor.lastName}
                                                </TableCell>
                                            )}
                                            {isDoctor && (
                                                <TableCell>
                                                    {appointment.patient.firstName} {appointment.patient.lastName}
                                                </TableCell>
                                            )}
                                            <TableCell>{appointment.reason}</TableCell>
                                            <TableCell>
                                                <Chip
                                                    label={appointment.status}
                                                    color={getStatusChipColor(appointment.status)}
                                                />
                                            </TableCell>
                                            <TableCell>
                                                <Button
                                                    variant="outlined"
                                                    size="small"
                                                    onClick={() => handleViewAppointment(appointment.id)}
                                                >
                                                    View
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard;