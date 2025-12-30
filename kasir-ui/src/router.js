import { createRouter, createWebHistory } from 'vue-router';

// Import Views
import Login from '@/views/Login.vue';
import NotaBaru from '@/views/NotaBaru.vue';
import KasirHistory from '@/views/KasirHistory.vue';
import NotaPrint from '@/views/NotaPrint.vue';
import NotaDetail from '@/views/NotaDetail.vue';
import EditNota from '@/views/EditNota.vue';

import Inventory from '@/views/Inventory.vue';
import ItemForm from '@/views/ItemForm.vue';

import UserProfile from '@/views/UserProfile.vue';

// Import Admin Views
import AdminDashboard from '@/views/AdminDashboard.vue'; // Pastikan file ini ada
import LaporanPenjualan from '@/views/LaporanPenjualan.vue'; 
import UserManagement from '@/views/UserManagement.vue'; // Pastikan file ini ada

const routes = [
    { path: '/login', component: Login, meta: { guest: true } },
    
    // Halaman Utama Kasir
    { path: '/', component: NotaBaru, meta: { requiresAuth: true } },
    { path: '/riwayat', component: KasirHistory, meta: { requiresAuth: true } }, 
    
    // Transaksi & Print
    { path: '/print/:id', component: NotaPrint, meta: { requiresAuth: true } },
    { path: '/nota/:id', component: NotaDetail, meta: { requiresAuth: true } },
    { path: '/edit/:id', component: EditNota, meta: { requiresAuth: true } },
    
    // Inventory
    { path: '/inventory', component: Inventory, meta: { requiresAuth: true } },
    { path: '/inventory/tambah', component: ItemForm, meta: { requiresAuth: true } },
    { path: '/inventory/edit/:id', component: ItemForm, meta: { requiresAuth: true } },
    
    // User Profile
    { path: '/profile', component: UserProfile, meta: { requiresAuth: true } },

    // --- AREA KHUSUS & ADMIN ---

    // 1. Laporan (Fleksibel: Admin ATAU Kasir dengan Izin)
    { 
      path: '/laporan', 
      name: 'LaporanPenjualan', 
      component: LaporanPenjualan, 
      // HAPUS role: 'ADMIN' dari sini agar tidak bentrok dengan Global Guard
      meta: { requiresAuth: true }, 
      
      // Logic Khusus: Cek Izin secara manual
      beforeEnter: (to, from, next) => {
          const userRole = localStorage.getItem('role');
          const permissions = JSON.parse(localStorage.getItem('permissions') || '[]');

          // Jika Admin ATAU punya izin VIEW_REPORT -> Boleh Masuk
          if (userRole === 'ADMIN' || permissions.includes('VIEW_REPORT')) {
              next();
          } else {
              // Jika ditolak, kembalikan ke Home tanpa Alert
              next('/'); 
          }
      }
    },

    // 2. Dashboard Admin (Wajib Admin)
    { 
        path: '/admin', 
        component: AdminDashboard, 
        meta: { requiresAuth: true, role: 'ADMIN' } 
    },

    // 3. Manajemen User (Wajib Admin)
    { 
      path: '/admin/users', 
      name: 'UserManagement', 
      component: UserManagement, 
      meta: { requiresAuth: true, role: 'ADMIN' } 
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// --- GLOBAL GUARD (PENJAGA PINTU UTAMA) ---
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('accessToken');
    const userRole = localStorage.getItem('role'); 

    // 1. Cek Token untuk halaman butuh login
    if (to.meta.requiresAuth && !token) {
        return next('/login');
    }

    // 2. Cek Guest (agar yang sudah login tidak bisa balik ke login)
    if (to.meta.guest && token) {
        return next('/'); 
    }

    // 3. Cek Role ADMIN (Hanya untuk route yang secara eksplisit ada meta.role = 'ADMIN')
    if (to.meta.role === 'ADMIN' && userRole !== 'ADMIN') {
        // Redirect diam-diam ke home jika mencoba paksa masuk URL admin
        return next('/');
    }

    // Lolos semua pengecekan
    next();
});

export default router;