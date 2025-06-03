// src/pages/DoctorAvailability.js
import React, { useState, useEffect } from 'react';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import {
    Typography, Box, Card, CardContent, Button, TextField,
    MenuItem, Grid, Paper, CircularProgress, Alert,
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, IconButton, Tabs, Tab
} from '@mui/material';
import { Delete as DeleteIcon, Edit as EditIcon } from '@mui/icons-material';
import { availabilityService } from '../services/api';
import { useAuth } from '../context/AuthContext';

// Validation schema
const AvailabilitySchema = Yup.object().shape({
    dayOfWeek: Yup.string().when('specificDate', {
        is: undefined,
        then: Yup.string().required('Either day of week or specific date is required')
    }),
    startTime: Yup.string().required('Start time is required'),
    endTime: Yup.string().required('End time is required'),
    slotDuration: Yup.number()
        .min(15, 'Slot must be at least 15 minutes')
        .max(120, 'Slot must be at most 120 minutes')
        .required('Slot duration is required'),
});

const DoctorAvailability = () => {
    const { currentUser } = useAuth();
    const [availabilities, setAvailabilities] = useState([]);
    const [editingAvailability, setEditingAvailability] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [tabValue, setTabValue] = useState(0);

    useEffect(() => {
        fetchAvailabilities();
    }, []);

    const fetchAvailabilities = async () => {
        try {
            setLoading(true);
            const response = await availabilityService.getDoctorAvailabilities(currentUser.id);
            setAvailabilities(response.data);
        } catch (err) {
            setError('Failed to fetch availabilities');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this availability?')) {
            try {
                await availabilityService.deleteAvailability(id);
                setAvailabilities(availabilities.filter(a => a.id !== id));
            } catch (err) {
                setError('Failed to delete availability');
                console.error(err);
            }
        }
    };

    const handleEdit = (availability) => {
        setEditingAvailability(availability);
        setTabValue(0); // Switch to form tab
    };

    const handleSubmit = async (values, { setSubmitting, resetForm, setStatus }) => {
        try {
            const availabilityData = {
                ...values,
                doctorId: currentUser.id
            };

            if (editingAvailability) {
                await availabilityService.updateAvailability(
                    editingAvailability.id, availabilityData
                );
            } else {
                await availabilityService.createAvailability(availabilityData);
            }

            resetForm();
            setEditingAvailability(null);
            fetchAvailabilities();
            setTabValue(1); // Switch to list tab
        } catch (err) {
            setStatus({
                error: err.response?.data?.message || 'Failed to save availability'
            });
        } finally {
            setSubmitting(false);
        }
    };

    const weekdays = [
        { value: 'MONDAY', label: 'Monday' },
        { value: 'TUESDAY', label: 'Tuesday' },
        { value: 'WEDNESDAY', label: 'Wednesday' },
        { value: 'THURSDAY', label: 'Thursday' },
        { value: 'FRIDAY', label: 'Friday' },
        { value: 'SATURDAY', label: 'Saturday' },
        { value: 'SUNDAY', label: 'Sunday' }
    ];

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
        if (newValue === 0) {
            // Reset form when switching to the form tab
            setEditingAvailability(null);
        }
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Manage Availability
            </Typography>

            <Tabs value={tabValue} onChange={handleTabChange} sx={{ mb: 2 }}>
                <Tab label="Add/Edit Availability" />
                <Tab label="View All Availabilities" />
            </Tabs>

            {/* Form Tab */}
            {tabValue === 0 && (
                <Paper sx={{ p: 3, mb: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        {editingAvailability ? 'Edit Availability' : 'Add New Availability'}
                    </Typography>

                    <Formik
                        initialValues={{
                            dayOfWeek: editingAvailability?.dayOfWeek || '',
                            specificDate: editingAvailability?.specificDate || '',
                            startTime: editingAvailability?.startTime || '',
                            endTime: editingAvailability?.endTime || '',
                            slotDuration: editingAvailability?.slotDuration || 30,
                            available: editingAvailability?.available !== false,
                            notes: editingAvailability?.notes || ''
                        }}
                        enableReinitialize
                        validationSchema={AvailabilitySchema}
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
                                    <Grid item xs={12} md={6}>
                                        <Field
                                            as={TextField}
                                            select
                                            fullWidth
                                            name="dayOfWeek"
                                            label="Day of Week (for recurring)"
                                            value={values.dayOfWeek}
                                            error={touched.dayOfWeek && !!errors.dayOfWeek}
                                            helperText={touched.dayOfWeek && errors.dayOfWeek}
                                            disabled={!!values.specificDate}
                                        >
                                            <MenuItem value="">-- Select Day --</MenuItem>
                                            {weekdays.map((day) => (
                                                <MenuItem key={day.value} value={day.value}>
                                                    {day.label}
                                                </MenuItem>
                                            ))}
                                        </Field>
                                    </Grid>

                                    <Grid item xs={12} md={6}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="specificDate"
                                            label="Specific Date (for non-recurring)"
                                            type="date"
                                            InputLabelProps={{ shrink: true }}
                                            error={touched.specificDate && !!errors.specificDate}
                                            helperText={touched.specificDate && errors.specificDate}
                                            disabled={!!values.dayOfWeek}
                                            onChange={(e) => {
                                                if (e.target.value) {
                                                    setFieldValue('dayOfWeek', '');
                                                }
                                                setFieldValue('specificDate', e.target.value);
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12} md={4}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="startTime"
                                            label="Start Time"
                                            type="time"
                                            InputLabelProps={{ shrink: true }}
                                            error={touched.startTime && !!errors.startTime}
                                            helperText={touched.startTime && errors.startTime}
                                        />
                                    </Grid>

                                    <Grid item xs={12} md={4}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="endTime"
                                            label="End Time"
                                            type="time"
                                            InputLabelProps={{ shrink: true }}
                                            error={touched.endTime && !!errors.endTime}
                                            helperText={touched.endTime && errors.endTime}
                                        />
                                    </Grid>

                                    <Grid item xs={12} md={4}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="slotDuration"
                                            label="Slot Duration (minutes)"
                                            type="number"
                                            InputProps={{ inputProps: { min: 15, step: 5 } }}
                                            error={touched.slotDuration && !!errors.slotDuration}
                                            helperText={touched.slotDuration && errors.slotDuration}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <Field
                                            as={TextField}
                                            fullWidth
                                            name="notes"
                                            label="Notes (Optional)"
                                            multiline
                                            rows={2}
                                            error={touched.notes && !!errors.notes}
                                            helperText={touched.notes && errors.notes}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                                            <Button
                                                variant="outlined"
                                                onClick={() => {
                                                    setEditingAvailability(null);
                                                    setTabValue(1);
                                                }}
                                            >
                                                Cancel
                                            </Button>
                                            <Button
                                                type="submit"
                                                variant="contained"
                                                disabled={isSubmitting}
                                            >
                                                {isSubmitting ? <CircularProgress size={24} /> : 'Save Availability'}
                                            </Button>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Form>
                        )}
                    </Formik>
                </Paper>
            )}

            {/* List Tab */}
            {tabValue === 1 && (
                <Paper sx={{ p: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Your Availability Schedule
                    </Typography>

                    {loading ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                            <CircularProgress />
                        </Box>
                    ) : error ? (
                        <Alert severity="error">{error}</Alert>
                    ) : availabilities.length === 0 ? (
                        <Typography color="text.secondary">
                            No availabilities found. Add your first availability schedule.
                        </Typography>
                    ) : (
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Day/Date</TableCell>
                                        <TableCell>Time</TableCell>
                                        <TableCell>Duration</TableCell>
                                        <TableCell>Status</TableCell>
                                        <TableCell>Notes</TableCell>
                                        <TableCell>Actions</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {availabilities.map((availability) => (
                                        <TableRow key={availability.id}>
                                            <TableCell>
                                                {availability.specificDate
                                                    ? new Date(availability.specificDate).toLocaleDateString()
                                                    : availability.dayOfWeek
                                                }
                                            </TableCell>
                                            <TableCell>
                                                {availability.startTime} - {availability.endTime}
                                            </TableCell>
                                            <TableCell>{availability.slotDuration} min</TableCell>
                                            <TableCell>
                                                {availability.available ? 'Available' : 'Unavailable'}
                                            </TableCell>
                                            <TableCell>{availability.notes || '-'}</TableCell>
                                            <TableCell>
                                                <IconButton
                                                    color="primary"
                                                    onClick={() => handleEdit(availability)}
                                                >
                                                    <EditIcon />
                                                </IconButton>
                                                <IconButton
                                                    color="error"
                                                    onClick={() => handleDelete(availability.id)}
                                                >
                                                    <DeleteIcon />
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}

                    <Box sx={{ mt: 2 }}>
                        <Button
                            variant="contained"
                            onClick={() => setTabValue(0)}
                        >
                            Add New Availability
                        </Button>
                    </Box>
                </Paper>
            )}
        </Box>
    );
};

export default DoctorAvailability;