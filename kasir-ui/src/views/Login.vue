<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100 px-4">
    <div class="bg-white p-8 rounded-lg shadow-xl w-full max-w-sm border-t-4 border-blue-600">
      
      <div class="text-center mb-6">
        <div v-if="toko.logoUrl" class="w-20 h-20 mx-auto mb-2 overflow-hidden flex items-center justify-center border-2 border-gray-100 rounded-lg">
           <img :src="getImageUrl(toko.logoUrl)" class="w-full h-full object-contain"/>
        </div>
        <h2 class="text-2xl font-extrabold text-gray-800 uppercase">{{ toko.namaToko || 'DWIN COMPUTER' }}</h2>
        <p class="text-gray-500 text-sm">Silakan masuk ke sistem</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-4 text-black">
        <div>
          <label class="block mb-1 text-sm font-bold text-gray-700 uppercase">Username</label>
          <input v-model="username" type="text" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold" required />
        </div>
        <div>
          <label class="block mb-1 text-sm font-bold text-gray-700 uppercase">Password</label>
          <input v-model="password" type="password" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold" required />
        </div>
        <button type="submit" class="w-full bg-blue-600 text-white py-2 rounded font-bold hover:bg-blue-700 transition shadow-lg uppercase tracking-widest">
          Masuk Aplikasi
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { getImageUrl } from '../api'; // Import helper getImageUrl

const username = ref("");
const password = ref("");
const toko = ref({});

onMounted(async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
  } catch(e) { 
    console.log("Gagal memuat info toko."); 
  }
});

const handleLogin = async () => {
  try {
    const res = await api.post('/api/auth/login', { 
      username: username.value, 
      password: password.value 
    });
    localStorage.setItem('accessToken', res.data.accessToken);
    localStorage.setItem('refreshToken', res.data.refreshToken);
    localStorage.setItem('username', res.data.username);
    localStorage.setItem('role', res.data.role);
    window.location.href = "/";
  } catch (err) {
    alert('Username atau Password salah!');
  }
};
</script>