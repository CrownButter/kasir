import { createRouter, createWebHistory } from 'vue-router';
import Login from './views/Login.vue';
import KasirHistory from './views/KasirHistory.vue';
import NotaBaru from './views/NotaBaru.vue';
import NotaPrint from './views/NotaPrint.vue';
import Inventory from './views/Inventory.vue';
import AdminDashboard from './views/AdminDashboard.vue';

const routes = [
    // Tambahkan meta guest agar kalau sudah login tidak bisa buka halaman login lagi
    { path: '/login', component: Login, meta: { guest: true } }, 
    
    // Halaman Umum (Semua User Login Bisa Akses)
    { path: '/', component: KasirHistory, meta: { requiresAuth: true } },
    { path: '/tambah', component: NotaBaru, meta: { requiresAuth: true } },
    { path: '/print/:id', component: NotaPrint, meta: { requiresAuth: true } },
    { path: '/inventory', component: Inventory, meta: { requiresAuth: true } },
    
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

// === PENJAGA PINTU (GUARD) ===
router.beforeEach((to, from, next) => {
    // PERBAIKAN 1: Ganti 'token' menjadi 'accessToken' sesuai Login.vue
    const token = localStorage.getItem('accessToken'); 
    const userRole = localStorage.getItem('role'); 

    // 1. Cek Apakah Halaman Butuh Login?
    if (to.meta.requiresAuth && !token) {
        return next('/login'); // Tendang ke login jika tidak ada accessToken
    }

    // 2. Cek Apakah User Sudah Login tapi mau masuk halaman Login?
    if (to.meta.guest && token) {
        return next('/'); // Tendang ke Dashboard
    }

    // 3. Cek Apakah Halaman Khusus ADMIN?
    if (to.meta.role === 'ADMIN') {
        // Jika role di localStorage bukan ADMIN, tolak
        if (userRole !== 'ADMIN') {
            alert(" AKSES DITOLAK: Halaman ini khusus Admin!");
            return next('/'); // Tendang balik ke Home
        }
    }

    next(); // Izinkan masuk
});

export default router;