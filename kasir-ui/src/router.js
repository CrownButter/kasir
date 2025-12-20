import { createRouter, createWebHistory } from 'vue-router';
import Login from './views/Login.vue';
import KasirHistory from './views/KasirHistory.vue';
import NotaBaru from './views/NotaBaru.vue';
import NotaPrint from './views/NotaPrint.vue';
import Inventory from './views/Inventory.vue';
import AdminDashboard from './views/AdminDashboard.vue';
import UserProfile from './views/UserProfile.vue';
import EditNota from './views/EditNota.vue';
import ItemForm from './views/ItemForm.vue'; 

const routes = [
    { path: '/login', component: Login, meta: { guest: true } },
    
    { path: '/', component: NotaBaru, meta: { requiresAuth: true } },
    { path: '/riwayat', component: KasirHistory, meta: { requiresAuth: true } }, 
    { path: '/print/:id', component: NotaPrint, meta: { requiresAuth: true } },
    
    // Rute Inventory
    { path: '/inventory', component: Inventory, meta: { requiresAuth: true } },
    { path: '/inventory/tambah', component: ItemForm, meta: { requiresAuth: true } },
    { path: '/inventory/edit/:id', component: ItemForm, meta: { requiresAuth: true } },
    
    { path: '/profile', component: UserProfile, meta: { requiresAuth: true } },
    { path: '/edit/:id', component: EditNota, meta: { requiresAuth: true } },

    // HALAMAN KHUSUS ADMIN
    { 
        path: '/admin', 
        component: AdminDashboard, 
        meta: { 
            requiresAuth: true,
            role: 'ADMIN'
        } 
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// --- PENJAGA PINTU (GUARD) ---
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('accessToken');
    const userRole = localStorage.getItem('role'); 

    // 1. Cek apakah user mau masuk halaman Login (Guest) tapi sudah punya Token?
    if (to.meta.guest && token) {
        return next('/'); 
    }

    // 2. Cek apakah halaman butuh login (requiresAuth) tapi tidak punya Token?
    if (to.meta.requiresAuth && !token) {
        return next('/login');
    }

    // 3. Cek Hak Akses Admin
    if (to.meta.role === 'ADMIN') {
        if (userRole !== 'ADMIN') {
            alert("AKSES DITOLAK: Halaman ini khusus Admin!");
            return next('/');
        }
    }

    next();
});

export default router;