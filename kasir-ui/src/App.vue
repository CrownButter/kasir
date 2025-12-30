<template>
  <div class="min-h-screen bg-gray-100 font-sans text-black">
    
    <Navbar v-if="!isLoginPage" />

    <router-view></router-view>

  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
// Import komponen Navbar yang baru saja Anda buat
import Navbar from './components/Navbar.vue'; 

const route = useRoute();

// Computed property untuk mendeteksi apakah kita sedang di halaman Login
const isLoginPage = computed(() => {
  // Cek jika path URL adalah '/login'
  // (Anda bisa menambahkan kondisi lain jika ada halaman register/lupa password)
  return route.path === '/login';
});
</script>