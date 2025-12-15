<template>
  <div class="min-h-screen bg-gray-100 font-sans">
    <nav v-if="$route.path !== '/login'" class="bg-blue-600 text-white shadow-md sticky top-0 z-40 w-full">
      <div class="container mx-auto px-2 md:px-4 py-3 flex justify-between items-center max-w-full">
        
        <div class="flex items-center gap-2 md:gap-3 overflow-hidden">
          
          <div v-if="toko.logoBase64" class="w-8 h-8 md:w-10 md:h-10 bg-white rounded-full flex items-center justify-center overflow-hidden border-2 border-blue-400 flex-shrink-0">
            <img :src="toko.logoBase64" class="w-full h-full object-contain"/>
          </div>
          
          <div class="flex flex-col justify-center min-w-0">
            <h1 class="font-bold text-sm md:text-lg leading-tight truncate max-w-[120px] md:max-w-xs">
              {{ toko.namaToko || 'Loading...' }}
            </h1>
            <p class="text-[10px] md:text-xs text-blue-200 truncate">Point of Sales</p>
          </div>
        </div>

        <div class="flex items-center gap-2 md:gap-4 flex-shrink-0">
          <router-link to="/" class="hover:bg-blue-700 px-2 md:px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2">
            <i class="bi bi-cart4 text-lg md:text-xl"></i> 
            <span class="hidden md:inline">Kasir</span>
          </router-link>

          <router-link to="/inventory" class="hover:bg-blue-700 px-2 md:px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2">
            <i class="bi bi-box-seam text-lg md:text-xl"></i>
            <span class="hidden md:inline">Stok</span>
          </router-link>

          <router-link v-if="userRole === 'ADMIN'" to="/admin" class="bg-blue-800 hover:bg-blue-900 px-2 md:px-3 py-2 rounded transition text-sm font-bold shadow flex items-center gap-2">
            <i class="bi bi-shield-lock-fill text-sm md:text-base"></i>
            <span class="hidden md:inline">Admin</span>
          </router-link>

          <router-link to="/profile" class="flex items-center gap-2 hover:bg-blue-700 px-2 md:px-3 py-2 rounded transition" title="Profil">
            <div class="w-7 h-7 md:w-8 md:h-8 bg-blue-800 rounded-full flex items-center justify-center text-xs font-bold border border-blue-400">
              <i class="bi bi-person-fill"></i>
            </div>
            <span class="text-sm font-bold hidden md:inline">{{ username || 'User' }}</span>
          </router-link>

          <button @click="handleLogout" class="bg-red-500 hover:bg-red-600 px-2 md:px-3 py-2 rounded text-sm font-bold transition shadow flex items-center gap-2">
            <i class="bi bi-box-arrow-right"></i>
            <span class="hidden md:inline">Logout</span>
          </button>
        </div>
      </div>
    </nav>

    <div class="p-2 md:p-4">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from './api';

const router = useRouter();
const userRole = ref(localStorage.getItem('role'));
const toko = ref({ namaToko: 'Dwin Computer', logoBase64: null });
const username = ref(localStorage.getItem('username'));

const handleLogout = async () => {
  try {
    await api.post('/api/auth/logout');
    console.log("Logout backend sukses");
  } catch (e) {
    console.log("Token expired/server error, force logout.");
  } finally {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    window.location.href = '/login';
  }
};

onMounted(async () => {
  const token = localStorage.getItem('accessToken');
  if (window.location.pathname !== '/login' && token) {
    try {
      const res = await api.get('/api/admin/toko');
      toko.value = res.data;
    } catch(e) {
      console.log("Gagal load info toko");
    }
  }
});
</script>