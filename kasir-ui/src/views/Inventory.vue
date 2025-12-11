<template>
  <div class="container mx-auto p-6 max-w-6xl">
    <div class="bg-white shadow-lg rounded-lg p-6 min-h-screen">
      
      <div class="flex justify-between items-center mb-6 border-b pb-4">
        <div>
           <h2 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
              <i class="bi bi-box-seam-fill text-blue-600"></i> Manajemen Stok Gudang
           </h2>
           <p class="text-sm text-gray-500">Kelola master barang, harga modal, dan harga jual disini.</p>
        </div>
        <div class="bg-blue-50 text-blue-800 px-4 py-2 rounded text-sm font-bold border border-blue-200">
            Total Item: {{ items.length }}
        </div>
      </div>

      <div class="bg-gray-50 p-5 rounded border border-gray-200 mb-8 shadow-inner">
          <h3 class="font-bold text-gray-700 mb-3 flex items-center gap-2">
              <i class="bi" :class="isEditMode ? 'bi-pencil-square text-orange-500' : 'bi-plus-circle text-green-500'"></i>
              {{ isEditMode ? 'Edit Barang' : 'Tambah Barang Baru' }}
          </h3>
          
          <div class="grid grid-cols-1 md:grid-cols-5 gap-4 items-end">
              <div>
                  <label class="block text-xs font-bold text-gray-500 mb-1">Kode Barang</label>
                  <input v-model="form.kode" class="w-full border p-2 rounded bg-white focus:ring-2 focus:ring-blue-500 outline-none uppercase font-mono" placeholder="KODE001"/>
              </div>
              
              <div class="md:col-span-2">
                  <label class="block text-xs font-bold text-gray-500 mb-1">Nama Barang</label>
                  <input v-model="form.nama" class="w-full border p-2 rounded bg-white focus:ring-2 focus:ring-blue-500 outline-none" placeholder="Contoh: SSD Samsung 250GB"/>
              </div>

              <div>
                  <label class="block text-xs font-bold text-gray-500 mb-1">Stok Awal</label>
                  <input v-model="form.stok" type="number" class="w-full border p-2 rounded bg-white focus:ring-2 focus:ring-blue-500 outline-none text-center font-bold"/>
              </div>

              <div class="flex gap-2">
                  <button v-if="isEditMode" @click="cancelEdit" class="w-1/2 bg-gray-300 hover:bg-gray-400 text-gray-700 font-bold py-2 rounded">
                      Batal
                  </button>
                  <button @click="saveItem" :class="isEditMode ? 'bg-orange-500 hover:bg-orange-600' : 'bg-green-600 hover:bg-green-700'" class="flex-1 text-white font-bold py-2 rounded shadow transition">
                      {{ isEditMode ? 'Update' : 'Simpan' }}
                  </button>
              </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4 border-t pt-4 border-gray-200">
              <div>
                  <label class="block text-xs font-bold text-gray-500 mb-1">Harga Modal (Beli)</label>
                  <div class="relative">
                      <span class="absolute left-3 top-2 text-gray-400 text-sm">Rp</span>
                      <input v-model="form.hargaBeli" type="number" class="w-full border p-2 pl-8 rounded bg-white focus:ring-2 focus:ring-blue-500 outline-none font-mono"/>
                  </div>
              </div>
              <div>
                  <label class="block text-xs font-bold text-blue-600 mb-1">Harga Jual</label>
                  <div class="relative">
                      <span class="absolute left-3 top-2 text-blue-400 text-sm font-bold">Rp</span>
                      <input v-model="form.harga" type="number" class="w-full border-2 border-blue-100 p-2 pl-8 rounded bg-white focus:border-blue-500 outline-none font-bold text-blue-700 font-mono"/>
                  </div>
              </div>
          </div>
      </div>

      <div class="overflow-x-auto rounded border border-gray-200">
          <table class="w-full text-sm text-left">
              <thead class="bg-gray-800 text-white uppercase">
                  <tr>
                      <th class="p-3 w-32">Kode</th>
                      <th class="p-3">Nama Barang</th>
                      <th class="p-3 text-center w-24">Stok</th>
                      <th class="p-3 text-right w-32">Modal</th>
                      <th class="p-3 text-right w-32">Jual</th>
                      <th class="p-3 text-center w-24">Margin</th>
                      <th class="p-3 text-center w-32">Aksi</th>
                  </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                  <tr v-for="item in items" :key="item.id" class="hover:bg-blue-50 transition">
                      <td class="p-3 font-mono font-bold text-gray-600">{{ item.kode }}</td>
                      <td class="p-3 font-bold text-gray-800">{{ item.nama }}</td>
                      <td class="p-3 text-center">
                          <span :class="item.stok > 0 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 rounded font-bold text-xs">
                              {{ item.stok }}
                          </span>
                      </td>
                      <td class="p-3 text-right text-gray-500">{{ formatRupiah(item.hargaBeli) }}</td>
                      <td class="p-3 text-right font-bold text-blue-600">{{ formatRupiah(item.harga) }}</td>
                      
                      <td class="p-3 text-center text-xs font-bold text-green-600">
                          {{ calculateMargin(item.hargaBeli, item.harga) }}%
                      </td>

                      <td class="p-3 text-center flex justify-center gap-2">
                          <button @click="editItem(item)" class="text-orange-500 hover:text-orange-700 bg-orange-50 p-2 rounded transition" title="Edit">
                              <i class="bi bi-pencil-fill"></i>
                          </button>
                          <button @click="deleteItem(item.id)" class="text-red-500 hover:text-red-700 bg-red-50 p-2 rounded transition" title="Hapus">
                              <i class="bi bi-trash-fill"></i>
                          </button>
                      </td>
                  </tr>
                  <tr v-if="items.length === 0">
                      <td colspan="7" class="p-8 text-center text-gray-400 italic">Belum ada data barang.</td>
                  </tr>
              </tbody>
          </table>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const items = ref([]);
const isEditMode = ref(false);

const form = ref({
    id: null,
    kode: "",
    nama: "",
    stok: 0,
    hargaBeli: 0, // Modal
    harga: 0      // Jual
});

onMounted(async () => {
    loadItems();
});

const loadItems = async () => {
    try {
        const res = await api.get('/api/items');
        // Sort barang terbaru di atas (ID Desc)
        items.value = res.data.sort((a, b) => b.id - a.id);
    } catch(e) {
        console.error("Gagal load item");
    }
};

const saveItem = async () => {
    if(!form.value.nama || !form.value.kode) return alert("Kode dan Nama wajib diisi!");
    
    try {
        // Create/Update ke Backend (JPA otomatis handle update jika ID ada)
        await api.post('/api/items', form.value);
        
        alert(isEditMode.value ? "Barang berhasil diupdate!" : "Barang berhasil ditambahkan!");
        resetForm();
        loadItems();
    } catch(e) {
        alert("Gagal simpan: " + (e.response?.data?.message || e.message));
    }
};

const editItem = (item) => {
    isEditMode.value = true;
    // Copy value agar tidak reaktif langsung ke tabel sebelum save
    form.value = { ...item };
};

const cancelEdit = () => {
    resetForm();
};

const deleteItem = async (id) => {
    if(!confirm("Yakin hapus barang ini?")) return;
    try {
        await api.delete(`/api/items/${id}`);
        loadItems();
    } catch(e) {
        alert("Gagal hapus item");
    }
};

const resetForm = () => {
    isEditMode.value = false;
    form.value = { id: null, kode: "", nama: "", stok: 0, hargaBeli: 0, harga: 0 };
};

// --- HELPERS ---
const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);

const calculateMargin = (beli, jual) => {
    if(!beli || !jual || beli == 0) return 0;
    const margin = ((jual - beli) / beli) * 100;
    return Math.round(margin);
};
</script>