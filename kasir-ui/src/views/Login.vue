<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100 px-4">
    <div class="bg-white p-8 rounded-lg shadow-xl w-full max-w-sm border-t-4 border-blue-600">
      
      <div class="text-center mb-6">
        <div v-if="toko.logoUrl" class="w-20 h-20 mx-auto mb-2 overflow-hidden flex items-center justify-center border-2 border-gray-100 rounded-lg bg-gray-50">
           <img :src="getImageUrl(toko.logoUrl)" class="w-full h-full object-contain"/>
        </div>
        <h2 class="text-2xl font-extrabold text-gray-800 uppercase tracking-tight">{{ toko.namaToko || 'DWIN COMPUTER' }}</h2>
        <p class="text-gray-500 text-xs font-bold uppercase tracking-widest mt-1">Point of Sale System</p>
      </div>

      <transition name="fade">
        <div v-if="loginError" class="mb-5 p-3 bg-red-50 border-l-4 border-red-500 flex items-center gap-3 rounded shadow-sm">
          <i class="bi bi-shield-lock-fill text-red-500 text-lg"></i>
          <p class="text-red-700 text-[11px] font-black uppercase leading-tight">
            {{ loginError }}
          </p>
        </div>
      </transition>

      <form @submit.prevent="handleLogin" class="space-y-4 text-black">
        <div>
          <label class="block mb-1 text-[10px] font-black text-gray-400 uppercase tracking-widest">Username</label>
          <input 
            v-model="username" 
            type="text" 
            :class="['w-full border-2 p-3 rounded-xl outline-none font-bold transition-all', 
                     loginError ? 'border-red-200 bg-red-50' : 'border-gray-100 focus:border-blue-500']" 
            @input="loginError = ''"
            placeholder="Masukkan username"
            required 
          />
        </div>

        <div>
          <label class="block mb-1 text-[10px] font-black text-gray-400 uppercase tracking-widest">Password</label>
          <input 
            v-model="password" 
            type="password" 
            :class="['w-full border-2 p-3 rounded-xl outline-none font-bold transition-all', 
                     loginError ? 'border-red-200 bg-red-50' : 'border-gray-100 focus:border-blue-500']" 
            @input="loginError = ''"
            placeholder="••••••••"
            required 
          />
        </div>

        <button 
          type="submit" 
          :disabled="loading"
          class="w-full bg-blue-600 text-white py-4 rounded-xl font-black hover:bg-blue-700 transition shadow-lg shadow-blue-100 uppercase tracking-[0.2em] text-sm flex items-center justify-center gap-2 active:scale-95 disabled:bg-gray-300"
        >
          <i v-if="loading" class="bi bi-hourglass-split animate-spin text-lg"></i>
          <span v-if="loading">Verifikasi...</span>
          <span v-else>Masuk Aplikasi</span>
        </button>
      </form>

      <p class="text-center mt-8 text-[10px] text-gray-400 font-bold uppercase tracking-tighter">
        &copy; 2025 {{ toko.namaToko }} - v1.0
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { getImageUrl } from '../api'; 

const username = ref("");
const password = ref("");
const toko = ref({});
const loginError = ref("");
const loading = ref(false);

onMounted(async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
  } catch(e) { 
    console.log("Informasi toko gagal dimuat."); 
  }
});

const handleLogin = async () => {
  loading.value = true;
  loginError.value = ""; 
  
  try {
    const res = await api.post('/api/auth/login', { 
      username: username.value, 
      password: password.value 
    });
    
    localStorage.setItem('accessToken', res.data.accessToken);
    localStorage.setItem('refreshToken', res.data.refreshToken);
    localStorage.setItem('username', res.data.username);
    localStorage.setItem('role', res.data.role);
    localStorage.setItem('permissions', JSON.stringify(res.data.permissions || []));
    
    window.location.href = "/";
  } catch (err) {
    // DEBUG: Munculkan di console untuk memastikan data sampai
    console.log("Error Response Data:", err.response?.data);

    if (!err.response) {
      loginError.value = "Server tidak merespon. Pastikan koneksi aktif.";
    } else {
      // Ambil string "Username atau Password yang Anda masukkan salah." dari backend
      loginError.value = err.response.data?.message || "Login Gagal. Cek kembali akun Anda.";
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>