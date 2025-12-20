<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black relative">
    
    <transition name="toast">
      <div v-if="toast.show" class="fixed top-20 left-1/2 -translate-x-1/2 z-[100] bg-red-600 text-white px-6 py-3 rounded-2xl shadow-2xl flex items-center gap-3 border-2 border-white/20 backdrop-blur-md">
        <i class="bi bi-exclamation-triangle-fill text-xl"></i>
        <span class="font-bold text-sm">{{ toast.message }}</span>
      </div>
    </transition>

    <div class="p-3 md:p-4 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-40 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase tracking-wider">Retail / Jual</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase tracking-wider">Service / Perbaikan</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-8 bg-gray-50">
      <div class="max-w-5xl mx-auto space-y-8 pb-32">
        
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center gap-3 mb-6 border-b pb-4">
            <div class="w-10 h-10 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center"><i class="bi bi-person-badge-fill text-xl"></i></div>
            <div>
              <h2 class="font-bold text-gray-800 uppercase">Informasi Pelanggan</h2>
              <p class="text-[10px] text-gray-400 font-bold uppercase tracking-widest">Customer Details</p>
            </div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Nama Lengkap *</label>
              <input v-model="form.customerNama" placeholder="Nama Pelanggan" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition font-bold" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Nomor WhatsApp</label>
              <input v-model="form.customerTelp" placeholder="08..." class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Alamat</label>
              <input v-model="form.customerAlamat" placeholder="Alamat singkat" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition" />
            </div>
          </div>
        </div>

        <div class="space-y-6">
          <div v-for="(row, index) in rows" :key="index">
            <div v-if="!row.isChild" class="bg-white rounded-2xl shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition">
              <div class="bg-gradient-to-r from-orange-600 to-orange-500 p-4 px-6 flex justify-between items-center text-white">
                <div class="flex items-center gap-3">
                  <span class="bg-white/20 w-8 h-8 flex items-center justify-center rounded-lg text-sm font-black">#{{ getRowNumber(index) }}</span>
                  <h3 class="font-bold uppercase tracking-wider text-sm">Unit Service</h3>
                </div>
                <button @click="removeRow(index)" class="hover:bg-red-500 p-2 rounded-lg transition"><i class="bi bi-trash3-fill"></i></button>
              </div>

              <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500 uppercase">Nama Unit / Model</label>
                    <input v-model="row.unitName" placeholder="Contoh: Asus ROG Strix" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 focus:bg-white outline-none transition font-bold" />
                  </div>
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500 uppercase">Keluhan / Kerusakan</label>
                    <input v-model="row.kerusakan" placeholder="Contoh: No Display" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 focus:bg-white outline-none transition" />
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
                    <div v-for="(subRow, subIndex) in getRelatedRows(index)" :key="subIndex" class="flex flex-col md:flex-row gap-3 p-4 bg-gray-50 rounded-xl border border-gray-100 items-end">
                      <div class="flex-1 w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Tindakan / Jasa</label>
                        <input v-model="subRow.solusi" placeholder="Tindakan..." class="w-full p-2 bg-white border rounded-lg text-sm outline-none focus:border-orange-500" />
                      </div>
                      <div class="flex-[1.5] w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Sparepart (Opsional)</label>
                        <select v-model="subRow.itemId" @change="onSparepartSelect(subRow)" class="w-full p-2 bg-white border rounded-lg text-sm outline-none">
                          <option :value="null">-- Tidak Ada Sparepart --</option>
                          <option v-for="item in masterItems" :key="item.id" :value="item.id">{{ item.nama }} ({{ formatNumber(item.harga) }})</option>
                        </select>
                      </div>
                      <div class="w-20">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-center block">Qty</label>
                        <input type="number" v-model="subRow.qty" class="w-full p-2 bg-white border rounded-lg text-sm text-center" min="1" />
                      </div>
                      <div class="w-32">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-right block">Biaya (Rp)</label>
                        <input 
                          type="text" 
                          :value="formatNumber(subRow.harga)" 
                          @input="onInputMoney($event, 'row_harga', {index: rows.indexOf(subRow)})"
                          class="w-full p-2 bg-white border rounded-lg text-sm text-right font-mono font-bold outline-none" 
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <button @click="addNewUnit" class="w-full p-6 border-2 border-dashed border-gray-300 rounded-3xl text-gray-400 hover:text-orange-600 hover:bg-orange-50 transition flex flex-col items-center justify-center gap-2">
            <i class="bi bi-plus-circle-dotted text-3xl"></i>
            <span class="font-black uppercase text-[10px] tracking-widest">Tambah Unit Service Baru</span>
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex-1 flex overflow-hidden">
      
      <div class="w-full md:w-1/2 flex flex-col bg-white border-r shadow-xl z-20 overflow-hidden">
        <div class="bg-slate-900 p-5 text-center flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase tracking-[0.2em] mb-1">Total Belanja</p>
          <h1 class="text-4xl font-mono text-green-400 font-black tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50/50">
          <div class="space-y-1 mb-4">
             <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Nama Pelanggan</label>
             <input v-model="form.customerNama" placeholder="Pelanggan Umum *" class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-blue-500 bg-white shadow-sm" />
          </div>

          <div v-for="(row, index) in rows" :key="index" v-show="row.namaBarang && row.itemId" class="flex justify-between items-center p-3 bg-white rounded-2xl shadow-sm border border-transparent hover:border-blue-200 transition">
            <div class="flex-1">
              <p class="font-bold text-sm text-slate-800 uppercase line-clamp-1">{{ row.namaBarang }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="bg-blue-100 text-blue-700 text-[10px] px-2 py-0.5 rounded font-black">{{ row.qty }}x</span>
                <span class="text-[10px] text-slate-400 font-mono">@ {{ formatNumber(row.harga) }}</span>
              </div>
            </div>
            <div class="text-right">
               <p class="font-black text-sm text-slate-900 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
               <button @click="removeRow(index)" class="text-red-300 hover:text-red-500 p-1"><i class="bi bi-trash3-fill"></i></button>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-100 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm border-b">
          <div class="relative">
            <i class="bi bi-search absolute left-5 top-4 text-slate-400"></i>
            <input v-model="search" @keyup.enter="handleBarcodeEnter" ref="barcodeField" placeholder="Scan Barcode atau Cari Nama Barang..." class="w-full pl-12 p-4 border-none rounded-2xl bg-gray-100 outline-none focus:bg-white focus:ring-4 focus:ring-blue-100 transition text-sm font-bold shadow-inner" />
            <div class="absolute right-5 top-4"><span class="text-[9px] bg-blue-600 text-white px-2 py-1 rounded font-black animate-pulse">SCANNER AKTIF</span></div>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-5">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" :class="{'shake-anim': item.isShaking}" class="bg-white p-3 rounded-[2.5rem] border-4 border-transparent hover:border-blue-500 cursor-pointer shadow-md relative active:scale-95 transition-all text-center">
              <span :class="['absolute top-4 right-4 px-2 py-0.5 rounded-full font-black text-[9px] border', item.stok <= 0 ? 'bg-red-600 text-white border-red-700' : 'bg-blue-100 text-blue-700']">STK: {{ item.stok }}</span>
              <div class="w-full aspect-square bg-gray-50 rounded-[2rem] mb-2 flex items-center justify-center overflow-hidden border">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover" />
                <i v-else class="bi bi-box-seam text-3xl text-gray-200"></i>
              </div>
              <h4 class="text-[10px] font-black text-gray-700 uppercase h-8 line-clamp-2 px-1">{{ item.nama }}</h4>
              <p class="text-blue-600 font-black text-xs font-mono bg-blue-50 px-3 py-1 rounded-full inline-block mt-1">{{ formatNumber(item.harga) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 md:p-6 bg-white border-t shadow-[0_-4px_10px_rgba(0,0,0,0.05)] z-40 flex-shrink-0">
      <div class="max-w-7xl mx-auto flex flex-col md:flex-row justify-end items-center gap-6">
        
        <div class="flex flex-wrap gap-4 w-full md:w-auto">
          <div class="flex items-center gap-3 bg-gray-50 p-2 px-4 rounded-xl border flex-1">
            <span class="font-black text-gray-400 text-[10px] uppercase">Titipan (DP)</span>
            <input :value="formattedDp" @input="onInputMoney($event, 'dp')" type="text" class="w-28 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-xl font-black text-gray-700" />
          </div>
          <div v-if="form.tipe === 'JUAL'" class="flex items-center gap-3 bg-blue-50 p-2 px-4 rounded-xl border border-blue-200 flex-1">
            <span class="font-black text-blue-400 text-[10px] uppercase">Bayar (Cash)</span>
            <input :value="formattedBayar" @input="onInputMoney($event, 'bayar')" type="text" class="w-28 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-xl font-black text-blue-600" />
          </div>
        </div>

        <div class="text-right flex-shrink-0">
          <p class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1">Sisa Pembayaran</p>
          <p class="text-2xl font-black text-red-600 font-mono leading-none">Rp {{ formatNumber(sisaBayar) }}</p>
        </div>

        <button @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" class="w-full md:w-auto px-10 py-4 text-white rounded-2xl font-black shadow-lg flex items-center justify-center gap-3 transition transform active:scale-95 uppercase tracking-widest">
          <i class="bi bi-cloud-check-fill text-xl"></i> Simpan Transaksi
        </button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import api, { getImageUrl } from '../api';

const router = useRouter();
const masterItems = ref([]);
const search = ref("");
const barcodeField = ref(null);
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

const toast = reactive({ show: false, message: "" });
const triggerToast = (msg) => {
  toast.message = msg; toast.show = true;
  setTimeout(() => { toast.show = false; }, 3000);
};

// INITIAL STATE
const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data.map(i => ({ ...i, isShaking: false }));
    if(barcodeField.value) barcodeField.value.focus();
  } catch(e) { console.error("Gagal load item"); }
});

// --- FORMATTING LOGIC ---
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);
const formattedDp = computed(() => formatNumber(form.value.dp));
const formattedBayar = computed(() => formatNumber(form.value.bayar));

const onInputMoney = (event, field, opt = {}) => {
  const raw = event.target.value.replace(/\D/g, "");
  const num = raw === "" ? 0 : parseInt(raw);
  if(field === 'dp') form.value.dp = num;
  else if(field === 'bayar') form.value.bayar = num;
  else if(field === 'row_harga') rows.value[opt.index].harga = num;
};

// --- LOGIC CALCULATION ---
const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const sisaBayar = computed(() => Math.max(0, grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar))));
const kembalian = computed(() => Math.max(0, (Number(form.value.dp) + Number(form.value.bayar)) - grandTotal.value));

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  if(!s) return masterItems.value;
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || (i.kode && i.kode.toLowerCase().includes(s)));
});

// --- SCANNER / SEARCH BAR LOGIC ---
const handleBarcodeEnter = () => {
  if(!search.value) return;
  const input = search.value.toLowerCase().trim();
  const matched = masterItems.value.find(i => (i.kode && i.kode.toLowerCase() === input) || i.nama.toLowerCase() === input);
  if(matched) {
    onProductClick(matched);
    search.value = "";
  }
};

const onProductClick = (item) => {
  if(item.stok <= 0) return triggerToast("Maaf, stok habis!");
  const existing = rows.value.find(r => r.itemId === item.id);
  if(existing) {
    if(existing.qty + 1 > item.stok) return triggerToast("Stok tidak cukup!");
    existing.qty++;
  } else {
    if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
    rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
  }
};

const onSparepartSelect = (row) => {
  const item = masterItems.value.find(i => i.id === row.itemId);
  if(item) {
    row.namaBarang = item.nama;
    row.harga = item.harga;
  }
};

// --- MODE SERVICE HELPERS ---
const switchMode = (mode) => {
  if(grandTotal.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false });

const addSolution = (index) => {
  let lastIdx = index;
  for(let i = index+1; i < rows.value.length; i++) { if(rows.value[i].isChild) lastIdx = i; else break; }
  rows.value.splice(lastIdx + 1, 0, { unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: true });
};

const removeRow = (index) => {
  if(!rows.value[index].isChild) {
    let count = 1;
    for(let i = index+1; i < rows.value.length; i++) { if(rows.value[i].isChild) count++; else break; }
    rows.value.splice(index, count);
  } else { rows.value.splice(index, 1); }
  if(rows.value.length === 0) addNewUnit();
};

const getRelatedRows = (mainIdx) => {
  const related = [rows.value[mainIdx]];
  for(let i = mainIdx+1; i < rows.value.length; i++) { if(rows.value[i].isChild) related.push(rows.value[i]); else break; }
  return related;
};

const getRowNumber = (idx) => {
  let c = 0;
  for(let i=0; i<=idx; i++) { if(!rows.value[i].isChild) c++; }
  return c;
};

const calculateUnitTotal = (mainIdx) => getRelatedRows(mainIdx).reduce((s, r) => s + (r.qty * r.harga), 0);

// --- SAVE ACTION ---
const saveNota = async () => {
  if(!form.value.customerNama) return triggerToast("Nama Pelanggan wajib diisi!");
  const totalIn = Number(form.value.dp) + Number(form.value.bayar);
  const lunas = totalIn >= grandTotal.value;
  
  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : (lunas ? 'LUNAS' : 'PROSES'),
    barangCustomer: form.value.tipe === 'SERVICE' ? (rows.value[0].unitName || 'Unit') : (rows.value[0]?.namaBarang || 'Retail'),
    keluhan: form.value.tipe === 'SERVICE' ? (rows.value[0].kerusakan || '-') : '-',
    items: rows.value.filter(r => r.namaBarang || r.solusi).map(r => ({
      itemId: r.itemId,
      namaBarang: r.namaBarang || r.solusi || 'Jasa Service',
      hargaSatuan: r.harga,
      jumlah: r.qty,
      catatan: form.value.tipe === 'SERVICE' ? `Unit: ${r.unitName || '-'}` : 'Retail'
    }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    router.push('/nota/' + res.data.id);
  } catch(e) { triggerToast("Gagal simpan transaksi"); }
};
</script>

<style scoped>
/* Scrollbar */
::-webkit-scrollbar { width: 5px; height: 5px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }

/* Toast Animation */
.toast-enter-active, .toast-leave-active { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, -100px); }

/* Shake Animation */
.shake-anim { animation: shake 0.5s; border-color: #ef4444 !important; }
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
}

input::-webkit-outer-spin-button, input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
</style>