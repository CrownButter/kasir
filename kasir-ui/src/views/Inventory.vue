<template>
  <div class="container mx-auto bg-white p-6 rounded shadow">
    <h2 class="text-2xl font-bold mb-6">Kelola Stok Barang</h2>
    <div class="flex gap-4 mb-6 items-end bg-gray-50 p-4 rounded">
        <div><label class="block text-sm font-bold">Kode</label><input v-model="newItem.kode" class="border p-2 rounded" /></div>
        <div><label class="block text-sm font-bold">Nama</label><input v-model="newItem.nama" class="border p-2 rounded w-64" /></div>
        <div><label class="block text-sm font-bold">Harga</label><input v-model="newItem.harga" type="number" class="border p-2 rounded" /></div>
        <div><label class="block text-sm font-bold">Stok</label><input v-model="newItem.stok" type="number" class="border p-2 rounded w-24" /></div>
        <button @click="saveItem" class="bg-blue-600 text-white px-4 py-2 rounded font-bold">+ Simpan</button>
    </div>
    <table class="w-full border-collapse border">
        <thead class="bg-gray-200"><tr><th class="border p-2">Kode</th><th class="border p-2">Nama</th><th class="border p-2">Stok</th><th class="border p-2">Aksi</th></tr></thead>
        <tbody>
            <tr v-for="item in items" :key="item.id" class="text-center">
                <td class="border p-2">{{ item.kode }}</td>
                <td class="border p-2 text-left">{{ item.nama }}</td>
                <td class="border p-2">{{ item.stok }}</td>
                <td class="border p-2"><button @click="deleteItem(item.id)" class="text-red-600">Hapus</button></td>
            </tr>
        </tbody>
    </table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';
const items = ref([]);
const newItem = ref({ kode: '', nama: '', harga: 0, stok: 0 });
const fetchItems = async () => { const res = await api.get('/api/items'); items.value = res.data; };
const saveItem = async () => { await api.post('/api/items', newItem.value); newItem.value = { kode: '', nama: '', harga: 0, stok: 0 }; fetchItems(); };
const deleteItem = async (id) => { if(confirm("Hapus?")) { await api.delete(`/api/items/${id}`); fetchItems(); } };
onMounted(fetchItems);
</script>