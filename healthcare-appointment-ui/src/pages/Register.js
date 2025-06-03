// src/pages/Register.js
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import {
    Container, Box, Typography, TextField, Button,
    Paper, Alert, CircularProgress
} from '@mui/material';

// Validation schema
const RegisterSchema = Yup.object().shape({
    username: Yup.string()
        .min(3, 'Username must be at least 3 characters')
        .max(50, 'Username must be less than 50 characters')
        .required('Username is required'),
    email: Yup.string()
        .email('Invalid email address')
        .required('Email is required'),
    password: Yup.string()
        .min(6, 'Password must be at least 6 characters')
        .required('Password is required'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), null], 'Passwords must match')
        .required('Confirm password is required')
});

const Register = () => {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (values, { setSubmitting }) => {
        try {
            await register({
                username: values.username,
                email: values.email,
                password: values.password
            });
            setSuccess(true);
            setTimeout(() => navigate('/login'), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
                    <Typography component="h1" variant="h5" align="center" gutterBottom>
                        Healthcare Appointment System
                    </Typography>
                    <Typography component="h2" variant="h6" align="center">
                        Register
                    </Typography>

                    {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
                    {success && (
                        <Alert severity="success" sx={{ mt: 2 }}>
                            Registration successful! Redirecting to login...
                        </Alert>
                    )}

                    <Formik
                        initialValues={{
                            username: '',
                            email: '',
                            password: '',
                            confirmPassword: ''
                        }}
                        validationSchema={RegisterSchema}
                        onSubmit={handleSubmit}
                    >
                        {({ errors, touched, isSubmitting }) => (
                            <Form>
                                <Field
                                    as={TextField}
                                    margin="normal"
                                    fullWidth
                                    name="username"
                                    label="Username"
                                    autoFocus
                                    error={touched.username && !!errors.username}
                                    helperText={touched.username && errors.username}
                                />

                                <Field
                                    as={TextField}
                                    margin="normal"
                                    fullWidth
                                    name="email"
                                    label="Email Address"
                                    type="email"
                                    error={touched.email && !!errors.email}
                                    helperText={touched.email && errors.email}
                                />

                                <Field
                                    as={TextField}
                                    margin="normal"
                                    fullWidth
                                    name="password"
                                    label="Password"
                                    type="password"
                                    error={touched.password && !!errors.password}
                                    helperText={touched.password && errors.password}
                                />

                                <Field
                                    as={TextField}
                                    margin="normal"
                                    fullWidth
                                    name="confirmPassword"
                                    label="Confirm Password"
                                    type="password"
                                    error={touched.confirmPassword && !!errors.confirmPassword}
                                    helperText={touched.confirmPassword && errors.confirmPassword}
                                />

                                <Button
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    sx={{ mt: 3, mb: 2 }}
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? <CircularProgress size={24} /> : 'Register'}
                                </Button>

                                <Typography variant="body2" align="center">
                                    Already have an account?{' '}
                                    <Link to="/login">Login</Link>
                                </Typography>
                            </Form>
                        )}
                    </Formik>
                </Paper>
            </Box>
        </Container>
    );
};

export default Register;