<template>
  <div class="container mx-auto p-6 max-w-4xl">
    <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
      <i class="bi bi-person-circle"></i> Profil Pengguna
    </h2>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="md:col-span-1">
        <div class="bg-white p-6 rounded shadow text-center border-t-4 border-blue-600">
          <div class="w-24 h-24 bg-gray-200 rounded-full mx-auto mb-4 flex items-center justify-center">
            <i class="bi bi-person-fill text-6xl text-gray-400"></i>
          </div>
          <h3 class="text-xl font-bold text-gray-800">{{ user.username || 'Loading...' }}</h3>
          <span class="inline-block bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-xs font-bold mt-2">
            {{ user.role || '-' }}
          </span>
        </div>
      </div>

      <div class="md:col-span-2">
        <div class="bg-white p-6 rounded shadow">
          <h3 class="font-bold text-lg mb-4 border-b pb-2">Edit Keamanan</h3>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-bold text-gray-600 mb-1">Username</label>
              <input v-model="form.username" type="text" class="w-full border p-2 rounded bg-gray-50 focus:bg-white focus:ring-2 focus:ring-blue-500 outline-none transition" />
              <p class="text-xs text-gray-400 mt-1">*Mengganti username akan mengubah cara Anda login.</p>
            </div>

            <div class="border-t pt-4 mt-2">
              <label class="block text-sm font-bold text-gray-600 mb-1">Password Lama <span class="text-red-500">*</span></label>
              <input v-model="form.oldPassword" type="password" placeholder="Wajib diisi untuk menyimpan perubahan" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-bold text-gray-600 mb-1">Password Baru</label>
                <input v-model="form.newPassword" type="password" placeholder="Kosongkan jika tidak ubah" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label class="block text-sm font-bold text-gray-600 mb-1">Konfirmasi Password</label>
                <input v-model="confirmPassword" type="password" placeholder="Ulangi password baru" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
            </div>

            <div class="flex justify-end mt-6">
               <button @click="saveProfile" class="bg-blue-600 text-white px-6 py-2 rounded font-bold hover:bg-blue-700 shadow flex items-center gap-2">
                 <i class="bi bi-save"></i> Simpan Perubahan
               </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const user = ref({});
const form = ref({ username: '', oldPassword: '', newPassword: '' });
const confirmPassword = ref('');

// 1. Ambil data profil saat halaman dibuka
onMounted(async () => {
  try {
    const res = await api.get('/api/profile');
    user.value = res.data;
    form.value.username = res.data.username; // Isi field username otomatis
  } catch (e) {
    console.error("Gagal load profil", e);
  }
});

// 2. Fungsi Simpan Perubahan
const saveProfile = async () => {
  // Validasi di Frontend
  if (!form.value.oldPassword) {
    return alert("Password Lama WAJIB diisi untuk keamanan!");
  }
  if (form.value.newPassword && form.value.newPassword !== confirmPassword.value) {
    return alert("Konfirmasi password baru tidak cocok!");
  }
  if (form.value.newPassword && form.value.newPassword.length < 6) {
    return alert("Password baru minimal 6 karakter!");
  }

  // Kirim ke Backend
  try {
    await api.put('/api/profile', form.value);
    alert("Profil berhasil diperbarui!");
    
    // Jika username berubah, update localStorage agar navbar berubah
    if(form.value.username !== user.value.username) {
        localStorage.setItem('username', form.value.username);
    }
    
    // Reset form password agar bersih
    form.value.oldPassword = '';
    form.value.newPassword = '';
    confirmPassword.value = '';
    
    // Update tampilan info user
    user.value.username = form.value.username;
    
  } catch (e) {
    // Tampilkan pesan error dari backend (misal: "Password lama salah")
    alert("Gagal: " + (e.response?.data || "Terjadi kesalahan"));
  }
};
</script>