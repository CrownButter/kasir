<template>
  <div class="container mx-auto p-6 max-w-6xl min-h-screen">
    
    <div class="bg-white rounded-2xl shadow-lg p-8 mb-8 flex flex-col md:flex-row items-center gap-6 border border-gray-100">
      <div class="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 border-4 border-white shadow-md">
        <i class="bi bi-person-circle text-6xl"></i>
      </div>
      <div class="text-center md:text-left flex-1">
        <h1 class="text-3xl font-black text-gray-800">{{ username }}</h1>
        <div class="flex flex-wrap gap-2 justify-center md:justify-start mt-2">
          <span class="px-3 py-1 bg-slate-800 text-white text-xs font-bold rounded-full uppercase tracking-wider">
            {{ role }}
          </span>
          <span v-for="perm in permissions" :key="perm" class="px-2 py-1 bg-green-100 text-green-700 text-[10px] font-bold rounded border border-green-200">
            {{ perm }}
          </span>
        </div>
      </div>
      <button @click="logout" class="bg-red-50 text-red-600 px-6 py-3 rounded-xl font-bold hover:bg-red-100 transition flex items-center gap-2 border border-red-100">
        <i class="bi bi-box-arrow-right text-xl"></i> Logout
      </button>
    </div>

    <h2 class="text-xl font-bold text-gray-700 mb-6 border-l-4 border-blue-500 pl-4">Akses Menu Cepat</h2>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

      <router-link to="/" class="group bg-white p-6 rounded-2xl shadow-md border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all">
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 rounded-lg bg-blue-100 text-blue-600 flex items-center justify-center text-2xl group-hover:scale-110 transition">
            <i class="bi bi-cart-plus-fill"></i>
          </div>
          <div>
            <h3 class="font-bold text-lg text-gray-800">Kasir Utama</h3>
            <p class="text-xs text-gray-500">Input transaksi baru</p>
          </div>
        </div>
      </router-link>

      <router-link to="/riwayat" class="group bg-white p-6 rounded-2xl shadow-md border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all">
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 rounded-lg bg-amber-100 text-amber-600 flex items-center justify-center text-2xl group-hover:scale-110 transition">
            <i class="bi bi-clock-history"></i>
          </div>
          <div>
            <h3 class="font-bold text-lg text-gray-800">Riwayat Transaksi</h3>
            <p class="text-xs text-gray-500">Cek penjualan lampau</p>
          </div>
        </div>
      </router-link>

      <router-link 
        v-if="can('VIEW_REPORT')" 
        to="/laporan" 
        class="group bg-white p-6 rounded-2xl shadow-md border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all relative overflow-hidden"
      >
        <div class="absolute right-0 top-0 p-2 opacity-10">
          <i class="bi bi-graph-up text-8xl text-green-600"></i>
        </div>
        <div class="flex items-center gap-4 mb-4 relative z-10">
          <div class="w-12 h-12 rounded-lg bg-green-100 text-green-600 flex items-center justify-center text-2xl group-hover:scale-110 transition">
            <i class="bi bi-file-earmark-bar-graph-fill"></i>
          </div>
          <div>
            <h3 class="font-bold text-lg text-gray-800">Laporan Omzet</h3>
            <p class="text-xs text-gray-500">Analisa penjualan toko</p>
          </div>
        </div>
        <div class="mt-2 pt-2 border-t border-gray-100">
           <span class="text-[10px] font-bold text-green-600 flex items-center gap-1">
             <i class="bi bi-shield-lock"></i> Akses Khusus
           </span>
        </div>
      </router-link>

      <router-link 
        v-if="can('MANAGE_ITEMS') || role === 'ADMIN'"
        to="/inventory" 
        class="group bg-white p-6 rounded-2xl shadow-md border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all"
      >
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 rounded-lg bg-emerald-100 text-emerald-600 flex items-center justify-center text-2xl group-hover:scale-110 transition">
            <i class="bi bi-box-seam-fill"></i>
          </div>
          <div>
            <h3 class="font-bold text-lg text-gray-800">Stok Barang</h3>
            <p class="text-xs text-gray-500">Kelola item & harga</p>
          </div>
        </div>
      </router-link>

      <router-link 
        v-if="role === 'ADMIN'" 
        to="/admin" 
        class="group bg-purple-600 p-6 rounded-2xl shadow-md border border-purple-500 hover:shadow-xl hover:-translate-y-1 transition-all"
      >
        <div class="flex items-center gap-4 mb-4 text-white">
          <div class="w-12 h-12 rounded-lg bg-white/20 flex items-center justify-center text-2xl group-hover:scale-110 transition">
            <i class="bi bi-grid-fill"></i>
          </div>
          <div>
            <h3 class="font-bold text-lg">Admin Panel</h3>
            <p class="text-xs opacity-80">Pengaturan Sistem Pusat</p>
          </div>
        </div>
      </router-link>

    </div>

    <div class="mt-10 p-4 bg-gray-50 rounded-lg text-center text-xs text-gray-400">
      <p>Login sebagai: <b>{{ username }}</b> pada {{ new Date().toLocaleString('id-ID') }}</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const role = ref('');
const permissions = ref([]);

// Fungsi Cek Permission
const can = (permissionName) => {
  // Admin selalu boleh, ATAU jika permissionName ada di dalam list permissions user
  return role.value === 'ADMIN' || permissions.value.includes(permissionName);
};

const logout = () => {
  localStorage.clear();
  router.push('/login');
};

onMounted(() => {
  username.value = localStorage.getItem('username') || 'Guest';
  role.value = localStorage.getItem('role') || '';
  
  // Ambil permission dari localStorage (Format JSON String)
  try {
    const permString = localStorage.getItem('permissions');
    if (permString) {
      permissions.value = JSON.parse(permString);
    }
  } catch (e) {
    console.error("Gagal parse permissions", e);
    permissions.value = [];
  }
});
</script>