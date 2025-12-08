import { createRouter, createWebHistory } from 'vue-router';
import Login from './views/Login.vue';
import KasirHistory from './views/KasirHistory.vue';
import NotaBaru from './views/NotaBaru.vue';
import NotaPrint from './views/NotaPrint.vue';
import Inventory from './views/Inventory.vue';
import AdminDashboard from './views/AdminDashboard.vue';

const routes = [
    { path: '/login', component: Login },
    
    // Halaman Umum (Semua User Login Bisa Akses)
    { path: '/', component: KasirHistory, meta: { requiresAuth: true } },
    { path: '/tambah', component: NotaBaru, meta: { requiresAuth: true } },
    { path: '/print/:id', component: NotaPrint, meta: { requiresAuth: true } },
    { path: '/inventory', component: Inventory, meta: { requiresAuth: true } },
    
    // HALAMAN KHUSUS ADMIN (Perhatikan bagian meta: role)
    { 
        path: '/admin', 
        component: AdminDashboard, 
        meta: { 
            requiresAuth: true, 
            role: 'ADMIN' // <--- KUNCI PENGAMANAN ADA DISINI
        } 
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// === PENJAGA PINTU (GUARD) ===
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('role'); // Ambil role dari memori browser

    // 1. Cek Apakah Halaman Butuh Login?
    if (to.meta.requiresAuth && !token) {
        return next('/login'); // Tendang ke login jika tidak ada token
    }

    // 2. Cek Apakah Halaman Khusus ADMIN?
    if (to.meta.role === 'ADMIN') {
        // Debugging: Lihat di Console F12 jika masih lolos
        console.log(`Cek Akses Admin: Role User = ${userRole}`);

        if (userRole !== 'ADMIN') {
            alert("⛔ AKSES DITOLAK: Halaman ini khusus Admin!");
            return next('/'); // Tendang balik ke Home (Kasir History)
        }
    }

    next(); // Izinkan masuk
});

export default router;