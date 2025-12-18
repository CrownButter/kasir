<template>
  <div class="flex flex-col md:flex-row h-screen bg-gray-100 overflow-hidden">
    
    <div :class="mode === 'JUAL' ? 'w-full md:w-5/12' : 'w-full md:w-1/3'" 
         class="flex flex-col bg-white border-r shadow-lg z-10">
      
      <div class="p-4 bg-gray-800 text-white flex justify-between items-center">
        <div class="flex gap-2">
          <button @click="switchMode('JUAL')" 
            :class="mode === 'JUAL' ? 'bg-blue-600' : 'bg-gray-700'"
            class="px-4 py-1 rounded text-xs font-bold transition">RETAIL</button>
          <button @click="switchMode('SERVICE')" 
            :class="mode === 'SERVICE' ? 'bg-orange-500' : 'bg-gray-700'"
            class="px-4 py-1 rounded text-xs font-bold transition">SERVICE</button>
        </div>
        <span class="text-xs font-mono text-yellow-400">KASIR: {{ username }}</span>
      </div>

      <div v-if="mode === 'SERVICE'" class="p-4 overflow-y-auto flex-1">
        <h2 class="font-bold mb-4 border-b pb-2">Form Penerimaan Service</h2>
        <div class="space-y-3">
          <input v-model="form.customerNama" placeholder="Nama Pelanggan *" class="w-full p-2 border rounded text-sm"/>
          <input v-model="form.customerTelp" placeholder="No Telp" class="w-full p-2 border rounded text-sm"/>
          <textarea v-model="form.barangCustomer" placeholder="Unit (Contoh: Laptop Acer)" class="w-full p-2 border rounded text-sm h-12"></textarea>
          <textarea v-model="form.keluhan" placeholder="Keluhan" class="w-full p-2 border rounded text-sm h-12"></textarea>
        </div>
      </div>

      <div v-if="mode === 'JUAL'" class="bg-black p-4 text-center">
        <p class="text-green-400 text-xs font-mono uppercase mb-1">Total Tagihan</p>
        <h1 class="text-5xl font-mono text-green-500 tracking-tighter">
          {{ formatRupiah(grandTotal) }}
        </h1>
      </div>

      <div class="flex-1 overflow-y-auto p-2">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 sticky top-0">
            <tr>
              <th class="p-2 text-left">Produk</th>
              <th class="p-2 text-center w-16">Qty</th>
              <th class="p-2 text-right">Subtotal</th>
              <th class="p-2 text-center w-8"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in cart" :key="index" class="border-b">
              <td class="p-2">
                <div class="font-bold">{{ item.namaBarang }}</div>
                <div class="text-xs text-gray-500">@{{ formatRupiah(item.hargaSatuan) }}</div>
              </td>
              <td class="p-2">
                <input type="number" v-model="item.jumlah" class="w-full text-center border rounded" min="1">
              </td>
              <td class="p-2 text-right font-mono">{{ formatRupiah(item.jumlah * item.hargaSatuan) }}</td>
              <td class="p-2 text-center">
                <button @click="cart.splice(index, 1)" class="text-red-500"><i class="bi bi-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="p-4 bg-gray-50 border-t">
        <div class="flex justify-between mb-4 text-sm font-bold">
          <span>Item: {{ cart.length }}</span>
          <span v-if="mode === 'SERVICE'">Status: {{ form.status }}</span>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <button @click="cart = []" class="bg-red-500 text-white py-3 rounded font-bold uppercase text-xs">Batal [F2]</button>
          <button @click="processTransaction" :disabled="loading"
            :class="mode === 'JUAL' ? 'bg-blue-600' : 'bg-orange-500'"
            class="text-white py-3 rounded font-bold uppercase text-xs shadow-lg">
            {{ loading ? '...' : (mode === 'JUAL' ? 'Bayar [F8]' : 'Simpan') }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="mode === 'JUAL'" class="flex-1 flex flex-col bg-gray-200">
      <div class="p-4 bg-white shadow-sm flex gap-4">
        <div class="relative flex-1">
          <i class="bi bi-search absolute left-3 top-2.5 text-gray-400"></i>
          <input v-model="search" placeholder="Cari produk atau scan barcode..." 
                 class="w-full pl-10 pr-4 py-2 border rounded-full outline-none focus:ring-2 focus:ring-blue-400"/>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-4">
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          <div v-for="item in filteredItems" :key="item.id" 
               @click="addToCart(item)"
               class="bg-white rounded-lg shadow hover:shadow-md cursor-pointer transition transform active:scale-95 overflow-hidden group">
            <div class="h-32 bg-gray-100 flex items-center justify-center relative">
              <i class="bi bi-box text-4xl text-gray-300"></i>
              <div class="absolute top-0 right-0 bg-blue-600 text-white px-2 py-1 text-[10px] font-bold">
                STOK: {{ item.stok }}
              </div>
            </div>
            <div class="p-3">
              <div class="text-xs font-bold text-gray-800 h-8 line-clamp-2 mb-1 group-hover:text-blue-600">
                {{ item.nama }}
              </div>
              <div class="text-sm font-black text-blue-700">
                {{ formatRupiah(item.harga) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="mode === 'SERVICE'" class="flex-1 p-10 flex flex-col items-center justify-center text-gray-400">
      <i class="bi bi-laptop text-8xl mb-4"></i>
      <p class="text-xl font-bold">Mode Service Aktif</p>
      <p>Pilih sparepart dari pencarian atau input manual biaya jasa.</p>
      <button @click="openManualModal" class="mt-6 bg-yellow-500 text-white px-6 py-2 rounded-full font-bold shadow-lg">
        + Input Jasa / Biaya Manual
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import api from '../api';

const mode = ref('JUAL');
const items = ref([]);
const cart = ref([]);
const search = ref("");
const loading = ref(false);
const username = ref(localStorage.getItem('username') || 'Kasir');
const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", barangCustomer: "", keluhan: "", status: 'LUNAS' });

// Load Data
const fetchItems = async () => {
  try {
    const res = await api.get('/api/items');
    items.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
};

onMounted(fetchItems);

// Logic
const filteredItems = computed(() => {
  return items.value.filter(i => 
    i.nama.toLowerCase().includes(search.value.toLowerCase()) || 
    (i.kode && i.kode.toLowerCase().includes(search.value.toLowerCase()))
  );
});

const formatRupiah = (val) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0 }).format(val || 0);

const grandTotal = computed(() => cart.value.reduce((sum, item) => sum + (item.hargaSatuan * item.jumlah), 0));

const addToCart = (item) => {
  if(item.stok <= 0) return alert("Stok Habis!");
  const existing = cart.value.find(c => c.itemId === item.id);
  if(existing) {
    existing.jumlah++;
  } else {
    cart.value.push({ itemId: item.id, namaBarang: item.nama, hargaSatuan: item.harga, jumlah: 1, catatan: "" });
  }
};

const switchMode = (newMode) => {
  if(cart.value.length > 0 && !confirm("Daftar transaksi akan dikosongkan. Lanjut?")) return;
  mode.value = newMode;
  cart.value = [];
  form.value.status = newMode === 'SERVICE' ? 'PROSES' : 'LUNAS';
};

const processTransaction = async () => {
  if(cart.value.length === 0) return alert("Pilih barang dulu!");
  if(mode.value === 'SERVICE' && !form.value.customerNama) return alert("Nama Pelanggan wajib diisi!");
  
  loading.value = true;
  const payload = { ...form.value, tipe: mode.value, items: cart.value, kasirNama: username.value };
  try {
    await api.post('/api/nota', payload);
    alert("Transaksi Berhasil!");
    window.location.reload();
  } catch(e) {
    alert("Gagal: " + (e.response?.data?.message || e.message));
  } finally { loading.value = false; }
};
</script>