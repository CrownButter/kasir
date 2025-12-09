<template>
  <div class="min-h-screen bg-gray-100 font-sans">
    <nav v-if="$route.path !== '/login'" class="bg-blue-600 text-white shadow-md sticky top-0 z-40">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        
        <div class="flex items-center gap-3">
          <div v-if="toko.logoBase64" class="w-10 h-10 bg-white rounded-full flex items-center justify-center overflow-hidden border-2 border-blue-400">
            <img :src="toko.logoBase64" class="w-full h-full object-contain"/>
          </div>
          <div>
            <h1 class="font-bold text-lg leading-tight">{{ toko.namaToko || 'Loading...' }}</h1>
            <p class="text-xs text-blue-200">Point of Sales</p>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <router-link to="/" class="hover:bg-blue-700 px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2">
            <i class="bi bi-cart4 text-lg"></i> Kasir
          </router-link>
          
          <router-link to="/inventory" class="hover:bg-blue-700 px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2">
            <i class="bi bi-box-seam text-lg"></i> Stok
          </router-link>

          <router-link v-if="userRole === 'ADMIN'" to="/admin" class="bg-blue-800 hover:bg-blue-900 px-3 py-2 rounded transition text-sm font-bold shadow flex items-center gap-2">
            <i class="bi bi-shield-lock-fill"></i> Admin Panel
          </router-link>

          <button @click="handleLogout" class="bg-red-500 hover:bg-red-600 px-4 py-2 rounded text-sm font-bold transition shadow flex items-center gap-2">
            <i class="bi bi-box-arrow-right"></i> Logout
          </button>
        </div>
      </div>
    </nav>

    <div class="p-4">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from './api'; // Pastikan path ini benar (jika api.js sejajar dengan App.vue)

const router = useRouter();
const userRole = ref(localStorage.getItem('role'));
const toko = ref({ namaToko: 'Dwin Computer', logoBase64: null });

// Fungsi Logout
const handleLogout = async () => {
  try {
    // 1. Panggil API logout ke Backend (Hapus Refresh Token di DB)
    await api.post('/api/auth/logout'); 
    console.log("Logout backend sukses");
  } catch (e) {
    console.log("Token sudah expired atau server mati, paksa logout lokal.");
  } finally {
    // 2. Wajib hapus semua jejak di browser (Apapun yang terjadi pada server)
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    
    // 3. Redirect ke login (Pakai window.location agar state bersih total)
    window.location.href = '/login';
  }
};

// Load Data Toko (Logo & Nama)
onMounted(async () => {
  // Cek apakah user sudah login (punya token) sebelum request data toko
  const token = localStorage.getItem('accessToken');
  
  if (window.location.pathname !== '/login' && token) {
    try {
      // Pastikan endpoint ini PUBLIC atau User punya Token valid
      const res = await api.get('/api/admin/toko'); 
      toko.value = res.data;
    } catch(e) {
      console.log("Gagal load info toko atau belum login");
    }
  }
});
</script>