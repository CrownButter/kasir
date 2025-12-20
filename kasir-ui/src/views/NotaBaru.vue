<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black relative">
    
    <transition name="toast">
      <div v-if="toast.show" class="fixed top-20 left-1/2 -translate-x-1/2 z-[100] bg-red-600 text-white px-6 py-3 rounded-2xl shadow-2xl flex items-center gap-3 border-2 border-white/20 backdrop-blur-md">
        <i class="bi bi-exclamation-triangle-fill text-xl"></i>
        <span class="font-bold text-sm">{{ toast.message }}</span>
      </div>
    </transition>

    <div class="p-3 md:p-4 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-30 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Retail</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Service</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-8 bg-gray-50">
      <div class="max-w-5xl mx-auto space-y-8">
          <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
              <h2 class="font-bold text-gray-800 mb-4">Informasi Pelanggan</h2>
              <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <input v-model="form.customerNama" placeholder="Nama Lengkap *" class="p-3 bg-gray-50 border rounded-xl font-bold outline-none" />
                <input v-model="form.customerTelp" placeholder="Nomor WhatsApp" class="p-3 bg-gray-50 border rounded-xl outline-none" />
                <input v-model="form.customerAlamat" placeholder="Alamat" class="p-3 bg-gray-50 border rounded-xl outline-none" />
              </div>
          </div>
          <div v-for="(row, index) in rows" :key="index">
              <div v-if="!row.isChild" class="bg-white rounded-2xl border p-4 mb-4">
                  <input v-model="row.unitName" placeholder="Nama Unit" class="w-full p-2 border rounded mb-2" />
                  <button @click="addSolution(index)" class="text-xs text-orange-600 font-bold">+ Detail Biaya</button>
              </div>
          </div>
          <button @click="addNewUnit" class="w-full p-4 border-2 border-dashed rounded-xl text-gray-400 font-bold">TAMBAH UNIT</button>
      </div>
    </div>

    <div v-else class="flex-1 flex flex-col md:flex-row overflow-hidden relative">
      <div class="w-full md:w-1/3 flex flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        <div class="bg-black p-4 text-center">
          <p class="text-green-500 text-[10px] font-mono">Total Tagihan</p>
          <h1 class="text-3xl font-mono text-green-400 font-bold">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4">
          <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full border-2 p-3 rounded-xl text-sm font-bold mb-4 outline-none" />
          <div v-for="(row, index) in rows" :key="index" v-show="row.namaBarang" class="flex justify-between items-center p-3 border rounded-xl mb-2">
            <div class="flex-1">
              <p class="font-bold text-sm">{{ row.namaBarang }}</p>
              <span class="text-xs text-blue-600">{{ row.qty }} x {{ formatNumber(row.harga) }}</span>
            </div>
            <button @click="removeRow(index)" class="text-red-300"><i class="bi bi-trash"></i></button>
          </div>
        </div>

        <div class="p-4 bg-gray-50 border-t space-y-3">
          <div class="grid grid-cols-2 gap-2">
            <input v-model="form.dp" type="number" placeholder="DP" class="p-2 border rounded-lg text-sm" />
            <input v-model="form.bayar" type="number" placeholder="Bayar" class="p-2 border-2 border-blue-400 rounded-lg text-sm bg-blue-50" />
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-200 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm">
          <input v-model="search" placeholder="Cari barang..." class="w-full p-3 border rounded-full bg-gray-50 outline-none" />
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            <div v-for="item in filteredItems" :key="item.id" 
                 @click="onProductClick(item)" 
                 :class="{'shake-anim': item.isShaking}"
                 class="bg-white p-3 rounded-2xl border-2 border-transparent hover:border-blue-500 cursor-pointer transition shadow-sm relative overflow-hidden active:scale-95 group">
              
              <span :class="[
                  'absolute top-2 right-2 px-2 py-0.5 rounded-full font-bold text-[9px] z-10',
                  item.stok <= 0 ? 'bg-red-600 text-white' : 
                  item.stok < 5 ? 'bg-orange-500 text-white animate-pulse' : 'bg-blue-100 text-blue-700'
              ]">
                {{ item.stok }}
              </span>

              <div class="h-24 bg-gray-50 rounded-xl mb-2 flex items-center justify-center overflow-hidden">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="h-full object-cover group-hover:scale-110 transition" />
                <i v-else class="bi bi-box text-2xl text-gray-200"></i>
              </div>
              <h4 class="text-[10px] font-bold text-gray-700 uppercase line-clamp-2 h-8">{{ item.nama }}</h4>
              <p class="text-blue-700 font-black mt-1 text-sm">{{ formatNumber(item.harga) }}</p>

              <div v-if="item.stok <= 0" class="absolute inset-0 bg-white/60 flex items-center justify-center backdrop-blur-[1px]">
                 <span class="bg-red-600 text-white text-[10px] px-3 py-1 rounded-full font-black uppercase rotate-12 shadow-lg">Habis</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 bg-white border-t flex justify-center items-center z-30">
      <button @click="saveNota" 
              :class="form.tipe === 'SERVICE' ? 'bg-orange-600' : 'bg-blue-600'" 
              class="w-full md:w-1/2 py-4 text-white rounded-2xl font-black shadow-lg transition active:scale-95 uppercase tracking-widest text-sm">
        Simpan Transaksi
      </button>
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
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

// State untuk Toast Notification
const toast = reactive({ show: false, message: "" });
const triggerToast = (msg) => {
  toast.message = msg;
  toast.show = true;
  setTimeout(() => { toast.show = false; }, 3000);
};

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    // Tambahkan properti isShaking untuk animasi per item
    masterItems.value = res.data.map(item => ({ ...item, isShaking: false }));
  } catch(e) { console.error("Gagal load item"); }
});

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const cartCount = computed(() => rows.value.filter(r => r.itemId || r.namaBarang).length);
const sisaBayar = computed(() => Math.max(0, grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar))));
const kembalian = computed(() => Math.max(0, (Number(form.value.dp) + Number(form.value.bayar)) - grandTotal.value));

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || i.kode.toLowerCase().includes(s));
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

// --- LOGIKA KLIK PRODUK DENGAN ANIMASI (TANPA ALERT) ---
const onProductClick = (item) => {
  // 1. Jika Stok Habis (0)
  if(item.stok <= 0) {
    item.isShaking = true;
    triggerToast(`Maaf, stok ${item.nama} sudah habis!`);
    setTimeout(() => { item.isShaking = false; }, 500);
    return;
  }

  const existing = rows.value.find(r => r.itemId === item.id);

  if(existing) {
    // 2. Jika Melebihi Stok Tersedia
    if(existing.qty + 1 > item.stok) {
      item.isShaking = true;
      triggerToast(`Stok terbatas! Hanya tersedia ${item.stok} unit.`);
      setTimeout(() => { item.isShaking = false; }, 500);
      return;
    }
    existing.qty++;
  } else {
    if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
    rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
  }
};

const switchMode = (mode) => {
  if(cartCount.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });
const addSolution = (index) => rows.value.splice(index + 1, 0, { unitName: "", kerusakan: "", solusi: "Biaya Baru", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: true });
const removeRow = (index) => { rows.value.splice(index, 1); if(rows.value.length === 0) addNewUnit(); };
const getRelatedRows = (index) => rows.value.filter((r, i) => i === index || (i > index && r.isChild && !rows.value.slice(index + 1, i).some(x => !x.isChild)));
const getRowNumber = (index) => rows.value.slice(0, index + 1).filter(r => !r.isChild).length;

const saveNota = async () => {
  if (!form.value.customerNama) { triggerToast("Nama Pelanggan wajib diisi!"); return; }
  if (cartCount.value === 0) { triggerToast("Pilih minimal satu barang!"); return; }
  
  const totalInput = Number(form.value.dp) + Number(form.value.bayar);
  const isLunas = totalInput >= grandTotal.value;
  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : (isLunas ? 'LUNAS' : 'PROSES'),
    barangCustomer: form.value.tipe === 'SERVICE' ? (rows.value[0].unitName || 'Service') : (rows.value[0].namaBarang || 'Retail'),
    items: rows.value.map(r => ({ itemId: r.itemId, namaBarang: r.namaBarang || r.solusi, hargaSatuan: r.harga, jumlah: r.qty, catatan: form.value.tipe === 'SERVICE' ? `Unit: ${r.unitName}` : 'Retail' }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    router.push('/nota/' + res.data.id);
  } catch (e) { triggerToast("Gagal menyimpan transaksi!"); }
};
</script>

<style scoped>
/* Scrollbar */
::-webkit-scrollbar { width: 4px; height: 4px; }
::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

/* ANIMASI TOAST (FADE & SLIDE) */
.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from { opacity: 0; transform: translate(-50%, -20px); }
.toast-leave-to { opacity: 0; transform: translate(-50%, -20px); }

/* ANIMASI PULSE (STOK RENDAH) */
.animate-pulse { animation: pulse 1.5s infinite; }
@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

/* ANIMASI GETAR (SHAKE) SAAT STOK MELEBIHI BATAS */
.shake-anim { animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both; border-color: #ef4444 !important; }
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}

/* Hide Spinner Number */
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
</style>