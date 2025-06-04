// src/pages/AppointmentBooking.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {
    Typography, Box, Button, TextField, MenuItem,
    FormControl, FormHelperText, Grid, Paper,
    CircularProgress, Alert
} from '@mui/material';
import { doctorService, availabilityService, appointmentService } from '../services/api';

// Validation schema
const AppointmentSchema = Yup.object().shape({
    doctorId: Yup.number().required('Doctor is required'),
    reason: Yup.string()
        .min(3, 'Reason must be at least 3 characters')
        .max(255, 'Reason must be less than 255 characters')
        .required('Reason is required'),
    notes: Yup.string().max(1000, 'Notes must be less than 1000 characters'),
});

const AppointmentBooking = () => {
    const navigate = useNavigate();
    const [doctors, setDoctors] = useState([]);
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [availableSlots, setAvailableSlots] = useState([]);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Fetch all doctors when component mounts
    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const response = await doctorService.getAllDoctors();
                setDoctors(response.data);
            } catch (err) {
                setError('Failed to fetch doctors');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchDoctors().then(r => {});
    }, []);

    // Fetch available slots when doctor and date change
    const fetchAvailableSlots = async (doctorId, date) => {
        if (!doctorId || !date) return;

        try {
            setLoading(true);
            // Format date as ISO string (YYYY-MM-DD)
            const formattedDate = date.toISOString().split('T')[0];

            const response = await availabilityService.getAvailabilities(
                doctorId, formattedDate
            );

            setAvailableSlots(response.data || []);
            setSelectedSlot(null); // Reset selected slot
        } catch (err) {
            setError('Failed to fetch available slots');
            console.error(err);
            setAvailableSlots([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        if (!selectedSlot) {
            setStatus({ error: 'Please select a time slot' });
            setSubmitting(false);
            return;
        }

        try {
            await appointmentService.createAppointment({
                doctorId: values.doctorId,
                appointmentDateTime: selectedSlot.startTime,
                reason: values.reason,
                notes: values.notes
            });

            navigate('/dashboard', { state: { success: 'Appointment booked successfully' } });
        } catch (err) {
            setStatus({
                error: err.response?.data?.message || 'Failed to book appointment'
            });
            console.error(err);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Book an Appointment
            </Typography>

            <Paper sx={{ p: 3, mb: 3 }}>
                <Formik
                    initialValues={{ doctorId: '', reason: '', notes: '' }}
                    validationSchema={AppointmentSchema}
                    onSubmit={handleSubmit}
                >
                    {({ values, errors, touched, status, isSubmitting, setFieldValue }) => (
                        <Form>
                            {status && status.error && (
                                <Alert severity="error" sx={{ mb: 2 }}>
                                    {status.error}
                                </Alert>
                            )}

                            <Grid container spacing={2}>
                                {/* Doctor Selection */}
                                <Grid item xs={12} md={6}>
                                    <Field
                                        as={TextField}
                                        select
                                        fullWidth
                                        name="doctorId"
                                        label="Select Doctor"
                                        value={values.doctorId}
                                        onChange={(e) => {
                                            setFieldValue('doctorId', e.target.value);
                                            fetchAvailableSlots(e.target.value, selectedDate);
                                        }}
                                        error={touched.doctorId && !!errors.doctorId}
                                        helperText={touched.doctorId && errors.doctorId}
                                    >
                                        <MenuItem value="">-- Select a Doctor --</MenuItem>
                                        {doctors.map((doctor) => (
                                            <MenuItem key={doctor.id} value={doctor.id}>
                                                Dr. {doctor.firstName} {doctor.lastName} ({doctor.specialization})
                                            </MenuItem>
                                        ))}
                                    </Field>
                                </Grid>

                                {/* Date Selection */}
                                <Grid item xs={12} md={6}>
                                    <FormControl fullWidth error={!selectedDate}>
                                        <Typography variant="body2" sx={{ mb: 1 }}>
                                            Select Date
                                        </Typography>
                                        <DatePicker
                                            selected={selectedDate}
                                            onChange={(date) => {
                                                setSelectedDate(date);
                                                if (values.doctorId) {
                                                    fetchAvailableSlots(values.doctorId, date);
                                                }
                                            }}
                                            minDate={new Date()}
                                            dateFormat="MMMM d, yyyy"
                                            className="form-control"
                                        />
                                        {!selectedDate && (
                                            <FormHelperText>Date is required</FormHelperText>
                                        )}
                                    </FormControl>
                                </Grid>

                                {/* Time Slots */}
                                <Grid item xs={12}>
                                    <Typography variant="body2" sx={{ mb: 1 }}>
                                        Select Time Slot
                                    </Typography>

                                    {loading ? (
                                        <CircularProgress size={24} />
                                    ) : availableSlots.length === 0 ? (
                                        <Typography color="text.secondary">
                                            No available time slots for the selected date
                                        </Typography>
                                    ) : (
                                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                            {availableSlots.map((slot, index) => {
                                                const startTime = new Date(slot.startTime);
                                                return (
                                                    <Button
                                                        key={index}
                                                        variant={selectedSlot === slot ? "contained" : "outlined"}
                                                        onClick={() => setSelectedSlot(slot)}
                                                        sx={{ minWidth: '100px' }}
                                                    >
                                                        {startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                                    </Button>
                                                );
                                            })}
                                        </Box>
                                    )}
                                </Grid>

                                {/* Reason */}
                                <Grid item xs={12}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="reason"
                                        label="Reason for Appointment"
                                        multiline
                                        rows={2}
                                        error={touched.reason && !!errors.reason}
                                        helperText={touched.reason && errors.reason}
                                    />
                                </Grid>

                                {/* Notes */}
                                <Grid item xs={12}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="notes"
                                        label="Additional Notes (Optional)"
                                        multiline
                                        rows={3}
                                        error={touched.notes && !!errors.notes}
                                        helperText={touched.notes && errors.notes}
                                    />
                                </Grid>

                                {/* Submit Button */}
                                <Grid item xs={12}>
                                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                                        <Button
                                            variant="outlined"
                                            onClick={() => navigate('/dashboard')}
                                        >
                                            Cancel
                                        </Button>
                                        <Button
                                            type="submit"
                                            variant="contained"
                                            disabled={isSubmitting || !selectedSlot}
                                        >
                                            {isSubmitting ? <CircularProgress size={24} /> : 'Book Appointment'}
                                        </Button>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Form>
                    )}
                </Formik>
            </Paper>
        </Box>
    );
};

export default AppointmentBooking;