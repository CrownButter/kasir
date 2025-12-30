<template>
  <div class="space-y-6">
    
    <div class="flex flex-col md:flex-row justify-between items-center bg-white p-6 rounded-lg shadow-md border-l-4 border-blue-600">
       <div>
          <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">
             <i class="bi bi-people-fill text-blue-600"></i> Management User & Role
          </h3>
          <p class="text-sm text-gray-500 mt-1">Atur pengguna dan hak akses (Permission) secara detail.</p>
       </div>
       <button @click="resetForm(); showModal = true" class="mt-4 md:mt-0 bg-blue-600 text-white px-6 py-2 rounded-lg font-bold shadow hover:bg-blue-700 transition flex items-center gap-2">
          <i class="bi bi-person-plus-fill"></i> Tambah User Baru
       </button>
    </div>

    <div class="bg-white rounded-lg shadow-md overflow-hidden border">
       <table class="w-full text-sm text-left">
          <thead class="bg-gray-800 text-white uppercase text-xs tracking-wider">
             <tr>
                <th class="p-4">Username</th>
                <th class="p-4">Role Utama</th>
                <th class="p-4">Hak Akses Khusus (Permissions)</th>
                <th class="p-4 text-center">Aksi</th>
             </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
             <tr v-for="u in users" :key="u.id" class="hover:bg-gray-50 transition">
                <td class="p-4 font-bold text-gray-700">{{ u.username }}</td>
                <td class="p-4">
                  <span :class="getRoleBadge(u.role)">
                    {{ u.role }}
                  </span>
                </td>
                <td class="p-4">
                   <div v-if="u.role === 'ADMIN'" class="text-xs text-gray-400 italic">Full Access (Super User)</div>
                   <div v-else class="flex flex-wrap gap-1">
                      <span v-for="perm in u.permissions" :key="perm" class="bg-yellow-100 text-yellow-800 px-2 py-0.5 rounded text-[10px] font-semibold border border-yellow-200 shadow-sm">
                        {{ formatPerm(perm) }}
                      </span>
                      <span v-if="!u.permissions || u.permissions.length === 0" class="text-gray-400 text-xs">- Tidak ada izin khusus -</span>
                   </div>
                </td>
                <td class="p-4 text-center">
                   <div class="flex justify-center gap-2">
                      <button v-if="u.role !== 'ADMIN'" @click="editUser(u)" class="text-blue-600 hover:text-blue-800 bg-blue-50 p-2 rounded hover:bg-blue-100 transition" title="Edit Akses">
                         <i class="bi bi-pencil-square"></i>
                      </button>
                      <button v-if="u.role !== 'ADMIN'" @click="deleteUser(u.id)" class="text-red-500 hover:text-red-700 bg-red-50 p-2 rounded hover:bg-red-100 transition" title="Hapus User">
                         <i class="bi bi-trash-fill"></i>
                      </button>
                   </div>
                </td>
             </tr>
          </tbody>
       </table>
       <div v-if="users.length === 0" class="p-8 text-center text-gray-500">Belum ada data user.</div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
       <div class="bg-white w-full max-w-2xl rounded-xl shadow-2xl overflow-hidden transform transition-all scale-100">
          
          <div class="bg-gray-900 text-white p-4 flex justify-between items-center">
             <h4 class="font-bold text-lg flex items-center gap-2">
                <i class="bi bi-shield-lock"></i> {{ isEdit ? 'Edit Hak Akses User' : 'Tambah User Baru' }}
             </h4>
             <button @click="showModal = false" class="text-gray-400 hover:text-white transition"><i class="bi bi-x-lg text-xl"></i></button>
          </div>

          <div class="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
             
             <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                   <label class="block text-xs font-bold text-gray-500 uppercase mb-1">Username</label>
                   <input v-model="form.username" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold" placeholder="Contoh: kasir01" />
                </div>
                <div>
                   <label class="block text-xs font-bold text-gray-500 uppercase mb-1">Password</label>
                   <input v-model="form.password" type="password" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" :placeholder="isEdit ? '(Kosongkan jika tidak ubah)' : 'Minimal 6 karakter'" />
                </div>
             </div>

             <div class="bg-blue-50 p-4 rounded border border-blue-100">
                <label class="block text-xs font-bold text-blue-700 uppercase mb-2">Pilih Role (Template)</label>
                <select v-model="form.role" @change="applyRolePreset" class="w-full p-2 border border-blue-300 rounded font-bold text-gray-700 focus:outline-none focus:border-blue-500">
                   <option value="KASIR">KASIR BIASA (Hanya Transaksi)</option>
                   <option value="MANAGER">MANAGER (Bisa Edit Stok & Laporan)</option>
                   <option value="ADMIN">ADMIN (Full Akses System)</option>
                </select>
                <p class="text-[10px] text-blue-500 mt-1 italic">*Memilih role akan mencentang permission default secara otomatis.</p>
             </div>

             <div v-if="form.role !== 'ADMIN'">
                <label class="block text-xs font-bold text-gray-500 uppercase mb-3 border-b pb-1">Setting Detail Permission (Bisa Apa Saja?)</label>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                   <div class="border rounded p-3 relative">
                      <span class="absolute -top-2 left-2 bg-white px-1 text-[10px] font-bold text-gray-400">Gudang & Stok</span>
                      <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded">
                         <input type="checkbox" value="STOCK_UPDATE" v-model="form.permissions" class="w-4 h-4 text-blue-600 rounded">
                         <span class="text-sm font-semibold">Edit Stok & Tambah Barang</span>
                      </label>
                      <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded mt-1">
                         <input type="checkbox" value="ITEM_DELETE" v-model="form.permissions" class="w-4 h-4 text-red-600 rounded">
                         <span class="text-sm font-semibold text-red-600">Hapus Data Barang (Bahaya)</span>
                      </label>
                   </div>

                   <div class="border rounded p-3 relative">
                      <span class="absolute -top-2 left-2 bg-white px-1 text-[10px] font-bold text-gray-400">Transaksi Kasir</span>
                      <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded">
                         <input type="checkbox" value="TRANSACTION_VOID" v-model="form.permissions" class="w-4 h-4 text-blue-600 rounded">
                         <span class="text-sm font-semibold">Batalkan Transaksi</span>
                      </label>
                      <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded mt-1">
                         <input type="checkbox" value="TRANSACTION_EDIT" v-model="form.permissions" class="w-4 h-4 text-blue-600 rounded">
                         <span class="text-sm font-semibold">Edit Nota Lama</span>
                      </label>
                   </div>

                   <div class="border rounded p-3 relative md:col-span-2">
                      <span class="absolute -top-2 left-2 bg-white px-1 text-[10px] font-bold text-gray-400">Management</span>
                      <div class="grid grid-cols-2 gap-4">
                         <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded">
                            <input type="checkbox" value="VIEW_REPORT" v-model="form.permissions" class="w-4 h-4 text-blue-600 rounded">
                            <span class="text-sm font-semibold">Lihat Laporan Keuangan</span>
                         </label>
                         <label class="flex items-center gap-2 cursor-pointer p-1 hover:bg-gray-50 rounded">
                            <input type="checkbox" value="MANAGE_USER" v-model="form.permissions" class="w-4 h-4 text-blue-600 rounded">
                            <span class="text-sm font-semibold">Kelola User Lain</span>
                         </label>
                      </div>
                   </div>
                </div>
             </div>
             
             <div v-else class="p-4 bg-green-100 text-green-800 rounded text-center text-sm font-bold border border-green-200">
                <i class="bi bi-check-circle-fill"></i> Role ADMIN memiliki akses penuh ke semua fitur.
             </div>

          </div>

          <div class="p-4 bg-gray-50 border-t flex justify-end gap-3">
             <button @click="showModal = false" class="px-4 py-2 text-gray-600 font-bold hover:bg-gray-200 rounded transition">Batal</button>
             <button @click="saveUser" class="px-6 py-2 bg-blue-600 text-white font-bold rounded shadow hover:bg-blue-700 transition flex items-center gap-2">
                <i class="bi bi-save"></i> Simpan User
             </button>
          </div>
       </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api'; // Pastikan path ini benar sesuai struktur folder Anda

const users = ref([]);
const showModal = ref(false);
const isEdit = ref(false);

const form = ref({ 
  id: null,
  username: "", 
  password: "", 
  role: "KASIR", 
  permissions: [] 
});

// --- ROLE PRESETS (LOGIKA "SETTING ROLE") ---
// Ini menentukan permission default saat Role dipilih
const rolePresets = {
    'KASIR': [], // Kasir default tidak punya akses spesial
    'MANAGER': ['STOCK_UPDATE', 'TRANSACTION_VOID', 'VIEW_REPORT'], // Manager default
    'ADMIN': [] // Admin logic terpisah
};

onMounted(() => loadUsers());

const loadUsers = async () => {
  try {
    const res = await api.get('/api/admin/users');
    users.value = res.data;
  } catch(e) { console.error(e); }
};

// Fungsi otomatis mencentang checkbox berdasarkan Role yang dipilih
const applyRolePreset = () => {
    const selectedRole = form.value.role;
    if (rolePresets[selectedRole]) {
        // Copy preset ke form permissions
        form.value.permissions = [...rolePresets[selectedRole]];
    }
};

const resetForm = () => {
    isEdit.value = false;
    form.value = { username: "", password: "", role: "KASIR", permissions: [] };
};

const editUser = (u) => {
    isEdit.value = true;
    form.value = {
        id: u.id,
        username: u.username,
        role: u.role,
        password: "", // Password dikosongkan saat edit
        permissions: u.permissions || []
    };
    showModal.value = true;
};

const saveUser = async () => {
  if(!form.value.username) return alert("Username wajib diisi!");
  if(!isEdit.value && !form.value.password) return alert("Password wajib diisi untuk user baru!");
  
  try {
    if (isEdit.value) {
        await api.put(`/api/admin/users/${form.value.id}`, form.value);
    } else {
        await api.post('/api/admin/users', form.value);
    }
    
    alert("Data User Berhasil Disimpan!");
    showModal.value = false;
    loadUsers();
  } catch(e) { 
    alert("Gagal menyimpan: " + (e.response?.data || e.message)); 
  }
};

const deleteUser = async (id) => {
  if(confirm("Yakin hapus user ini? Tindakan tidak bisa dibatalkan.")) {
    try {
      await api.delete(`/api/admin/users/${id}`);
      loadUsers();
    } catch(e) { alert("Gagal hapus user"); }
  }
};

// Helpers Visual
const getRoleBadge = (role) => {
    switch(role) {
        case 'ADMIN': return 'bg-purple-100 text-purple-800 px-3 py-1 rounded-full text-xs font-black border border-purple-200';
        case 'MANAGER': return 'bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-xs font-black border border-blue-200';
        default: return 'bg-gray-100 text-gray-800 px-3 py-1 rounded-full text-xs font-black border border-gray-200';
    }
};

const formatPerm = (p) => {
    // Translate kode permission ke bahasa manusia
    const map = {
        'STOCK_UPDATE': 'Edit Stok',
        'ITEM_DELETE': 'Hapus Barang',
        'TRANSACTION_VOID': 'Batal Transaksi',
        'TRANSACTION_EDIT': 'Edit Nota',
        'VIEW_REPORT': 'Laporan',
        'MANAGE_USER': 'Kelola User'
    };
    return map[p] || p;
};
</script>