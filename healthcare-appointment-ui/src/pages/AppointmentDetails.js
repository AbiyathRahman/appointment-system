// src/pages/AppointmentDetails.js
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import {
    Typography, Box, Card, CardContent, Button, TextField,
    Grid, Paper, CircularProgress, Alert, Chip, Divider
} from '@mui/material';
import { appointmentService } from '../services/api';
import { useAuth } from '../context/AuthContext';

// Validation schema for notes update
const NotesSchema = Yup.object().shape({
    notes: Yup.string().max(1000, 'Notes must be less than 1000 characters'),
});

const AppointmentDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { isDoctor, isAdmin } = useAuth();
    const [appointment, setAppointment] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [statusUpdateLoading, setStatusUpdateLoading] = useState(false);

    useEffect(() => {
        fetchAppointment();
    }, [id]);

    const fetchAppointment = async () => {
        try {
            setLoading(true);
            const response = await appointmentService.getAppointmentById(id);
            setAppointment(response.data);
        } catch (err) {
            setError('Failed to fetch appointment details');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (newStatus) => {
        try {
            setStatusUpdateLoading(true);
            await appointmentService.updateAppointment(id, {
                ...appointment,
                status: newStatus
            });

            // Refresh appointment data
            fetchAppointment();
        } catch (err) {
            setError('Failed to update appointment status');
            console.error(err);
        } finally {
            setStatusUpdateLoading(false);
        }
    };

    const handleNotesSubmit = async (values, { setSubmitting }) => {
        try {
            await appointmentService.updateAppointment(id, {
                ...appointment,
                notes: values.notes
            });

            // Refresh appointment data
            fetchAppointment();
        } catch (err) {
            setError('Failed to update appointment notes');
            console.error(err);
        } finally {
            setSubmitting(false);
        }
    };

    const formatDateTime = (dateTimeStr) => {
        if (!dateTimeStr) return '';
        const date = new Date(dateTimeStr);
        return date.toLocaleString();
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Alert severity="error" sx={{ mb: 3 }}>
                {error}
            </Alert>
        );
    }

    if (!appointment) {
        return (
            <Typography>Appointment not found</Typography>
        );
    }

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Appointment Details
                </Typography>

                <Button
                    variant="outlined"
                    onClick={() => navigate('/dashboard')}
                >
                    Back to Dashboard
                </Button>
            </Box>

            <Paper sx={{ p: 3, mb: 3 }}>
                <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Basic Information
                                </Typography>
                                <Divider sx={{ mb: 2 }} />

                                <Grid container spacing={2}>
                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            Appointment ID
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Typography variant="body1">
                                            {appointment.id}
                                        </Typography>
                                    </Grid>

                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            Status
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Chip
                                            label={appointment.status}
                                            color={
                                                appointment.status === 'SCHEDULED' ? 'primary' :
                                                    appointment.status === 'COMPLETED' ? 'success' :
                                                        appointment.status === 'CANCELLED' ? 'error' : 'default'
                                            }
                                        />
                                    </Grid>

                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            Date & Time
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Typography variant="body1">
                                            {formatDateTime(appointment.appointmentDateTime)}
                                        </Typography>
                                    </Grid>

                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            End Time
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Typography variant="body1">
                                            {formatDateTime(appointment.endDateTime)}
                                        </Typography>
                                    </Grid>

                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            Doctor
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Typography variant="body1">
                                            Dr. {appointment.doctor.firstName} {appointment.doctor.lastName}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            {appointment.doctor.specialization}
                                        </Typography>
                                    </Grid>

                                    <Grid item xs={4}>
                                        <Typography variant="body2" color="text.secondary">
                                            Patient
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={8}>
                                        <Typography variant="body1">
                                            {appointment.patient.firstName} {appointment.patient.lastName}
                                        </Typography>
                                    </Grid>
                                </Grid>
                            </CardContent>
                        </Card>
                    </Grid>

                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Appointment Details
                                </Typography>
                                <Divider sx={{ mb: 2 }} />

                                <Typography variant="body2" color="text.secondary">
                                    Reason for Appointment
                                </Typography>
                                <Typography variant="body1" paragraph>
                                    {appointment.reason}
                                </Typography>

                                <Typography variant="body2" color="text.secondary">
                                    Notes
                                </Typography>
                                <Typography variant="body1" paragraph>
                                    {appointment.notes || 'No notes available'}
                                </Typography>

                                <Typography variant="body2" color="text.secondary">
                                    Created At
                                </Typography>
                                <Typography variant="body1" paragraph>
                                    {formatDateTime(appointment.createdAt)}
                                </Typography>

                                <Typography variant="body2" color="text.secondary">
                                    Last Updated
                                </Typography>
                                <Typography variant="body1">
                                    {formatDateTime(appointment.updatedAt)}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            </Paper>

            {/* Status Update Options (for Doctors and Admin) */}
            {(isDoctor || isAdmin) && appointment.status === 'SCHEDULED' && (
                <Paper sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Update Appointment Status
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 2 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            disabled={statusUpdateLoading}
                            onClick={() => handleStatusUpdate('IN_PROGRESS')}
                        >
                            {statusUpdateLoading ? <CircularProgress size={24} /> : 'Start Appointment'}
                        </Button>

                        <Button
                            variant="contained"
                            color="success"
                            disabled={statusUpdateLoading}
                            onClick={() => handleStatusUpdate('COMPLETED')}
                        >
                            {statusUpdateLoading ? <CircularProgress size={24} /> : 'Complete Appointment'}
                        </Button>

                        <Button
                            variant="contained"
                            color="error"
                            disabled={statusUpdateLoading}
                            onClick={() => handleStatusUpdate('CANCELLED')}
                        >
                            {statusUpdateLoading ? <CircularProgress size={24} /> : 'Cancel Appointment'}
                        </Button>
                    </Box>
                </Paper>
            )}

            {/* Patient Can Cancel Scheduled Appointments */}
            {!isDoctor && !isAdmin && appointment.status === 'SCHEDULED' && (
                <Paper sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Appointment Actions
                    </Typography>
                    <Button
                        variant="contained"
                        color="error"
                        disabled={statusUpdateLoading}
                        onClick={() => {
                            if (window.confirm('Are you sure you want to cancel this appointment?')) {
                                handleStatusUpdate('CANCELLED');
                            }
                        }}
                    >
                        {statusUpdateLoading ? <CircularProgress size={24} /> : 'Cancel Appointment'}
                    </Button>
                </Paper>
            )}

            {/* Notes Update (for Doctors only) */}
            {isDoctor && (
                <Paper sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Update Appointment Notes
                    </Typography>

                    <Formik
                        initialValues={{ notes: appointment.notes || '' }}
                        validationSchema={NotesSchema}
                        onSubmit={handleNotesSubmit}
                    >
                        {({ errors, touched, isSubmitting }) => (
                            <Form>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="notes"
                                            label="Medical Notes"
                                            multiline
                                            rows={4}
                                            error={touched.notes && !!errors.notes}
                                            helperText={touched.notes && errors.notes}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                                            <Button
                                                type="submit"
                                                variant="contained"
                                                disabled={isSubmitting}
                                            >
                                                {isSubmitting ? <CircularProgress size={24} /> : 'Save Notes'}
                                            </Button>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Form>
                        )}
                    </Formik>
                </Paper>
            )}
        </Box>
    );
};

export default AppointmentDetails;