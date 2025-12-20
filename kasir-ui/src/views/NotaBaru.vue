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
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Retail / Jual</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Service / Perbaikan</button>
      </div>
    </div>

    <div class="flex-1 flex flex-col md:flex-row overflow-hidden relative">
      
      <div class="w-full md:w-1/2 flex flex-col bg-white border-r shadow-2xl z-20 overflow-hidden">
        
        <div class="bg-slate-900 p-5 text-center flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase tracking-[0.2em] mb-1">Total Tagihan</p>
          <h1 class="text-4xl font-mono text-green-400 font-black tracking-tighter">
            {{ formatNumber(grandTotal) }}
          </h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50/50">
          <div class="space-y-1 mb-4">
             <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Data Pelanggan</label>
             <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-blue-500 focus:bg-white transition bg-white shadow-sm" />
          </div>

          <div v-for="(row, index) in rows" :key="index" v-show="row.namaBarang" class="flex justify-between items-center p-3 border-2 border-transparent bg-white rounded-2xl shadow-sm hover:border-blue-200 transition">
            <div class="flex-1">
              <p class="font-bold text-sm text-slate-800 line-clamp-1 uppercase">{{ row.namaBarang }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="bg-blue-100 text-blue-700 text-[10px] px-2 py-0.5 rounded font-black">{{ row.qty }}x</span>
                <span class="text-[10px] text-slate-400 font-mono">@ {{ formatNumber(row.harga) }}</span>
              </div>
            </div>
            <div class="text-right flex flex-col items-end gap-1">
               <p class="font-black text-sm text-slate-900 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
               <button @click="removeRow(index)" class="text-red-300 hover:text-red-500 transition-colors p-1"><i class="bi bi-trash3-fill"></i></button>
            </div>
          </div>

          <div v-if="cartCount === 0" class="flex flex-col items-center justify-center h-48 text-slate-300">
            <i class="bi bi-cart-x text-5xl mb-3 opacity-20"></i>
            <p class="text-xs font-black uppercase tracking-widest">Keranjang Kosong</p>
          </div>
        </div>

        <div class="p-5 bg-white border-t-4 border-gray-100 space-y-4 shadow-[0_-10px_20px_rgba(0,0,0,0.05)]">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-1">
                <i class="bi bi-wallet2 text-blue-500"></i> Uang Muka (DP)
              </label>
              <input 
                :value="formattedDp" 
                @input="onInputMoney($event, 'dp')"
                type="text" 
                class="w-full p-3 border-2 rounded-xl font-mono font-bold text-lg focus:border-blue-400 outline-none transition bg-slate-50" 
                placeholder="0" 
              />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-blue-600 uppercase tracking-widest flex items-center gap-1">
                <i class="bi bi-cash-coin"></i> Bayar (Cash)
              </label>
              <input 
                :value="formattedBayar" 
                @input="onInputMoney($event, 'bayar')"
                type="text" 
                class="w-full p-3 border-2 border-blue-400 rounded-xl font-mono font-bold bg-blue-50 text-lg focus:ring-4 focus:ring-blue-100 outline-none transition text-blue-700" 
                placeholder="0" 
              />
            </div>
          </div>

          <div class="flex justify-between items-center p-4 bg-slate-900 rounded-2xl">
            <div class="border-r border-slate-700 pr-4">
              <p class="text-[9px] font-black text-slate-500 uppercase mb-1">Sisa Tagihan</p>
              <p class="text-xl font-black text-red-500 font-mono leading-none">{{ formatNumber(sisaBayar) }}</p>
            </div>
            <div class="text-right pl-4">
              <p class="text-[9px] font-black text-slate-500 uppercase mb-1">Kembalian</p>
              <p class="text-xl font-black text-green-400 font-mono leading-none">{{ formatNumber(kembalian) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-100 flex flex-col overflow-hidden">
        
        <div class="p-4 bg-white shadow-sm flex items-center gap-4 border-b">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-5 top-4 text-slate-400"></i>
            <input 
              v-model="search" 
              @keyup.enter="handleBarcodeEnter"
              ref="barcodeField"
              placeholder="Cari nama barang atau Scan Barcode disini..." 
              class="w-full pl-12 p-4 border-none rounded-2xl bg-gray-100 outline-none focus:bg-white focus:ring-4 focus:ring-blue-100 transition text-sm font-bold shadow-inner" 
            />
            <div class="absolute right-5 top-4">
               <span class="text-[9px] bg-blue-600 text-white px-2 py-1 rounded font-black uppercase tracking-tighter animate-pulse">Mode Scan Aktif</span>
            </div>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-5">
            <div v-for="item in filteredItems" :key="item.id" 
                 @click="onProductClick(item)" 
                 :class="{'shake-anim': item.isShaking}"
                 class="bg-white p-3 rounded-[2.5rem] border-4 border-transparent hover:border-blue-500 cursor-pointer transition-all shadow-md relative group overflow-hidden active:scale-95 flex flex-col items-center text-center">
              
              <span :class="[
                  'absolute top-4 right-4 px-2.5 py-1 rounded-full font-black text-[9px] z-10 shadow-sm border',
                  item.stok <= 0 ? 'bg-red-600 text-white border-red-700' : 
                  item.stok < 5 ? 'bg-orange-500 text-white animate-pulse border-orange-600' : 'bg-blue-100 text-blue-700 border-blue-200'
              ]">
                STK: {{ item.stok }}
              </span>

              <div class="w-full aspect-square bg-slate-50 rounded-[2rem] mb-3 flex items-center justify-center overflow-hidden border">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover group-hover:scale-110 transition duration-500" />
                <i v-else class="bi bi-box-seam text-4xl text-slate-200"></i>
              </div>
              
              <h4 class="text-[11px] font-black text-slate-700 uppercase line-clamp-2 h-8 leading-tight px-1 mb-2">{{ item.nama }}</h4>
              <p class="text-blue-600 font-black text-sm font-mono bg-blue-50 px-3 py-1 rounded-full">{{ formatNumber(item.harga) }}</p>

              <div v-if="item.stok <= 0" class="absolute inset-0 bg-white/70 flex items-center justify-center backdrop-blur-[2px] z-20">
                 <span class="bg-red-600 text-white text-[10px] px-4 py-1.5 rounded-full font-black uppercase rotate-[-10deg] shadow-xl">Stok Habis</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-5 md:p-8 bg-white border-t flex justify-center items-center flex-shrink-0 z-30">
      <button @click="saveNota" 
              :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700 shadow-orange-200' : 'bg-blue-600 hover:bg-blue-700 shadow-blue-200'" 
              class="w-full md:w-2/3 lg:w-1/2 py-5 text-white rounded-[2rem] font-black shadow-2xl transition-all transform active:scale-95 uppercase tracking-[0.2em] text-base flex items-center justify-center gap-4">
        <i class="bi bi-cloud-check-fill text-2xl"></i>
        Proses & Simpan Transaksi
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
const barcodeField = ref(null);
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

// TOAST NOTIFIKASI
const toast = reactive({ show: false, message: "" });
const triggerToast = (msg) => {
  toast.message = msg;
  toast.show = true;
  setTimeout(() => { toast.show = false; }, 3000);
};

// FORM DATA
const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data.map(item => ({ ...item, isShaking: false }));
    // Fokus otomatis ke pencarian saat halaman dibuka
    if(barcodeField.value) barcodeField.value.focus();
  } catch(e) { console.error("Gagal load item"); }
});

// FORMAT ANGKA KE RUPIAH (TITIK)
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

// FORMAT TAMPILAN INPUT DP & BAYAR
const formattedDp = computed(() => formatNumber(form.value.dp));
const formattedBayar = computed(() => formatNumber(form.value.bayar));

// LOGIKA KETIK ANGKA OTOMATIS BERSIHKAN DARI HURUF
const onInputMoney = (event, field) => {
  const rawValue = event.target.value.replace(/\D/g, "");
  form.value[field] = rawValue === "" ? 0 : parseInt(rawValue);
};

// PERHITUNGAN TRANSAKSI
const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const cartCount = computed(() => rows.value.filter(r => r.itemId || r.namaBarang).length);
const sisaBayar = computed(() => Math.max(0, grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar))));
const kembalian = computed(() => Math.max(0, (Number(form.value.dp) + Number(form.value.bayar)) - grandTotal.value));

// FILTER KATALOG PRODUK
const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  if(!s) return masterItems.value;
  return masterItems.value.filter(i => 
    i.nama.toLowerCase().includes(s) || (i.kode && i.kode.toLowerCase().includes(s))
  );
});

// LOGIKA BARCODE SCANNER (TEKAN ENTER)
const handleBarcodeEnter = () => {
  if(!search.value) return;
  
  const inputVal = search.value.toLowerCase().trim();
  // Cari barang yang kodenya atau namanya persis sama dengan input
  const matchedItem = masterItems.value.find(i => 
    (i.kode && i.kode.toLowerCase() === inputVal) || 
    (i.nama.toLowerCase() === inputVal)
  );

  if(matchedItem) {
    onProductClick(matchedItem);
    search.value = ""; // Bersihkan kolom pencarian agar bisa scan lagi
  }
};

// TAMBAH BARANG KE KERANJANG
const onProductClick = (item) => {
  if(item.stok <= 0) {
    item.isShaking = true;
    triggerToast(`Maaf, stok ${item.nama} sudah habis!`);
    setTimeout(() => { item.isShaking = false; }, 500);
    return;
  }

  const existing = rows.value.find(r => r.itemId === item.id);
  if(existing) {
    if(existing.qty + 1 > item.stok) {
      item.isShaking = true;
      triggerToast(`Stok terbatas! Tersedia ${item.stok} unit.`);
      setTimeout(() => { item.isShaking = false; }, 500);
      return;
    }
    existing.qty++;
  } else {
    // Jika baris pertama kosong, timpa baris pertama
    if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) {
      rows.value = [];
    }
    rows.value.push({ 
        namaBarang: item.nama, 
        qty: 1, 
        harga: item.harga, 
        itemId: item.id 
    });
  }
};

const switchMode = (mode) => {
  if(cartCount.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null }];
};

const removeRow = (index) => {
  rows.value.splice(index, 1);
  if(rows.value.length === 0) {
    rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null }];
  }
};

const saveNota = async () => {
  if (!form.value.customerNama) return triggerToast("Nama Pelanggan wajib diisi!");
  if (cartCount.value === 0) return triggerToast("Pilih minimal satu barang!");
  
  const totalInput = Number(form.value.dp) + Number(form.value.bayar);
  const isLunas = totalInput >= grandTotal.value;
  
  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : (isLunas ? 'LUNAS' : 'PROSES'),
    barangCustomer: form.value.tipe === 'SERVICE' ? 'Unit Service' : (rows.value[0].namaBarang || 'Retail'),
    items: rows.value.filter(r => r.namaBarang).map(r => ({ 
        itemId: r.itemId, 
        namaBarang: r.namaBarang, 
        hargaSatuan: r.harga, 
        jumlah: r.qty, 
        catatan: 'Retail' 
    }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    router.push('/nota/' + res.data.id);
  } catch (e) { triggerToast("Gagal menyimpan transaksi!"); }
};
</script>

<style scoped>
::-webkit-scrollbar { width: 5px; height: 5px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }

.toast-enter-active, .toast-leave-active { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.toast-enter-from { opacity: 0; transform: translate(-50%, -100px); }
.toast-leave-to { opacity: 0; transform: translate(-50%, -100px); }

.shake-anim { animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both; border-color: #ef4444 !important; }
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}

input::-webkit-outer-spin-button, input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
</style>