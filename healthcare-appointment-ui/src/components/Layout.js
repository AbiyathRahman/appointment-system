import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    AppBar, Toolbar, Typography, Button, Box,
    Drawer, List, ListItem, ListItemIcon, ListItemText,
    Divider, Container, CssBaseline
} from '@mui/material';
import {
    Dashboard as DashboardIcon,
    Event as EventIcon,
    Person as PersonIcon,
    ExitToApp as LogoutIcon,
    Schedule as ScheduleIcon,
    Settings as SettingsIcon,
} from '@mui/icons-material';

const drawerWidth = 240;
const Layout = () => {
    const { currentUser, logOut, isDoctor, isAdmin } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logOut();
        navigate('/login');
    };

    const menuItems = [
        {text: 'Dashboard', icon: <DashboardIcon />, path: '/dashboard'},
        {text: 'Book Appointment', icon: <EventIcon />, path: '/appointments/book', hideForDoctor: true},
        {text: 'My Appointments', icon: <EventIcon />, path: '/dashboard'},
        {text: 'My Profile', icon: <PersonIcon />, path: '/profile'},
    ];
    if(isDoctor){
        menuItems.push(
            {text: 'Manage Availability', icon:<ScheduleIcon />, path: '/doctor/availability'},
        );

    }
    if(isAdmin){
        menuItems.push(
            {text: 'Admin Panel', icon:<SettingsIcon />, path: '/admin'},
        );
    }

    return (
        <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <AppBar
                position="fixed"
                sx={{ width: `calc(100% - ${drawerWidth}px)`, ml: `${drawerWidth}px` }}
                >
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Healthcare Appointment System
                    </Typography>
                    <Typography variant="subtitle1" sx={{mr:2}}>
                        {currentUser?.username}
                    </Typography>
                    <Button color="inherit" onClick={handleLogout} startIcon={<LogoutIcon />}>Logout</Button>
                </Toolbar>
            </AppBar>
            <Drawer
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
                }}
                variant="permanent"
                anchor="left">
                <Toolbar>
                    <Typography variant="h6" noWrap component={"div"}>
                        Menu
                    </Typography>
                </Toolbar>
                <Divider />
                <List>
                    {menuItems.map((item, index) => (
                        // Skip items that should be hidden for the current role
                        (!item.hideForDoctor || !isDoctor) && (
                            <ListItem button key={item.text} onClick={() => navigate(item.path)}>
                                <ListItemIcon>{item.icon}</ListItemIcon>
                                <ListItemText primary={item.text} />
                            </ListItem>
                        )
                    ))}
                </List>
            </Drawer>
            <Box
                component="main"
                sx={{ flexGrow: 1, p: 3, width: `calc(100% - ${drawerWidth}px)` }}
            >
                <Toolbar /> {/* This is for spacing below the AppBar */}
                <Container>
                    <Outlet /> {/* This is where the page content will be rendered */}
                </Container>
            </Box>

        </Box>
    );
};

export default Layout;