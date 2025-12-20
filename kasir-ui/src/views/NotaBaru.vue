<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black">
    <div class="p-3 md:p-4 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-30 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Retail</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-6 md:px-8 py-2 rounded-full text-[10px] md:text-xs font-bold transition-all uppercase tracking-wider">Service</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-8 bg-gray-50">
      <div class="max-w-5xl mx-auto space-y-8">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center gap-3 mb-6 border-b pb-4">
            <div class="w-10 h-10 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center"><i class="bi bi-person-badge-fill text-xl"></i></div>
            <div>
              <h2 class="font-bold text-gray-800">Informasi Pelanggan</h2>
              <p class="text-[10px] text-gray-400 uppercase tracking-widest font-bold">Service Mode</p>
            </div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <input v-model="form.customerNama" placeholder="Nama Lengkap *" class="p-3 bg-gray-50 border rounded-xl font-bold outline-none focus:ring-2 focus:ring-orange-200" />
            <input v-model="form.customerTelp" placeholder="Nomor WhatsApp" class="p-3 bg-gray-50 border rounded-xl outline-none" />
            <input v-model="form.customerAlamat" placeholder="Alamat" class="p-3 bg-gray-50 border rounded-xl outline-none" />
          </div>
        </div>

        <div v-for="(row, index) in rows" :key="index">
          <div v-if="!row.isChild" class="bg-white rounded-2xl shadow-md border border-gray-200 overflow-hidden mb-6">
            <div class="bg-orange-600 p-4 px-6 flex justify-between items-center text-white">
              <span class="font-bold text-sm uppercase">Unit #{{ getRowNumber(index) }}</span>
              <button @click="removeRow(index)" class="hover:bg-red-500 p-1 rounded"><i class="bi bi-trash"></i></button>
            </div>
            <div class="p-6 space-y-4">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <input v-model="row.unitName" placeholder="Nama Unit (Contoh: Laptop Asus)" class="w-full p-3 bg-gray-50 rounded-xl border font-bold" />
                <input v-model="row.kerusakan" placeholder="Keluhan / Kerusakan" class="w-full p-3 bg-gray-50 rounded-xl border" />
              </div>
              <div v-for="(subRow, subIndex) in getRelatedRows(index)" :key="subIndex" class="flex gap-2 items-center bg-gray-50 p-2 rounded-lg">
                 <input v-model="subRow.solusi" placeholder="Tindakan/Sparepart" class="flex-1 p-2 border rounded" />
                 <input v-model="subRow.harga" type="number" placeholder="Biaya" class="w-32 p-2 border rounded text-right font-mono" />
                 <button v-if="subRow.isChild" @click="removeRow(index + subIndex)" class="text-red-500"><i class="bi bi-x-circle"></i></button>
              </div>
              <button @click="addSolution(index)" class="text-xs font-bold text-orange-600 hover:underline">+ Tambah Detail Biaya</button>
            </div>
          </div>
        </div>
        <button @click="addNewUnit" class="w-full p-4 border-2 border-dashed rounded-2xl text-gray-400 font-bold hover:bg-orange-50 transition-all">+ TAMBAH UNIT BARU</button>
      </div>
    </div>

    <div v-else class="flex-1 flex flex-col md:flex-row overflow-hidden relative">
      <div class="w-full md:w-1/3 flex flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        <div class="bg-black p-4 text-center flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase mb-1">Total Tagihan</p>
          <h1 class="text-3xl font-mono text-green-400 font-bold tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-3">
          <input v-model="form.customerNama" placeholder="Nama Customer *" class="w-full border-2 p-3 rounded-xl text-sm font-bold mb-4 focus:border-blue-400 outline-none" />
          
          <div v-for="(row, index) in rows" :key="index" v-show="row.namaBarang" class="flex justify-between items-center p-3 border rounded-xl bg-white shadow-sm group">
            <div class="flex-1">
              <p class="font-bold text-sm text-gray-800">{{ row.namaBarang }}</p>
              <div class="flex items-center gap-2 mt-1">
                <span class="bg-blue-100 text-blue-700 text-[10px] px-2 py-0.5 rounded font-bold">{{ row.qty }}x</span>
                <span class="text-[10px] text-gray-400 font-mono">@ {{ formatNumber(row.harga) }}</span>
              </div>
            </div>
            <div class="flex flex-col items-end gap-2">
              <p class="font-bold text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
              <button @click="removeRow(index)" class="text-red-300 hover:text-red-500 transition"><i class="bi bi-trash-fill"></i></button>
            </div>
          </div>

          <div v-if="cartCount === 0" class="flex flex-col items-center justify-center h-40 text-gray-300">
            <i class="bi bi-cart-x text-4xl mb-2"></i>
            <p class="text-xs font-bold uppercase">Keranjang Kosong</p>
          </div>
        </div>

        <div class="p-4 bg-gray-50 border-t space-y-3 shadow-inner">
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="text-[9px] font-black text-gray-500 uppercase">DP / Panjar</label>
              <input v-model="form.dp" type="number" class="w-full p-2 border rounded-lg font-mono font-bold text-sm" />
            </div>
            <div>
              <label class="text-[9px] font-black text-blue-600 uppercase">Bayar (Cash)</label>
              <input v-model="form.bayar" type="number" class="w-full p-2 border-2 border-blue-400 rounded-lg font-mono font-bold bg-blue-50 text-sm" />
            </div>
          </div>
          <div class="flex justify-between items-center p-3 bg-white rounded-xl border-2 border-dashed">
            <div>
              <p class="text-[9px] font-bold text-gray-400 uppercase">Sisa Tagihan</p>
              <p class="text-xl font-black text-red-600 font-mono">{{ formatNumber(sisaBayar) }}</p>
            </div>
            <div class="text-right">
              <p class="text-[9px] font-bold text-gray-400 uppercase">Kembalian</p>
              <p class="text-xl font-black text-green-600 font-mono">{{ formatNumber(kembalian) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-gray-200 flex flex-col overflow-hidden">
        <div class="p-4 bg-white shadow-sm flex items-center gap-3">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-4 top-3 text-gray-400"></i>
            <input v-model="search" placeholder="Cari nama barang atau kode..." class="w-full pl-11 p-3 border rounded-full bg-gray-50 outline-none focus:bg-white focus:ring-2 focus:ring-blue-100 transition" />
          </div>
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" 
                 class="bg-white p-3 rounded-2xl border-2 border-transparent hover:border-blue-500 cursor-pointer transition shadow-sm relative group overflow-hidden active:scale-95">
              
              <span :class="[
                  'absolute top-2 right-2 px-2 py-0.5 rounded-full font-bold text-[9px] z-10 shadow-sm',
                  item.stok <= 0 ? 'bg-red-600 text-white' : 
                  item.stok < 5 ? 'bg-orange-500 text-white animate-pulse' : 
                  'bg-blue-100 text-blue-700'
              ]">
                STOK: {{ item.stok }}
              </span>

              <div class="h-28 bg-gray-50 rounded-xl mb-3 flex items-center justify-center overflow-hidden">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="h-full w-full object-cover group-hover:scale-110 transition duration-300" />
                <i v-else class="bi bi-box text-3xl text-gray-200"></i>
              </div>
              <h4 class="text-[11px] font-bold text-gray-700 uppercase leading-tight h-8 line-clamp-2">{{ item.nama }}</h4>
              <div class="flex justify-between items-end mt-2">
                <p class="text-blue-700 font-black text-sm font-mono">{{ formatNumber(item.harga) }}</p>
                <i class="bi bi-plus-circle-fill text-blue-600 opacity-0 group-hover:opacity-100 transition"></i>
              </div>

              <div v-if="item.stok <= 0" class="absolute inset-0 bg-white/60 flex items-center justify-center backdrop-blur-[1px] z-20">
                 <span class="bg-red-600 text-white text-[10px] px-3 py-1 rounded-full font-black uppercase rotate-12 shadow-lg">Habis</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 md:p-6 bg-white border-t flex justify-center items-center flex-shrink-0 z-30">
      <button @click="saveNota" 
              :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700 shadow-orange-200' : 'bg-blue-600 hover:bg-blue-700 shadow-blue-200'" 
              class="w-full md:w-1/2 py-4 text-white rounded-2xl font-black shadow-lg flex items-center justify-center gap-3 transition transform active:scale-95 uppercase tracking-widest text-sm">
        <i class="bi bi-cloud-check-fill text-xl"></i> Simpan Transaksi & Lihat Detail
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api, { getImageUrl } from '../api';

const router = useRouter();
const masterItems = ref([]);
const search = ref("");
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
});

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const cartCount = computed(() => rows.value.filter(r => r.itemId || r.namaBarang).length);

const sisaBayar = computed(() => {
  const sisa = grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar));
  return sisa > 0 ? sisa : 0;
});

const kembalian = computed(() => {
  const totalMasuk = Number(form.value.dp) + Number(form.value.bayar);
  return totalMasuk > grandTotal.value ? totalMasuk - grandTotal.value : 0;
});

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || i.kode.toLowerCase().includes(s));
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

// --- PERBAIKAN: VALIDASI STOK SAAT KLIK PRODUK ---
const onProductClick = (item) => {
  if(item.stok <= 0) return alert("Maaf, stok barang ini sudah habis!");

  // Cari apakah item sudah ada di keranjang
  const existing = rows.value.find(r => r.itemId === item.id);

  if(existing) {
    // CEK STOK: Jangan biarkan qty melebihi stok tersedia
    if(existing.qty + 1 > item.stok) {
      return alert(`Gagal: Stok hanya tersedia ${item.stok} unit.`);
    }
    existing.qty++;
  } else {
    // Jika baris pertama kosong, bersihkan dulu
    if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
    
    rows.value.push({ 
      unitName: "", kerusakan: "", solusi: "", 
      namaBarang: item.nama, qty: 1, harga: item.harga, 
      itemId: item.id, isChild: false 
    });
  }
};

const switchMode = (mode) => {
  if(cartCount.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });
const addSolution = (index) => rows.value.splice(index + 1, 0, { unitName: "", kerusakan: "", solusi: "Biaya Jasa/Part", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: true });

const removeRow = (index) => {
  rows.value.splice(index, 1);
  if(rows.value.length === 0) addNewUnit();
};

const getRelatedRows = (index) => rows.value.filter((r, i) => i === index || (i > index && r.isChild && !rows.value.slice(index + 1, i).some(x => !x.isChild)));
const getRowNumber = (index) => rows.value.slice(0, index + 1).filter(r => !r.isChild).length;

const saveNota = async () => {
  if (!form.value.customerNama) return alert("Nama Pelanggan wajib diisi!");
  if (cartCount.value === 0) return alert("Tambahkan minimal 1 item!");

  const totalInput = Number(form.value.dp) + Number(form.value.bayar);
  const isLunas = totalInput >= grandTotal.value;

  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : (isLunas ? 'LUNAS' : 'PROSES'),
    barangCustomer: form.value.tipe === 'SERVICE' ? (rows.value[0].unitName || 'Service') : (rows.value[0].namaBarang || 'Retail'),
    items: rows.value.map(r => ({
      itemId: r.itemId,
      namaBarang: r.namaBarang || r.solusi,
      hargaSatuan: r.harga,
      jumlah: r.qty,
      catatan: form.value.tipe === 'SERVICE' ? `Unit: ${r.unitName}` : 'Retail'
    }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    alert(isLunas ? "Transaksi Berhasil (LUNAS)!" : "Transaksi Disimpan!");
    router.push('/nota/' + res.data.id);
  } catch (e) { alert("Gagal Simpan"); }
};
</script>

<style scoped>
/* Scrollbar */
::-webkit-scrollbar { width: 4px; height: 4px; }
::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

/* Animasi Pulse untuk Stok Menipis */
.animate-pulse {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

/* Hide Spinner Number */
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
</style>