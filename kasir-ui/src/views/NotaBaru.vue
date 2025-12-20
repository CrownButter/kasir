<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black">
    <div class="p-4 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-20 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase tracking-wider">Retail</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase tracking-wider">Service</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-8 bg-gray-50">
      <div class="max-w-5xl mx-auto space-y-8">
        
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center gap-3 mb-6 border-b pb-4">
            <div class="w-10 h-10 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center">
              <i class="bi bi-person-badge-fill text-xl"></i>
            </div>
            <div>
              <h2 class="font-bold text-gray-800">Informasi Pelanggan</h2>
              <p class="text-[10px] text-gray-400 uppercase tracking-widest font-bold">Customer Details</p>
            </div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Nama Lengkap *</label>
              <input v-model="form.customerNama" placeholder="Masukkan nama pelanggan" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition font-bold" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Nomor WhatsApp</label>
              <input v-model="form.customerTelp" placeholder="0812xxxx" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Alamat</label>
              <input v-model="form.customerAlamat" placeholder="Alamat singkat" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition" />
            </div>
          </div>
        </div>

        <div class="space-y-6">
          <div v-for="(row, index) in rows" :key="index">
            <div v-if="!row.isChild" class="bg-white rounded-2xl shadow-md border border-gray-200 overflow-hidden transition-all hover:shadow-lg">
              
              <div class="bg-gradient-to-r from-orange-600 to-orange-500 p-4 px-6 flex justify-between items-center text-white">
                <div class="flex items-center gap-3">
                  <span class="bg-white/20 w-8 h-8 flex items-center justify-center rounded-lg text-sm font-black">
                    #{{ getRowNumber(index) }}
                  </span>
                  <h3 class="font-bold uppercase tracking-wider text-sm">Unit Service</h3>
                </div>
                <button @click="removeRow(index)" class="hover:bg-red-500 p-2 rounded-lg transition">
                  <i class="bi bi-trash3-fill"></i>
                </button>
              </div>

              <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500 flex items-center gap-2">
                      <i class="bi bi-laptop"></i> NAMA UNIT / MODEL
                    </label>
                    <input v-model="row.unitName" placeholder="Contoh: Asus ROG Strix G15" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 focus:bg-white outline-none transition font-bold" />
                  </div>
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500 flex items-center gap-2">
                      <i class="bi bi-exclamation-octagon"></i> KELUHAN / KERUSAKAN
                    </label>
                    <input v-model="row.kerusakan" placeholder="Contoh: No Display / Engsel Patah" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 focus:bg-white outline-none transition" />
                  </div>
                </div>

                <div class="space-y-4">
                  <div class="flex justify-between items-center border-b pb-2">
                    <label class="text-xs font-black text-orange-600 uppercase tracking-widest">Tindakan & Sparepart</label>
                    <button @click="addSolution(index)" class="text-[10px] bg-orange-100 text-orange-700 px-3 py-1 rounded-full font-bold hover:bg-orange-200 transition">
                      <i class="bi bi-plus-lg"></i> Tambah Tindakan
                    </button>
                  </div>

                  <div class="space-y-3">
                    <div v-for="(subRow, subIndex) in getRelatedRows(index)" :key="subIndex" 
                         class="flex flex-col md:flex-row gap-3 p-4 bg-gray-50 rounded-xl border border-gray-100 items-end group relative">
                      
                      <div class="flex-1 w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Tindakan / Solusi</label>
                        <input v-model="subRow.solusi" placeholder="Tindakan..." class="w-full p-2 bg-white border rounded-lg text-sm outline-none focus:border-orange-500" />
                      </div>

                      <div class="flex-[1.5] w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Sparepart (Opsional)</label>
                        <ItemSearchInput v-model="subRow.namaBarang" :items="masterItems" @select="(item) => onItemSelect(subRow, item)" />
                      </div>

                      <div class="w-20">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-center block">Qty</label>
                        <input type="number" v-model="subRow.qty" class="w-full p-2 bg-white border rounded-lg text-sm text-center outline-none" min="1" />
                      </div>

                      <div class="w-32">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-right block">Harga (Rp)</label>
                        <input type="number" v-model="subRow.harga" class="w-full p-2 bg-white border rounded-lg text-sm text-right font-mono font-bold outline-none" />
                      </div>

                      <div v-if="subRow.isChild" class="pb-1">
                        <button @click="removeRow(index + subIndex)" class="p-2 text-red-400 hover:text-red-600 transition">
                          <i class="bi bi-x-circle-fill"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="flex justify-end pt-4 border-t border-dashed">
                  <div class="text-right">
                    <p class="text-[10px] text-gray-400 font-bold uppercase">Subtotal Unit</p>
                    <p class="text-xl font-black text-orange-600 font-mono">Rp {{ formatNumber(calculateUnitTotal(index)) }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <button @click="addNewUnit" class="w-full p-6 border-2 border-dashed border-gray-300 rounded-3xl text-gray-400 hover:text-orange-600 hover:border-orange-400 hover:bg-orange-50 transition-all flex flex-col items-center justify-center gap-2 group">
            <i class="bi bi-plus-circle-dotted text-3xl group-hover:scale-110 transition"></i>
            <span class="font-black uppercase text-xs tracking-widest">Tambah Unit Service Baru</span>
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex-1 flex overflow-hidden">
      <div class="w-full md:w-1/2 flex flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        <div class="bg-black p-4 text-center border-b border-gray-800 flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase mb-1">Total Tagihan (IDR)</p>
          <h1 class="text-5xl font-mono text-green-400 tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>
        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div class="bg-gray-50 p-3 rounded-xl border grid grid-cols-2 gap-4 shadow-inner">
            <input v-model="form.customerNama" placeholder="Nama Customer *" class="border p-3 rounded-lg text-xs font-bold focus:ring-2 focus:ring-blue-100 outline-none" />
            <input v-model="form.customerTelp" placeholder="No Telp" class="border p-3 rounded-lg text-xs focus:ring-2 focus:ring-blue-100 outline-none" />
          </div>
          <div class="space-y-2">
            <div v-for="(row, index) in rows" :key="index" class="p-4 bg-white border rounded-xl shadow-sm flex justify-between items-start group hover:border-blue-300 transition">
              <div class="flex-1 min-w-0">
                <p class="font-bold text-sm text-gray-800 truncate">{{ row.namaBarang || 'Manual Item' }}</p>
                <div class="flex items-center gap-3 mt-1">
                  <div class="flex items-center border rounded-lg overflow-hidden">
                    <button @click="row.qty > 1 ? row.qty-- : null" class="px-2 py-1 bg-gray-100 text-gray-500 text-xs">-</button>
                    <input type="number" v-model="row.qty" class="w-10 text-center text-xs outline-none font-bold" min="1" />
                    <button @click="row.qty++" class="px-2 py-1 bg-gray-100 text-gray-500 text-xs">+</button>
                  </div>
                  <span class="text-xs text-gray-400">x {{ formatNumber(row.harga) }}</span>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
                <button type="button" @click="removeRow(index)" class="text-red-300 hover:text-red-500 transition"><i class="bi bi-trash-fill"></i></button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-200 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm flex gap-3 flex-shrink-0">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-4 top-3 text-gray-400"></i>
            <input v-model="search" placeholder="Cari barang atau kode produk..." class="w-full pl-12 pr-4 py-3 border rounded-full bg-gray-50 focus:bg-white focus:ring-2 focus:ring-blue-100 outline-none transition" />
          </div>
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" 
                 class="bg-white rounded-2xl shadow-sm border-2 border-transparent hover:border-blue-500 cursor-pointer overflow-hidden transition transform active:scale-95 group relative">
              
              <span :class="[
                  'absolute top-2 right-2 px-2 py-0.5 rounded-full font-bold text-[10px] z-10 shadow-sm',
                  item.stok <= 0 ? 'bg-red-600 text-white' : 
                  item.stok < 5 ? 'bg-orange-500 text-white animate-pulse' : 
                  'bg-blue-100 text-blue-700'
              ]">
                Stok: {{ item.stok }}
              </span>

              <div class="h-32 bg-gray-50 flex items-center justify-center overflow-hidden">
                <img v-if="item.imageUrl"
                     :src="getImageUrl(item.imageUrl)"
                     class="w-full h-full object-cover" />

                <i v-else class="bi bi-box text-3xl text-gray-300"></i>
              </div>
              <div class="p-3 bg-white border-t border-gray-50">
                <h4 class="text-[11px] font-bold text-gray-700 line-clamp-2 h-8 uppercase">{{ item.nama }}</h4>
                <div class="text-sm font-black text-blue-700 text-right mt-2">{{ formatNumber(item.harga) }}</div>
              </div>
              
              <div v-if="item.stok <= 0" class="absolute inset-0 bg-white/60 flex items-center justify-center backdrop-blur-[1px]">
                 <span class="bg-red-600 text-white text-[10px] px-3 py-1 rounded-full font-black uppercase rotate-12 shadow-lg">Habis</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-6 bg-white border-t shadow-[0_-4px_10px_rgba(0,0,0,0.05)] z-20 flex-shrink-0">
      <div class="container mx-auto flex flex-col md:flex-row justify-end items-center gap-8">
        <div class="flex items-center gap-4 bg-gray-50 p-2 px-6 rounded-2xl border">
          <span class="font-black text-gray-400 text-[10px] uppercase">Titipan (DP)</span>
          <input v-model="form.dp" type="number" class="w-32 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-xl font-black text-gray-700" />
        </div>
        <div class="text-right">
          <p class="text-[10px] font-black text-gray-400 uppercase tracking-widest leading-none mb-2">Sisa Pembayaran</p>
          <p class="text-3xl font-black text-red-600 font-mono leading-none">Rp {{ formatNumber(grandTotal - form.dp) }}</p>
        </div>
        <button @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" class="w-full md:w-auto px-12 py-4 text-white rounded-2xl font-black shadow-lg flex items-center justify-center gap-3 transition transform active:scale-95 uppercase tracking-wider">
          <i class="bi bi-shield-check text-xl"></i> Simpan Transaksi
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

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
});

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  return masterItems.value.filter(i => 
    i.nama.toLowerCase().includes(s) || 
    i.kode.toLowerCase().includes(s)
  );
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const switchMode = (mode) => {
  if(rows.value.length > 0 && (rows.value[0].namaBarang || rows.value[0].unitName) && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

// --- LOGIC SERVICE (CARD HELPERS) ---
const getRelatedRows = (mainIndex) => {
  const related = [rows.value[mainIndex]];
  for (let i = mainIndex + 1; i < rows.value.length; i++) {
    if (rows.value[i].isChild) related.push(rows.value[i]);
    else break;
  }
  return related;
};

const calculateUnitTotal = (mainIndex) => {
  const related = getRelatedRows(mainIndex);
  return related.reduce((sum, r) => sum + (r.qty * r.harga), 0);
};

// --- ACTION HANDLERS ---
const onProductClick = (item) => {
  if(item.stok <= 0) return alert("Maaf, stok barang habis!");
  if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
  rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
};

const onItemSelect = (row, item) => {
  if(item.stok <= 0) {
    alert("Maaf, stok sparepart ini habis!");
    row.namaBarang = ""; row.itemId = null; return;
  }
  row.namaBarang = item.nama;
  row.itemId = item.id;
  row.harga = item.harga;
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });
const addSolution = (index) => {
  // Mencari index terakhir dari child unit ini
  let lastChildIndex = index;
  for(let i = index + 1; i < rows.value.length; i++) {
    if(rows.value[i].isChild) lastChildIndex = i;
    else break;
  }
  rows.value.splice(lastChildIndex + 1, 0, { unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: true });
};

const removeRow = (index) => {
  const rowToDelete = rows.value[index];
  if(!rowToDelete.isChild) {
    // Jika menghapus unit utama, hapus juga semua child-nya
    let countToDelete = 1;
    for(let i = index + 1; i < rows.value.length; i++) {
      if(rows.value[i].isChild) countToDelete++;
      else break;
    }
    rows.value.splice(index, countToDelete);
  } else {
    rows.value.splice(index, 1);
  }
  if(rows.value.length === 0) addNewUnit();
};

const getRowNumber = (currentIndex) => {
  let count = 0;
  for(let i=0; i<= currentIndex; i++) { if(!rows.value[i].isChild) count++; }
  return count;
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));

const saveNota = async () => {
  if(!form.value.customerNama) return alert("Nama Customer wajib diisi!");
  const validItems = rows.value.filter(r => r.namaBarang || r.solusi || r.unitName);
  if(validItems.length === 0) return alert("Tambahkan minimal satu unit atau barang!");

  const itemsPayload = rows.value.map(r => ({
    itemId: r.itemId,
    namaBarang: r.namaBarang || r.solusi || 'Biaya Service',
    catatan: form.value.tipe === 'SERVICE' ? `Unit: ${r.unitName || '-'} | Keluhan: ${r.kerusakan || '-'}` : 'Retail',
    hargaSatuan: r.harga,
    jumlah: r.qty
  }));

  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : 'LUNAS',
    barangCustomer: form.value.tipe === 'SERVICE' ? (rows.value[0].unitName || 'Unit Service') : (itemsPayload[0]?.namaBarang || 'Retail'),
    keluhan: form.value.tipe === 'SERVICE' ? (rows.value[0].kerusakan || '-') : '-',
    items: itemsPayload
  };

  try {
    await api.post('/api/nota', payload);
    alert("Nota Berhasil Disimpan!");
    router.push('/');
  } catch(e) { alert("Gagal Simpan"); }
};
</script>

<style scoped>
::-webkit-scrollbar { width: 5px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }
::-webkit-scrollbar-thumb:hover { background: #9ca3af; }

.animate-pulse {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .6; }
}
</style>