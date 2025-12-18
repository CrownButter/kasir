<template>
  <div class="min-h-screen bg-gray-100 font-sans text-black">
    <nav v-if="$route.path !== '/login'" class="bg-blue-600 text-white shadow-md sticky top-0 z-40 w-full">
      <div class="container mx-auto px-2 md:px-4 py-3 flex justify-between items-center max-w-full">
        
        <div class="flex items-center gap-2 md:gap-3 overflow-hidden">
          <div v-if="toko.logoBase64" class="w-8 h-8 md:w-10 md:h-10 bg-white rounded-full flex items-center justify-center overflow-hidden border-2 border-blue-400 flex-shrink-0">
            <img :src="toko.logoBase64" class="w-full h-full object-contain"/>
          </div>
          <div class="flex flex-col justify-center min-w-0">
            <h1 class="font-bold text-sm md:text-lg leading-tight truncate">
              {{ toko.namaToko || 'Dwin Computer' }}
            </h1>
          </div>
        </div>

        <div class="flex items-center gap-1 md:gap-3 flex-shrink-0">
          <router-link to="/" class="hover:bg-blue-700 px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2" active-class="bg-blue-800 shadow-inner">
            <i class="bi bi-clock-history"></i>
            <span class="hidden md:inline">Riwayat</span>
          </router-link>

          <router-link to="/tambah" class="bg-blue-500 hover:bg-blue-400 px-3 py-2 rounded-lg transition text-sm font-bold flex items-center gap-2 border border-blue-400 shadow-sm" active-class="bg-white text-blue-600 border-white">
            <i class="bi bi-plus-circle-fill"></i>
            <span class="hidden md:inline">Tambah Transaksi</span>
          </router-link>

          <router-link to="/inventory" class="hover:bg-blue-700 px-3 py-2 rounded transition text-sm font-medium flex items-center gap-2" active-class="bg-blue-800">
            <i class="bi bi-box-seam"></i>
            <span class="hidden md:inline">Stok</span>
          </router-link>

          <router-link v-if="userRole === 'ADMIN'" to="/admin" class="bg-orange-500 hover:bg-orange-600 px-3 py-2 rounded transition text-sm font-bold shadow flex items-center gap-2">
            <i class="bi bi-shield-lock-fill"></i>
            <span class="hidden md:inline">Admin</span>
          </router-link>
        </div>

        <div class="flex items-center gap-2 md:gap-4">
          <router-link to="/profile" class="flex items-center gap-2 hover:bg-blue-700 p-1 md:px-3 py-2 rounded transition" title="Profil Saya">
            <div class="w-8 h-8 bg-blue-800 rounded-full flex items-center justify-center text-xs font-bold border border-blue-400">
              <i class="bi bi-person-fill text-lg"></i>
            </div>
            <span class="text-sm font-bold hidden lg:inline">{{ username || 'User' }}</span>
          </router-link>

          <button @click="handleLogout" class="bg-red-500 hover:bg-red-600 p-2 md:px-3 py-2 rounded text-sm font-bold transition shadow flex items-center gap-2">
            <i class="bi bi-box-arrow-right"></i>
            <span class="hidden md:inline text-xs">Keluar</span>
          </button>
        </div>
      </div>
    </nav>

    <div class="p-0">
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
const username = ref(localStorage.getItem('username'));
const toko = ref({ namaToko: 'Dwin Computer', logoBase64: null });

const handleLogout = async () => {
  try {
    await api.post('/api/auth/logout');
  } catch (e) {
    console.log("Force logout");
  } finally {
    localStorage.clear();
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