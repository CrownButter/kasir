<template>
  <div class="container mx-auto p-6 max-w-6xl">
    <div class="bg-white shadow-lg rounded-lg p-6 min-h-screen text-black">
      
      <div class="flex justify-between items-center mb-6 border-b pb-4">
        <div>
           <h2 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
              <i class="bi bi-box-seam-fill text-blue-600"></i> Manajemen Stok Gudang
           </h2>
           <p class="text-sm text-gray-500">Kelola master barang, harga modal, dan harga jual disini.</p>
        </div>
        <div class="flex gap-3">
            <div class="bg-blue-50 text-blue-800 px-4 py-2 rounded text-sm font-bold border border-blue-200 flex items-center">
                Total Item: {{ items.length }}
            </div>
            <router-link to="/inventory/tambah" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-bold shadow-md flex items-center gap-2 transition uppercase text-xs">
                <i class="bi bi-plus-circle-fill"></i> Tambah Barang
            </router-link>
        </div>
      </div>
      <div class="flex justify-between items-center mb-6 border-b pb-4">
  <div class="flex gap-3 items-center">
    <div class="relative">
      <i class="bi bi-search absolute left-3 top-2 text-gray-400"></i>
      <input v-model="searchQuery" type="text" placeholder="Cari Kode / Nama..." 
        class="pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm w-64"/>
    </div>
    <div class="bg-blue-50 ...">Total Item: {{ filteredItems.length }}</div>
    <router-link to="/inventory/tambah" class="...">Tambah Barang</router-link>
  </div>
</div>

<tr v-for="item in filteredItems" :key="item.id" class="...">

      <div class="overflow-x-auto rounded border border-gray-200">
          <table class="w-full text-sm text-left">
              <thead class="bg-gray-800 text-white uppercase font-bold">
                  <tr>
                      <th class="p-3 w-16">Foto</th>
                      <th class="p-3">Kode</th>
                      <th class="p-3">Nama Barang</th>
                      <th class="p-3 text-center">Stok</th>
                      <th class="p-3 text-right">Modal</th>
                      <th class="p-3 text-right">Jual</th>
                      <th class="p-3 text-center">Aksi</th>
                  </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                  <tr v-for="item in items" :key="item.id" class="hover:bg-blue-50 transition">
                      <td class="p-2">
                        <div class="w-12 h-12 bg-gray-100 rounded border overflow-hidden flex items-center justify-center">
                            <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover" />
                            <i v-else class="bi bi-image text-gray-300"></i>
                        </div>
                      </td>
                      <td class="p-3 font-mono font-bold text-gray-600 uppercase">{{ item.kode }}</td>
                      <td class="p-3 font-bold text-gray-800 uppercase">{{ item.nama }}</td>
                      
                      <td class="p-3 text-center">
                          <span :class="[
                            'px-2 py-1 rounded font-bold text-xs flex items-center justify-center gap-1 mx-auto w-fit',
                            item.stok <= 0 ? 'bg-red-100 text-red-800' : 
                            item.stok < 5 ? 'bg-orange-100 text-orange-800 border border-orange-200' : 
                            'bg-green-100 text-green-800'
                          ]">
                              {{ item.stok }}
                              <i v-if="item.stok > 0 && item.stok < 5" class="bi bi-exclamation-triangle-fill"></i>
                              <i v-if="item.stok <= 0" class="bi bi-x-circle-fill"></i>
                          </span>
                      </td>

                      <td class="p-3 text-right text-gray-500">{{ formatRupiah(item.hargaBeli) }}</td>
                      <td class="p-3 text-right font-bold text-blue-600">{{ formatRupiah(item.harga) }}</td>
                      <td class="p-3 text-center">
                          <div class="flex justify-center gap-2">
                            <router-link :to="'/inventory/edit/' + item.id" class="text-orange-500 hover:bg-orange-100 p-2 rounded transition shadow-sm">
                                <i class="bi bi-pencil-fill"></i>
                            </router-link>
                            <button @click="deleteItem(item.id)" class="text-red-500 hover:bg-red-100 p-2 rounded transition shadow-sm">
                                <i class="bi bi-trash-fill"></i>
                            </button>
                          </div>
                      </td>
                  </tr>
                  
                  <tr v-if="items.length === 0">
                    <td colspan="7" class="p-10 text-center text-gray-400">
                      <i class="bi bi-inbox text-4xl block mb-2"></i>
                      Belum ada data barang.
                    </td>
                  </tr>
              </tbody>
          </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { getImageUrl } from '../api';

const items = ref([]);
const searchQuery = ref(""); // TAMBAH INI
onMounted(() => { loadItems(); });
const filteredItems = computed(() => {
  if (!searchQuery.value) return items.value;
  const q = searchQuery.value.toLowerCase();
  return items.value.filter(i => 
    i.nama.toLowerCase().includes(q) || 
    i.kode.toLowerCase().includes(q)
  );
});
const loadItems = async () => {
    try {
        const res = await api.get('/api/items');
        // Urutkan berdasarkan ID terbaru
        items.value = res.data.sort((a, b) => b.id - a.id);
    } catch(e) { 
        console.error("Gagal load item"); 
    }
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

const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);
</script>

<style scoped>
/* Menambahkan sedikit animasi pada peringatan agar lebih terlihat */
.bg-orange-100 {
  animation: pulse-subtle 2s infinite;
}

@keyframes pulse-subtle {
  0% { opacity: 1; }
  50% { opacity: 0.8; }
  100% { opacity: 1; }
}
</style>