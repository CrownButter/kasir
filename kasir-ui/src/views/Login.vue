<template>
  <div class="flex items-center justify-center h-screen bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-xl w-96 border-t-4 border-blue-600">
      
      <div class="text-center mb-6">
          <div v-if="toko.logoBase64" class="w-20 h-20 mx-auto mb-2">
              <img :src="toko.logoBase64" class="w-full h-full object-contain"/>
          </div>
          <h2 class="text-2xl font-extrabold text-gray-800">{{ toko.namaToko || 'Login Kasir' }}</h2>
          <p class="text-gray-500 text-sm">Silakan masuk untuk melanjutkan</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block mb-1 text-sm font-bold text-gray-700">Username</label>
          <input v-model="username" type="text" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" required />
        </div>
        <div>
          <label class="block mb-1 text-sm font-bold text-gray-700">Password</label>
          <input v-model="password" type="password" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" required />
        </div>
        <button type="submit" class="w-full bg-blue-600 text-white py-2 rounded font-bold hover:bg-blue-700 transition shadow-lg">
          MASUK APLIKASI
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api'; // Pastikan path import api.js benar
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const router = useRouter();
const toko = ref({});

// Load data toko
onMounted(async () => {
  // --- PENAMBAHAN: Cek Token ---
  const token = localStorage.getItem('accessToken');
  if (token) {
    // Jika token ada, langsung lempar ke dashboard
    router.replace('/'); 
    return;
  }
  // -----------------------------

  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
  } catch(e) {
    console.log("Offline / Backend mati / Belum setup toko");
  }
});

const handleLogin = async () => {
  try {
    const res = await api.post('/api/auth/login', {
      username: username.value,
      password: password.value
    });
    
    // PERUBAHAN DISINI: Simpan Access Token DAN Refresh Token
    // Sesuai dengan DTO LoginResponse dari Backend
    localStorage.setItem('accessToken', res.data.accessToken); 
    localStorage.setItem('refreshToken', res.data.refreshToken);
    
    localStorage.setItem('username', res.data.username);
    localStorage.setItem('role', res.data.role);
    
    // Force reload agar Navbar di App.vue merender ulang data toko & role baru
    window.location.href = "/"; 
    
  } catch (err) {
    // Error handling lebih detail
    if (err.response && err.response.status === 400) {
        // Backend menggunakan @Valid, error mungkin berupa field validation
        alert('Data tidak valid / Salah input.');
    } else if (err.response && err.response.status === 401) {
        alert('Username atau Password salah!');
    } else {
        alert('Login Gagal! Server error.');
    }
    console.error(err);
  }
};
</script>