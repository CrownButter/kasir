<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black">
    <div class="p-4 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-20 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase">Service</button>
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase">Retail</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-6">
      <div class="bg-white shadow-lg rounded-lg p-6 min-h-[500px]">
        <div class="flex justify-end mb-6">
          <div class="grid grid-cols-1 gap-2 w-full md:w-1/3 bg-gray-50 p-4 rounded border border-gray-200 shadow-sm">
            <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full p-2 border rounded text-sm font-bold outline-none" />
            <input v-model="form.customerTelp" placeholder="No Telp / WA" class="w-full p-2 border rounded text-sm outline-none" />
            <textarea v-model="form.customerAlamat" placeholder="Alamat Lengkap" class="w-full p-2 border rounded text-sm h-16 resize-none outline-none"></textarea>
          </div>
        </div>
        <div class="overflow-x-auto border rounded shadow-sm">
          <table class="w-full border-collapse">
            <thead class="bg-orange-600 text-white text-xs uppercase">
              <tr>
                <th class="p-3 border-r border-orange-500 w-10">No</th>
                <th class="p-3 border-r border-orange-500 text-left">Unit</th>
                <th class="p-3 border-r border-orange-500 text-left">Kerusakan</th>
                <th class="p-3 border-r border-orange-500 text-left">Tindakan</th>
                <th class="p-3 border-r border-orange-500 text-left bg-orange-700">Sparepart (Stok)</th>
                <th class="p-3 border-r border-orange-500 w-12">Qty</th>
                <th class="p-3 border-r border-orange-500 text-right">Harga</th>
                <th class="p-3 border-r border-orange-500 text-right">Total</th>
                <th class="p-3 w-12 text-center">Aksi</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in rows" :key="index" class="border-b text-sm transition-colors hover:bg-orange-50/30">
                <td class="p-2 border-r text-center text-gray-500 font-mono text-xs">{{ row.isChild ? '' : getRowNumber(index) }}</td>
                <td class="p-1 border-r"><input v-if="!row.isChild" v-model="row.unitName" class="w-full p-1 outline-none font-bold text-xs" /></td>
                <td class="p-1 border-r"><input v-if="!row.isChild" v-model="row.kerusakan" class="w-full p-1 outline-none text-xs" /></td>
                <td class="p-1 border-r"><input v-model="row.solusi" class="w-full p-1 outline-none text-xs text-blue-700 font-medium" /></td>
                <td class="p-1 border-r bg-yellow-50/50"><ItemSearchInput v-model="row.namaBarang" :items="masterItems" @select="(item) => onItemSelect(row, item)" /></td>
                <td class="p-1 border-r text-center"><input type="number" v-model="row.qty" class="w-full text-center outline-none" min="1" /></td>
                <td class="p-1 border-r"><input type="number" v-model="row.harga" class="w-full text-right outline-none font-mono" /></td>
                <td class="p-2 border-r text-right font-bold bg-gray-50/50">{{ formatNumber(row.qty * row.harga) }}</td>
                <td class="p-1 text-center">
                  <button v-if="!row.isChild" @click="addSolution(index)" class="text-green-600 mr-2"><i class="bi bi-plus-lg"></i></button>
                  <button @click="removeRow(index)" class="text-red-500"><i class="bi bi-trash-fill"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-else class="flex-1 flex overflow-hidden">
      <div class="w-full md:w-2/5 flex flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        <div class="bg-black p-4 text-center border-b border-gray-800 flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase mb-1">Total Tagihan (IDR)</p>
          <h1 class="text-5xl font-mono text-green-400 tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div class="bg-gray-50 p-3 rounded border grid grid-cols-2 gap-2 shadow-inner">
            <input v-model="form.customerNama" placeholder="Customer *" class="border p-2 rounded text-xs font-bold" />
            <input v-model="form.customerTelp" placeholder="No Telp" class="border p-2 rounded text-xs" />
          </div>
          <div class="space-y-2">
            <div v-for="(row, index) in rows" :key="index" class="p-3 bg-white border rounded shadow-sm flex justify-between items-start group">
              <div class="flex-1 min-w-0">
                <p class="font-bold text-sm text-gray-800 truncate">{{ row.namaBarang || 'Manual Item' }}</p>
                <div class="flex items-center gap-2 mt-1">
                  <input type="number" v-model="row.qty" class="w-12 border-b text-center text-xs outline-none" min="1" />
                  <span class="text-xs text-gray-400">x {{ formatNumber(row.harga) }}</span>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
                <button type="button" @click="removeRow(index)" class="text-red-400"><i class="bi bi-trash-fill"></i></button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-200 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm flex gap-3 flex-shrink-0">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-4 top-3 text-gray-400"></i>
            <input v-model="search" placeholder="Cari barang..." class="w-full pl-12 pr-4 py-3 border rounded-full bg-gray-50" />
          </div>
          <button @click="addNewUnit" class="bg-gray-800 text-white px-6 rounded-full font-bold text-xs">+ Jasa</button>
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" class="bg-white rounded-xl shadow-sm border-2 border-transparent hover:border-blue-500 cursor-pointer overflow-hidden transition transform active:scale-95 group">
              <div class="h-28 bg-gray-50 flex items-center justify-center relative">
                <img v-if="item.imageUrl" :src="'http://localhost:8080' + item.imageUrl" class="w-full h-full object-cover" />
                <i v-else class="bi bi-box text-3xl text-gray-300"></i>
                <span class="absolute top-2 right-2 bg-blue-100 text-blue-700 text-[10px] px-2 py-0.5 rounded-full font-bold">Stok: {{ item.stok }}</span>
              </div>
              <div class="p-3 bg-white border-t border-gray-50">
                <h4 class="text-xs font-bold text-gray-700 line-clamp-2 h-8">{{ item.nama }}</h4>
                <div class="text-sm font-black text-blue-700 text-right mt-2">{{ formatNumber(item.harga) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 bg-white border-t shadow-lg z-20 flex-shrink-0">
      <div class="container mx-auto flex flex-col md:flex-row justify-end items-center gap-8 px-4">
        <div class="flex items-center gap-3 bg-gray-50 p-2 px-5 rounded-xl border">
          <span class="font-bold text-gray-500 text-xs uppercase">DP:</span>
          <input v-model="form.dp" type="number" class="w-32 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-lg font-bold" />
        </div>
        <div class="text-right">
          <p class="text-[10px] font-bold text-gray-400 uppercase leading-none mb-1">Sisa Bayar</p>
          <p class="text-2xl font-black text-red-600 font-mono leading-none">Rp {{ formatNumber(grandTotal - form.dp) }}</p>
        </div>
        <button @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" class="w-full md:w-auto px-16 py-3 text-white rounded-xl font-bold shadow-lg flex items-center justify-center gap-2 transition transform active:scale-95 uppercase">
          <i class="bi bi-save2"></i> Simpan Nota
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';
import ItemSearchInput from '../components/ItemSearchInput.vue';

const router = useRouter();
const masterItems = ref([]);
const search = ref("");
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'SERVICE', dp: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
});

const filteredItems = computed(() => masterItems.value.filter(i => i.nama.toLowerCase().includes(search.value.toLowerCase())));
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const switchMode = (mode) => {
  if(rows.value.length > 0 && rows.value[0].namaBarang && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const onProductClick = (item) => {
  if(item.stok <= 0) return alert("Stok Habis!");
  if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
  rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });
const addSolution = (index) => rows.value.splice(index + 1, 0, { unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: true });
const removeRow = (index) => {
  rows.value.splice(index, 1);
  if(rows.value.length === 0) addNewUnit();
};

const onItemSelect = (row, item) => {
  row.namaBarang = item.nama;
  row.itemId = item.id;
  row.harga = item.harga;
};

const getRowNumber = (currentIndex) => {
  let count = 0;
  for(let i=0; i<= currentIndex; i++) { if(!rows.value[i].isChild) count++; }
  return count;
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));

const saveNota = async () => {
  if(!form.value.customerNama) return alert("Nama Customer wajib diisi!");
  const itemsPayload = rows.value.map(r => ({
    itemId: r.itemId,
    namaBarang: r.namaBarang || r.solusi || 'Item',
    catatan: form.value.tipe === 'SERVICE' ? `Unit: ${r.unitName} | Kondisi: ${r.kerusakan}` : 'Retail',
    hargaSatuan: r.harga,
    jumlah: r.qty
  }));

  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : 'LUNAS',
    barangCustomer: form.value.tipe === 'SERVICE' ? rows.value[0].unitName : (itemsPayload[0]?.namaBarang || 'Retail'),
    keluhan: form.value.tipe === 'SERVICE' ? rows.value[0].kerusakan : '-',
    items: itemsPayload
  };

  try {
    await api.post('/api/nota', payload);
    alert("Berhasil!");
    router.push('/');
  } catch(e) { alert("Gagal"); }
};
</script>

<style scoped>
::-webkit-scrollbar { width: 4px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>