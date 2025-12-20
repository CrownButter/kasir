import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/views/Login.vue';
import KasirHistory from '@/views/KasirHistory.vue';
import NotaBaru from '@/views/NotaBaru.vue';
import NotaPrint from '@/views/NotaPrint.vue';
import Inventory from '@/views/Inventory.vue';
import AdminDashboard from '@/views/AdminDashboard.vue';
import UserProfile from '@/views/UserProfile.vue';
import EditNota from '@/views/EditNota.vue';
import ItemForm from '@/views/ItemForm.vue';
import NotaDetail from '@/views/NotaDetail.vue';

// PASTIKAN NAMA FILE INI PERSIS DI FOLDER src/views/
import LaporanPenjualan from '@/views/LaporanPenjualan.vue'; 

const routes = [
    { path: '/login', component: Login, meta: { guest: true } },
    { path: '/', component: NotaBaru, meta: { requiresAuth: true } },
    { path: '/riwayat', component: KasirHistory, meta: { requiresAuth: true } }, 
    { path: '/print/:id', component: NotaPrint, meta: { requiresAuth: true } },
    { path: '/inventory', component: Inventory, meta: { requiresAuth: true } },
    { path: '/inventory/tambah', component: ItemForm, meta: { requiresAuth: true } },
    { path: '/inventory/edit/:id', component: ItemForm, meta: { requiresAuth: true } },
    { path: '/profile', component: UserProfile, meta: { requiresAuth: true } },
    { path: '/edit/:id', component: EditNota, meta: { requiresAuth: true } },
    { path: '/nota/:id', component: NotaDetail, meta: { requiresAuth: true } },
    
    { 
      path: '/laporan', 
      name: 'LaporanPenjualan', 
      component: LaporanPenjualan, 
      meta: { requiresAuth: true, role: 'ADMIN' } 
    },

    { 
        path: '/admin', 
        component: AdminDashboard, 
        meta: { requiresAuth: true, role: 'ADMIN' } 
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('accessToken');
    const userRole = localStorage.getItem('role'); 

    if (to.meta.guest && token) return next('/'); 
    if (to.meta.requiresAuth && !token) return next('/login');

    if (to.meta.role === 'ADMIN' && userRole !== 'ADMIN') {
        alert("AKSES DITOLAK!");
        return next('/');
    }
    next();
});

export default router;