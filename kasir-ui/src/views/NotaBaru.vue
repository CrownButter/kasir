<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black">
    <div class="p-4 bg-white shadow-sm flex justify-between items-center flex-shrink-0 z-20 border-b">
      <div class="flex items-center gap-4">
        <h1 class="text-xl font-bold text-gray-800 flex items-center gap-2">
          <i class="bi bi-file-earmark-plus"></i> Form Transaksi
        </h1>
        <div class="flex bg-gray-100 p-1 rounded-full border">
          <button type="button" @click="switchMode('SERVICE')" 
            :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500 hover:bg-gray-200'"
            class="px-6 py-1.5 rounded-full text-xs font-bold transition-all flex items-center gap-2 uppercase">
            <i class="bi bi-tools"></i> Service
          </button>
          <button type="button" @click="switchMode('JUAL')" 
            :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500 hover:bg-gray-200'"
            class="px-6 py-1.5 rounded-full text-xs font-bold transition-all flex items-center gap-2 uppercase">
            <i class="bi bi-cart4"></i> Retail
          </button>
        </div>
      </div>
      <div class="text-right">
        <p class="text-[10px] text-gray-400 leading-none uppercase">Kasir</p>
        <p class="text-sm font-bold text-blue-600">{{ kasirNama }}</p>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-6">
      <div class="bg-white shadow-lg rounded-lg p-6 min-h-[500px]">
        <div class="flex justify-end mb-6">
          <div class="grid grid-cols-1 gap-2 w-full md:w-1/3 bg-gray-50 p-4 rounded border border-gray-200 shadow-sm">
            <div class="relative flex items-center">
              <i class="bi bi-person absolute left-3 text-gray-400"></i>
              <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full pl-9 p-2 border rounded text-sm font-bold outline-none focus:ring-1 focus:ring-orange-500" />
            </div>
            <div class="relative flex items-center">
              <i class="bi bi-whatsapp absolute left-3 text-gray-400"></i>
              <input v-model="form.customerTelp" placeholder="No Telp / WA" class="w-full pl-9 p-2 border rounded text-sm outline-none" />
            </div>
            <textarea v-model="form.customerAlamat" placeholder="Alamat Lengkap" class="w-full p-2 border rounded text-sm h-16 resize-none outline-none"></textarea>
          </div>
        </div>

        <div class="overflow-x-auto border rounded shadow-sm">
          <table class="w-full border-collapse">
            <thead class="bg-orange-600 text-white text-xs uppercase">
              <tr>
                <th class="p-3 border-r border-orange-500 w-10 text-center">No</th>
                <th class="p-3 border-r border-orange-500 text-left w-[20%]">Unit / Barang</th>
                <th class="p-3 border-r border-orange-500 text-left w-[15%]">Kerusakan</th>
                <th class="p-3 border-r border-orange-500 text-left w-[15%]">Solusi / Tindakan</th>
                <th class="p-3 border-r border-orange-500 text-left w-[20%] bg-orange-700">Ganti Sparepart (Stok)</th>
                <th class="p-3 border-r border-orange-500 w-12 text-center">Qty</th>
                <th class="p-3 border-r border-orange-500 text-right w-28">Harga</th>
                <th class="p-3 border-r border-orange-500 text-right w-28">Total</th>
                <th class="p-3 w-16 text-center">Aksi</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in rows" :key="index" class="border-b text-sm transition-colors hover:bg-orange-50/30">
                <td class="p-2 border-r text-center text-gray-500 font-mono text-xs">{{ row.isChild ? '' : getRowNumber(index) }}</td>
                <td class="p-1 border-r">
                  <input v-if="!row.isChild" v-model="row.unitName" class="w-full p-1 outline-none bg-transparent font-bold text-xs" placeholder="Contoh: Laptop Acer..." />
                  <div v-else class="text-center text-gray-300 font-bold leading-none">"</div>
                </td>
                <td class="p-1 border-r">
                  <input v-if="!row.isChild" v-model="row.kerusakan" class="w-full p-1 outline-none bg-transparent text-xs" placeholder="Mati total..." />
                  <div v-else class="text-center text-gray-300 font-bold leading-none">"</div>
                </td>
                <td class="p-1 border-r">
                  <input v-model="row.solusi" class="w-full p-1 outline-none bg-transparent text-xs text-blue-700 font-medium" placeholder="Install Ulang..." />
                </td>
                <td class="p-1 border-r bg-yellow-50/50 relative">
                  <ItemSearchInput v-model="row.namaBarang" :items="masterItems" @select="(item) => onItemSelect(row, item)" />
                  <span v-if="row.itemId" class="absolute right-1 top-1 text-[8px] bg-green-600 text-white px-1 rounded font-bold uppercase">STOK</span>
                </td>
                <td class="p-1 border-r">
                  <input type="number" v-model="row.qty" class="w-full text-center bg-transparent outline-none" min="1" />
                </td>
                <td class="p-1 border-r">
                  <input type="number" v-model="row.harga" class="w-full text-right bg-transparent outline-none font-mono" />
                </td>
                <td class="p-2 border-r text-right font-bold bg-gray-50/50">{{ formatNumber(row.qty * row.harga) }}</td>
                <td class="p-1 text-center">
                  <div class="flex gap-1 justify-center">
                    <button type="button" v-if="!row.isChild" @click="addSolution(index)" class="bg-green-100 text-green-700 p-1 rounded hover:bg-green-200 shadow-sm" title="Tambah Part"><i class="bi bi-plus-lg text-xs"></i></button>
                    <button type="button" @click="removeRow(index)" class="bg-red-50 text-red-500 p-1 rounded hover:bg-red-100 shadow-sm"><i class="bi bi-trash-fill text-xs"></i></button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="mt-4">
          <button type="button" @click="addNewUnit" class="bg-blue-600 text-white px-6 py-2 rounded-lg font-bold shadow flex items-center gap-2 hover:bg-blue-700 transition">
            <i class="bi bi-plus-circle-fill"></i> Tambah Unit / Perangkat Baru
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex-1 flex overflow-hidden">
      <div class="w-full md:w-2/5 flex flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        <div class="bg-black p-4 text-center border-b border-gray-800 flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase mb-1">Total Belanja (IDR)</p>
          <h1 class="text-5xl font-mono text-green-400 tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div class="bg-gray-50 p-3 rounded border grid grid-cols-2 gap-2 shadow-inner">
            <input v-model="form.customerNama" placeholder="Customer *" class="border p-2 rounded text-xs font-bold bg-white" />
            <input v-model="form.customerTelp" placeholder="No Telp" class="border p-2 rounded text-xs bg-white" />
          </div>

          <div class="space-y-2">
            <div v-for="(row, index) in rows" :key="index" class="p-3 bg-white border rounded shadow-sm flex justify-between items-start group hover:border-blue-300">
              <div class="flex-1 min-w-0">
                <p class="font-bold text-sm text-gray-800 truncate">{{ row.namaBarang || 'Barang Manual' }}</p>
                <div class="flex items-center gap-2 mt-1">
                  <input type="number" v-model="row.qty" class="w-12 border-b border-gray-300 text-center text-xs outline-none focus:border-blue-500" min="1" />
                  <span class="text-xs text-gray-400">x</span>
                  <span class="text-xs font-mono text-gray-600">{{ formatNumber(row.harga) }}</span>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
                <button type="button" @click="removeRow(index)" class="text-red-400 hover:text-red-600 mt-1 transition"><i class="bi bi-trash-fill"></i></button>
              </div>
            </div>
            <div v-if="rows.length === 0" class="text-center py-24 text-gray-300 italic text-sm">
              <i class="bi bi-cart-x text-6xl block mb-2 opacity-20"></i> Klik katalog di sebelah kanan
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-200 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm flex gap-3 flex-shrink-0">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-4 top-3 text-gray-400"></i>
            <input v-model="search" placeholder="Cari nama barang atau scan barcode..." class="w-full pl-12 pr-4 py-3 border rounded-full bg-gray-50 focus:ring-2 focus:ring-blue-400 outline-none transition-all" />
          </div>
          <button type="button" @click="addNewUnit" class="bg-gray-800 text-white px-6 rounded-full font-bold text-xs uppercase hover:bg-black transition">+ Tambah Jasa</button>
        </div>

        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)"
              class="bg-white rounded-xl shadow-sm border-2 border-transparent hover:border-blue-500 cursor-pointer overflow-hidden transition-all transform active:scale-95 group">
              <div class="h-28 bg-gray-50 flex items-center justify-center relative">
                <i class="bi bi-box text-3xl text-gray-300 group-hover:scale-110 transition-transform"></i>
                <span class="absolute top-2 right-2 bg-blue-100 text-blue-700 text-[10px] px-2 py-0.5 rounded-full font-bold">Stok: {{ item.stok }}</span>
              </div>
              <div class="p-3 bg-white border-t border-gray-50">
                <h4 class="text-xs font-bold text-gray-700 line-clamp-2 h-8 leading-tight group-hover:text-blue-600 transition-colors">{{ item.nama }}</h4>
                <div class="text-sm font-black text-blue-700 text-right mt-2">{{ formatNumber(item.harga) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 bg-white border-t shadow-[0_-4px_10px_rgba(0,0,0,0.05)] z-20 flex-shrink-0">
      <div class="container mx-auto flex flex-col md:flex-row justify-end items-center gap-8 px-4">
        <div class="flex items-center gap-3 bg-gray-50 p-2 px-5 rounded-xl border border-gray-200">
          <span class="font-bold text-gray-500 text-xs uppercase">Uang Muka (DP):</span>
          <div class="flex items-center gap-1 font-bold text-blue-800">
            <span class="text-sm">Rp</span>
            <input v-model="form.dp" type="number" class="w-32 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-lg" />
          </div>
        </div>
        <div class="text-right">
          <p class="text-[10px] font-bold text-gray-400 uppercase leading-none mb-1">Sisa Pembayaran</p>
          <p class="text-2xl font-black text-red-600 font-mono leading-none">Rp {{ formatNumber(grandTotal - form.dp) }}</p>
        </div>
        <button type="button" @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" class="w-full md:w-auto px-16 py-3 text-white rounded-xl font-bold shadow-lg flex items-center justify-center gap-2 transition transform active:scale-95 uppercase tracking-wider">
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

const form = ref({
  customerNama: "", customerTelp: "", customerAlamat: "",
  tipe: 'SERVICE', dp: 0
});

const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
});

const filteredItems = computed(() => {
  return masterItems.value.filter(i => 
    i.nama.toLowerCase().includes(search.value.toLowerCase()) ||
    (i.kode && i.kode.toLowerCase().includes(search.value.toLowerCase()))
  );
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const switchMode = (mode) => {
  if(rows.value.length > 0 && rows.value[0].namaBarang && !confirm("Daftar transaksi akan dikosongkan. Ganti mode?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const onProductClick = (item) => {
  if(item.stok <= 0) return alert("Stok Habis!");
  if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
  rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, catatan: "", qty: 1, harga: item.harga, itemId: item.id, isChild: false });
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
  
  const itemsPayload = rows.value.map(r => {
    let catatanStr = "";
    if (form.value.tipe === 'SERVICE') {
       catatanStr = `Unit: ${r.unitName || '-'} | Kondisi: ${r.kerusakan || '-'}`;
    } else {
       catatanStr = r.catatan || 'Penjualan Retail';
    }
    
    return {
      itemId: r.itemId,
      namaBarang: r.namaBarang || r.solusi || 'Item',
      catatan: catatanStr,
      hargaSatuan: r.harga,
      jumlah: r.qty
    };
  });

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
    alert("Nota Berhasil Disimpan!");
    router.push('/');
  } catch(e) { 
    alert("Gagal: " + (e.response?.data?.message || e.message)); 
  }
};
</script>

<style scoped>
/* Scrollbar halus untuk katalog */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>