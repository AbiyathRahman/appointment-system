// src/pages/UserProfile.js
import React, { useState, useEffect } from 'react';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import {
    Typography, Box, Button, TextField,
    Grid, Paper, CircularProgress, Alert, Divider
} from '@mui/material';
import { userService } from '../services/api';
import { useAuth } from '../context/AuthContext';

// Validation schema
const ProfileSchema = Yup.object().shape({
    email: Yup.string()
        .email('Invalid email address')
        .required('Email is required'),
    firstName: Yup.string()
        .required('First name is required'),
    lastName: Yup.string()
        .required('Last name is required'),
    phone: Yup.string()
        .matches(/^[0-9+\-() ]+$/, 'Invalid phone number')
});

const UserProfile = () => {
    const { currentUser } = useAuth();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchUserProfile();
    }, []);

    const fetchUserProfile = async () => {
        try {
            setLoading(true);
            const response = await userService.getCurrentUser();
            setProfile(response.data);
            console.log('User profile loaded:', response.data); // Debug line
        } catch (err) {
            setError('Failed to fetch user profile');
            console.error('Profile fetch error:', err);
        } finally {
            setLoading(false);
        }
    };


    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        try {
            await userService.updateProfile(values);
            setStatus({ success: 'Profile updated successfully' });
            fetchUserProfile();
        } catch (err) {
            setStatus({
                error: err.response?.data?.message || 'Failed to update profile'
            });
            console.error(err);
        } finally {
            setSubmitting(false);
        }
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

    if (!profile) {
        return (
            <Typography>Profile not found</Typography>
        );
    }

    return (
        <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Your Profile
            </Typography>

            <Paper sx={{ p: 3, mb: 3 }}>
                <Formik
                    initialValues={{
                        email: profile.email || '',
                        firstName: profile.firstName || '',
                        lastName: profile.lastName || '',
                        phone: profile.phone || '',
                        address: profile.address || '',
                    }}
                    enableReinitialize
                    validationSchema={ProfileSchema}
                    onSubmit={handleSubmit}
                >
                    {({ errors, touched, status, isSubmitting }) => (
                        <Form>
                            {status && status.error && (
                                <Alert severity="error" sx={{ mb: 2 }}>
                                    {status.error}
                                </Alert>
                            )}

                            {status && status.success && (
                                <Alert severity="success" sx={{ mb: 2 }}>
                                    {status.success}
                                </Alert>
                            )}

                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Typography variant="h6">
                                        Account Information
                                    </Typography>
                                    <Divider sx={{ mb: 2 }} />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Typography variant="body2" color="text.secondary" gutterBottom>
                                        Username
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {profile.username}
                                    </Typography>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Typography variant="body2" color="text.secondary" gutterBottom>
                                        Role
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        {profile.role.replace('ROLE_', '')}
                                    </Typography>
                                </Grid>

                                <Grid item xs={12}>
                                    <Typography variant="h6" sx={{ mt: 2 }}>
                                        Personal Information
                                    </Typography>
                                    <Divider sx={{ mb: 2 }} />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="firstName"
                                        label="First Name"
                                        error={touched.firstName && !!errors.firstName}
                                        helperText={touched.firstName && errors.firstName}
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="lastName"
                                        label="Last Name"
                                        error={touched.lastName && !!errors.lastName}
                                        helperText={touched.lastName && errors.lastName}
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="email"
                                        label="Email Address"
                                        type="email"
                                        error={touched.email && !!errors.email}
                                        helperText={touched.email && errors.email}
                                    />
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="phone"
                                        label="Phone Number"
                                        error={touched.phone && !!errors.phone}
                                        helperText={touched.phone && errors.phone}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Field
                                        as={TextField}
                                        fullWidth
                                        name="address"
                                        label="Address"
                                        multiline
                                        rows={2}
                                        error={touched.address && !!errors.address}
                                        helperText={touched.address && errors.address}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                                        <Button
                                            type="submit"
                                            variant="contained"
                                            disabled={isSubmitting}
                                        >
                                            {isSubmitting ? <CircularProgress size={24} /> : 'Update Profile'}
                                        </Button>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Form>
                    )}
                </Formik>
            </Paper>

            {/* Password change section could be added here */}
        </Box>
    );
};

export default UserProfile;