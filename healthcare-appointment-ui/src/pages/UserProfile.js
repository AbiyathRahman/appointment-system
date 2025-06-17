// src/pages/UserProfile.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/api';
import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import {
    Container, Box, Typography, TextField, Button, Paper, Grid,
    Alert, CircularProgress, Avatar, Divider, Chip, Card, CardContent,
    FormControl, InputLabel, Select, MenuItem, FormHelperText, Tabs, Tab,
    Accordion, AccordionSummary, AccordionDetails, IconButton, Tooltip
} from '@mui/material';

import {
    Person as PersonIcon,
    Email as EmailIcon,
    Phone as PhoneIcon,
    Home as HomeIcon,
    Cake as CakeIcon,
    Emergency as EmergencyIcon,
    Edit as EditIcon,
    Save as SaveIcon,
    Cancel as CancelIcon,
    ExpandMore as ExpandMoreIcon,
    PhotoCamera as PhotoCameraIcon
} from '@mui/icons-material';

const ProfileSchema = Yup.object().shape({
    firstName: Yup.string()
        .min(2, 'First name must be at least 2 characters')
        .max(50, 'First name must be less than 50 characters')
        .required('First name is required'),
    lastName: Yup.string()
        .min(2, 'Last name must be at least 2 characters')
        .max(50, 'Last name must be less than 50 characters')
        .required('Last name is required'),
    email: Yup.string()
        .email('Invalid email address')
        .required('Email is required'),
    phone: Yup.string()
        .matches(/^[0-9+\-() ]+$/, 'Invalid phone number')
        .required('Phone number is required'),
    address: Yup.string()
        .required('Address is required'),
});







const UserProfile = () => {
    const { currentUser } = useAuth();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [ selectedDate, setSelectedDate ] = useState(null);
    const [ tabValue, setTabValue ] = useState(0);
    const [editMode, setEditMode] = useState(false);


    useEffect(() => {

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

        fetchUserProfile();
    },[]);


    const handleSubmit = async (values, { setSubmitting }) => {
        try {
            setError('');
            setSuccess('');
            const updateData = {
                ...values,
                dateOfBirth: selectedDate ? selectedDate.toISOString().split('T')[0] : null
            };
            const response = await userService.updateProfile(updateData);
            setProfile(response.data);
            setSuccess(
                'Profile updated successfully'
            )
            setTimeout(() => setSuccess(''), 5000);
        } catch (err) {
            setError({
                error: err.response?.data?.message || 'Failed to update profile'
            });
            console.error(err);
        } finally {
            setSubmitting(false);
        }
    };

    const handleTabChange = (event, newValue) => {
        setSelectedDate(newValue);
    };
    const handleEditToggle = () => {
        setEditMode(!editMode);
        setError('');
        setSuccess('');
    };




    if (loading) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
                <CircularProgress size={60} />
            </Container>
        );
    }

    if (!profile) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Alert severity="error">Failed to load profile data</Alert>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            {/* Header Section */}
            <Paper elevation={2} sx={{ p: 3, mb: 3, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
                <Grid container spacing={3} alignItems="center">
                    <Grid item>
                        <Box sx={{ position: 'relative' }}>
                            <Avatar
                                sx={{
                                    width: 100,
                                    height: 100,
                                    fontSize: '2rem',
                                    background: 'rgba(255,255,255,0.3)',
                                    backdropFilter: 'blur(10px)'
                                }}
                            >
                                {profile.firstName?.charAt(0)}{profile.lastName?.charAt(0)}
                            </Avatar>
                            <IconButton
                                sx={{
                                    position: 'absolute',
                                    bottom: -5,
                                    right: -5,
                                    backgroundColor: 'white',
                                    color: 'primary.main',
                                    '&:hover': { backgroundColor: 'grey.100' }
                                }}
                                size="small"
                            >
                                <PhotoCameraIcon fontSize="small" />
                            </IconButton>
                        </Box>
                    </Grid>
                    <Grid item xs>
                        <Typography variant="h4" fontWeight="bold" gutterBottom>
                            {profile.firstName} {profile.lastName}
                        </Typography>
                        <Typography variant="h6" sx={{ opacity: 0.9 }}>
                            Patient
                        </Typography>
                        <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                            <Chip
                                label={`Member since ${new Date(profile.createdAt).getFullYear()}`}
                                sx={{ backgroundColor: 'rgba(255,255,255,0.2)', color: 'white' }}
                            />
                            {profile.phone && (
                                <Chip
                                    label="Phone Verified"
                                    sx={{ backgroundColor: 'rgba(76, 175, 80, 0.8)', color: 'white' }}
                                />
                            )}
                        </Box>
                    </Grid>
                    <Grid item>
                        <Tooltip title={editMode ? "Cancel editing" : "Edit profile"}>
                            <IconButton
                                onClick={handleEditToggle}
                                sx={{
                                    backgroundColor: 'rgba(255,255,255,0.2)',
                                    color: 'white',
                                    '&:hover': { backgroundColor: 'rgba(255,255,255,0.3)' }
                                }}
                            >
                                {editMode ? <CancelIcon /> : <EditIcon />}
                            </IconButton>
                        </Tooltip>
                    </Grid>
                </Grid>
            </Paper>

            {/* Alerts */}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            {/* Tabs */}
            <Paper elevation={1} sx={{ mb: 3 }}>
                <Tabs value={tabValue} onChange={handleTabChange} variant="fullWidth">
                    <Tab label="Personal Information" />
                    <Tab label="Contact Details" />
                    <Tab label="Medical Information" />
                    <Tab label="Emergency Contact" />
                </Tabs>
            </Paper>

            {/* Form */}
            <Formik
                initialValues={{
                    firstName: profile.firstName || '',
                    lastName: profile.lastName || '',
                    email: profile.email || '',
                    phone: profile.phone || '',
                    address: profile.address || '',
                    gender: profile.gender || '',
                    emergencyContactName: profile.emergencyContactName || '',
                    emergencyContactPhone: profile.emergencyContactPhone || '',
                }}
                validationSchema={ProfileSchema}
                onSubmit={handleSubmit}
                enableReinitialize
            >
                {({ errors, touched, isSubmitting, values }) => (
                    <Form>
                        {/* Personal Information Tab */}
                        {tabValue === 0 && (
                            <Card elevation={2}>
                                <CardContent sx={{ p: 4 }}>
                                    <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                                        <PersonIcon sx={{ mr: 1, color: 'primary.main' }} />
                                        Personal Information
                                    </Typography>

                                    <Grid container spacing={3}>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="firstName"
                                                label="First Name"
                                                disabled={!editMode}
                                                error={touched.firstName && !!errors.firstName}
                                                helperText={touched.firstName && errors.firstName}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <PersonIcon sx={{ mr: 1, color: 'action.active' }} />
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="lastName"
                                                label="Last Name"
                                                disabled={!editMode}
                                                error={touched.lastName && !!errors.lastName}
                                                helperText={touched.lastName && errors.lastName}
                                                variant={editMode ? "outlined" : "filled"}
                                            />
                                        </Grid>

                                        <Grid item xs={12} md={6}>
                                            <FormControl fullWidth disabled={!editMode}>
                                                <Typography variant="body2" sx={{ mb: 1, color: 'text.secondary' }}>
                                                    Date of Birth
                                                </Typography>
                                                <DatePicker
                                                    selected={selectedDate}
                                                    onChange={setSelectedDate}
                                                    maxDate={new Date()}
                                                    showYearDropdown
                                                    yearDropdownItemNumber={100}
                                                    scrollableYearDropdown
                                                    dateFormat="MMMM d, yyyy"
                                                    disabled={!editMode}
                                                    customInput={
                                                        <TextField
                                                            fullWidth
                                                            variant={editMode ? "outlined" : "filled"}
                                                            InputProps={{
                                                                startAdornment: <CakeIcon sx={{ mr: 1, color: 'action.active' }} />
                                                            }}
                                                        />
                                                    }
                                                />
                                            </FormControl>
                                        </Grid>

                                        <Grid item xs={12} md={6}>
                                            <FormControl fullWidth disabled={!editMode}>
                                                <InputLabel>Gender</InputLabel>
                                                <Field
                                                    as={Select}
                                                    name="gender"
                                                    label="Gender"
                                                    variant={editMode ? "outlined" : "filled"}
                                                >
                                                    <MenuItem value="MALE">Male</MenuItem>
                                                    <MenuItem value="FEMALE">Female</MenuItem>
                                                    <MenuItem value="OTHER">Other</MenuItem>
                                                    <MenuItem value="PREFER_NOT_TO_SAY">Prefer not to say</MenuItem>
                                                </Field>
                                            </FormControl>
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        )}

                        {/* Contact Details Tab */}
                        {tabValue === 1 && (
                            <Card elevation={2}>
                                <CardContent sx={{ p: 4 }}>
                                    <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                                        <EmailIcon sx={{ mr: 1, color: 'primary.main' }} />
                                        Contact Details
                                    </Typography>

                                    <Grid container spacing={3}>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="email"
                                                label="Email Address"
                                                type="email"
                                                disabled={!editMode}
                                                error={touched.email && !!errors.email}
                                                helperText={touched.email && errors.email}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <EmailIcon sx={{ mr: 1, color: 'action.active' }} />
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="phone"
                                                label="Phone Number"
                                                disabled={!editMode}
                                                error={touched.phone && !!errors.phone}
                                                helperText={touched.phone && errors.phone}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <PhoneIcon sx={{ mr: 1, color: 'action.active' }} />
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="address"
                                                label="Address"
                                                multiline
                                                rows={3}
                                                disabled={!editMode}
                                                error={touched.address && !!errors.address}
                                                helperText={touched.address && errors.address}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <HomeIcon sx={{ mr: 1, color: 'action.active', alignSelf: 'flex-start', mt: 1 }} />
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        )}

                        {/* Medical Information Tab */}
                        {tabValue === 2 && (
                            <Card elevation={2}>
                                <CardContent sx={{ p: 4 }}>
                                    <Typography variant="h6" gutterBottom sx={{ mb: 3 }}>
                                        Medical Information
                                    </Typography>

                                    <Alert severity="info" sx={{ mb: 3 }}>
                                        Medical information will be available in future updates.
                                        For now, please discuss medical history directly with your healthcare provider.
                                    </Alert>

                                    <Grid container spacing={3}>
                                        <Grid item xs={12}>
                                            <Accordion disabled>
                                                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                                    <Typography>Allergies & Medical Conditions</Typography>
                                                </AccordionSummary>
                                                <AccordionDetails>
                                                    <Typography color="text.secondary">
                                                        Coming soon - Add your allergies and medical conditions
                                                    </Typography>
                                                </AccordionDetails>
                                            </Accordion>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Accordion disabled>
                                                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                                    <Typography>Current Medications</Typography>
                                                </AccordionSummary>
                                                <AccordionDetails>
                                                    <Typography color="text.secondary">
                                                        Coming soon - Track your current medications
                                                    </Typography>
                                                </AccordionDetails>
                                            </Accordion>
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        )}

                        {/* Emergency Contact Tab */}
                        {tabValue === 3 && (
                            <Card elevation={2}>
                                <CardContent sx={{ p: 4 }}>
                                    <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                                        <EmergencyIcon sx={{ mr: 1, color: 'error.main' }} />
                                        Emergency Contact
                                    </Typography>

                                    <Grid container spacing={3}>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="emergencyContactName"
                                                label="Emergency Contact Name"
                                                disabled={!editMode}
                                                error={touched.emergencyContactName && !!errors.emergencyContactName}
                                                helperText={touched.emergencyContactName && errors.emergencyContactName}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <PersonIcon sx={{ mr: 1, color: 'action.active' }} />
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <Field
                                                as={TextField}
                                                fullWidth
                                                name="emergencyContactPhone"
                                                label="Emergency Contact Phone"
                                                disabled={!editMode}
                                                error={touched.emergencyContactPhone && !!errors.emergencyContactPhone}
                                                helperText={touched.emergencyContactPhone && errors.emergencyContactPhone}
                                                variant={editMode ? "outlined" : "filled"}
                                                InputProps={{
                                                    startAdornment: <PhoneIcon sx={{ mr: 1, color: 'action.active' }} />
                                                }}
                                            />
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        )}

                        {/* Action Buttons */}
                        {editMode && (
                            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                                <Button
                                    variant="outlined"
                                    onClick={handleEditToggle}
                                    startIcon={<CancelIcon />}
                                >
                                    Cancel
                                </Button>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    disabled={isSubmitting}
                                    startIcon={isSubmitting ? <CircularProgress size={20} /> : <SaveIcon />}
                                >
                                    {isSubmitting ? 'Saving...' : 'Save Changes'}
                                </Button>
                            </Box>
                        )}
                    </Form>
                )}
            </Formik>
        </Container>
    );
};

export default UserProfile;
