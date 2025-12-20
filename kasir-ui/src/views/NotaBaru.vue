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

        <div class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50/50">
          
          <div class="space-y-1">
             <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Nama Pelanggan</label>
             <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-blue-500 focus:bg-white transition bg-white shadow-sm" />
          </div>

          <div v-if="form.tipe === 'SERVICE'" class="space-y-4 animate-in fade-in slide-in-from-top-2 duration-300">
             <div class="grid grid-cols-2 gap-3">
                <div class="space-y-1">
                   <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">No. Telp</label>
                   <input v-model="form.customerTelp" placeholder="08..." class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-orange-500 bg-white shadow-sm" />
                </div>
                <div class="space-y-1">
                   <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Nama Unit/Barang</label>
                   <input v-model="rows[0].unitName" placeholder="Contoh: Laptop Asus" class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-orange-500 bg-white shadow-sm" />
                </div>
             </div>
             <div class="space-y-1">
                <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Kerusakan / Keluhan</label>
                <textarea v-model="rows[0].kerusakan" placeholder="Jelaskan kerusakan..." class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-orange-500 bg-white shadow-sm h-20"></textarea>
             </div>
             <div class="space-y-1">
                <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Solusi / Tindakan</label>
                <input v-model="rows[0].solusi" placeholder="Tindakan yang diambil..." class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-orange-500 bg-white shadow-sm" />
             </div>
             <div class="space-y-1">
                <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1">Biaya Service (Jasa)</label>
                <input :value="formatNumber(rows[0].harga)" @input="onInputMoney($event, 'hargaService')" type="text" placeholder="Harga Jasa..." class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-orange-500 bg-orange-50 shadow-sm" />
             </div>
          </div>

          <div class="pt-4 border-t border-gray-200">
            <label class="text-[9px] font-black text-gray-400 uppercase tracking-widest ml-1 mb-2 block">
              {{ form.tipe === 'SERVICE' ? 'Sparepart / Material Tambahan' : 'Daftar Barang Belanjaan' }}
            </label>
            
            <div v-for="(row, index) in filteredRows" :key="index" class="flex justify-between items-center p-3 mb-2 border-2 border-transparent bg-white rounded-2xl shadow-sm hover:border-blue-200 transition">
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

            <div v-if="cartCount === 0 && form.tipe === 'JUAL'" class="flex flex-col items-center justify-center h-32 text-slate-300">
              <i class="bi bi-cart-x text-4xl mb-2 opacity-20"></i>
              <p class="text-[10px] font-black uppercase tracking-widest">Keranjang Kosong</p>
            </div>
          </div>
        </div>

        <div class="p-5 bg-white border-t-4 border-gray-100 space-y-4 shadow-[0_-10px_20px_rgba(0,0,0,0.05)]">
          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-[10px] font-black text-slate-400 uppercase tracking-widest flex items-center gap-1">
                <i class="bi bi-wallet2 text-blue-500"></i> DP / Uang Muka
              </label>
              <input :value="formattedDp" @input="onInputMoney($event, 'dp')" type="text" class="w-full p-3 border-2 rounded-xl font-mono font-bold text-lg focus:border-blue-400 outline-none transition bg-slate-50" placeholder="0" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-blue-600 uppercase tracking-widest flex items-center gap-1">
                <i class="bi bi-cash-coin"></i> Bayar (Cash)
              </label>
              <input :value="formattedBayar" @input="onInputMoney($event, 'bayar')" type="text" class="w-full p-3 border-2 border-blue-400 rounded-xl font-mono font-bold bg-blue-50 text-lg focus:ring-4 focus:ring-blue-100 outline-none transition text-blue-700" placeholder="0" />
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
              placeholder="Scan Barcode atau ketik nama barang disini..." 
              class="w-full pl-12 p-4 border-none rounded-2xl bg-gray-100 outline-none focus:bg-white focus:ring-4 focus:ring-blue-100 transition text-sm font-bold shadow-inner" 
            />
            <div class="absolute right-5 top-4">
               <span class="text-[9px] bg-blue-600 text-white px-2 py-1 rounded font-black uppercase tracking-tighter animate-pulse">Barcode Scanner Ready</span>
            </div>
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-5">
            <div v-for="item in filteredItems" :key="item.id" 
                 @click="onProductClick(item)" 
                 :class="{'shake-anim': item.isShaking}"
                 class="bg-white p-3 rounded-[2.5rem] border-4 border-transparent hover:border-blue-500 cursor-pointer transition-all shadow-md relative group overflow-hidden active:scale-95 flex flex-col items-center text-center">
              <span :class="['absolute top-4 right-4 px-2.5 py-1 rounded-full font-black text-[9px] z-10 shadow-sm border', item.stok <= 0 ? 'bg-red-600 text-white border-red-700' : 'bg-blue-100 text-blue-700 border-blue-200']">
                STK: {{ item.stok }}
              </span>
              <div class="w-full aspect-square bg-slate-50 rounded-[2rem] mb-3 flex items-center justify-center overflow-hidden border">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover group-hover:scale-110 transition duration-500" />
                <i v-else class="bi bi-box-seam text-4xl text-slate-200"></i>
              </div>
              <h4 class="text-[11px] font-black text-slate-700 uppercase line-clamp-2 h-8 px-1 mb-2">{{ item.nama }}</h4>
              <p class="text-blue-600 font-black text-sm font-mono bg-blue-50 px-3 py-1 rounded-full">{{ formatNumber(item.harga) }}</p>
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

const toast = reactive({ show: false, message: "" });
const triggerToast = (msg) => {
  toast.message = msg;
  toast.show = true;
  setTimeout(() => { toast.show = false; }, 3000);
};

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
// Service Row tetap ada di index 0
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data.map(item => ({ ...item, isShaking: false }));
    if(barcodeField.value) barcodeField.value.focus();
  } catch(e) { console.error("Gagal load item"); }
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);
const formattedDp = computed(() => formatNumber(form.value.dp));
const formattedBayar = computed(() => formatNumber(form.value.bayar));

const onInputMoney = (event, field) => {
  const rawValue = event.target.value.replace(/\D/g, "");
  const numValue = rawValue === "" ? 0 : parseInt(rawValue);
  
  if(field === 'hargaService') {
    rows.value[0].harga = numValue;
  } else {
    form.value[field] = numValue;
  }
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const cartCount = computed(() => rows.value.filter(r => r.itemId).length);
const sisaBayar = computed(() => Math.max(0, grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar))));
const kembalian = computed(() => Math.max(0, (Number(form.value.dp) + Number(form.value.bayar)) - grandTotal.value));

// Filtered rows untuk menampilkan cart barang (di luar baris service)
const filteredRows = computed(() => rows.value.filter(r => r.namaBarang && r.itemId));

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  if(!s) return masterItems.value;
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || (i.kode && i.kode.toLowerCase().includes(s)));
});

const handleBarcodeEnter = () => {
  if(!search.value) return;
  const inputVal = search.value.toLowerCase().trim();
  const matchedItem = masterItems.value.find(i => (i.kode && i.kode.toLowerCase() === inputVal) || (i.nama.toLowerCase() === inputVal));
  if(matchedItem) {
    onProductClick(matchedItem);
    search.value = "";
  }
};

const onProductClick = (item) => {
  if(item.stok <= 0) {
    item.isShaking = true;
    triggerToast(`Stok ${item.nama} habis!`);
    setTimeout(() => { item.isShaking = false; }, 500);
    return;
  }

  const existing = rows.value.find(r => r.itemId === item.id);
  if(existing) {
    if(existing.qty + 1 > item.stok) return triggerToast("Stok tidak mencukupi!");
    existing.qty++;
  } else {
    rows.value.push({ namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id });
  }
};

const switchMode = (mode) => {
  if(cartCount.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", qty: 1, harga: 0, itemId: null }];
};

const removeRow = (index) => {
  // Jika baris ke-0 di mode service, jangan hapus, hanya reset
  if(index === 0 && form.value.tipe === 'SERVICE') {
    rows.value[0].namaBarang = "";
    rows.value[0].itemId = null;
    rows.value[0].harga = 0;
  } else {
    rows.value.splice(index, 1);
  }
};

const saveNota = async () => {
  if (!form.value.customerNama) return triggerToast("Nama Pelanggan wajib diisi!");
  
  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : 'LUNAS',
    barangCustomer: form.value.tipe === 'SERVICE' ? rows.value[0].unitName : rows.value[1]?.namaBarang || 'Retail',
    keluhan: form.value.tipe === 'SERVICE' ? rows.value[0].kerusakan : '-',
    items: rows.value.filter(r => (r.itemId || r.harga > 0)).map(r => ({ 
        itemId: r.itemId, 
        namaBarang: r.itemId ? r.namaBarang : 'Jasa Service', 
        hargaSatuan: r.harga, 
        jumlah: r.qty, 
        catatan: r.itemId ? 'Retail/Sparepart' : r.kerusakan 
    }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    router.push('/nota/' + res.data.id);
  } catch (e) { triggerToast("Gagal menyimpan transaksi!"); }
};
</script>

<style scoped>
.toast-enter-active, .toast-leave-active { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, -100px); }
.shake-anim { animation: shake 0.5s; border-color: #ef4444 !important; }
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}
</style>