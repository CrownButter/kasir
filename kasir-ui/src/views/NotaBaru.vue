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
              <input v-model="form.customerNama" placeholder="Nama pelanggan" class="w-full p-3 bg-gray-50 border rounded-xl focus:ring-2 focus:ring-orange-200 outline-none transition font-bold" />
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
                  <span class="bg-white/20 w-8 h-8 flex items-center justify-center rounded-lg text-sm font-black">#{{ getRowNumber(index) }}</span>
                  <h3 class="font-bold uppercase tracking-wider text-sm">Unit Service</h3>
                </div>
                <button @click="removeRow(index)" class="hover:bg-red-500 p-2 rounded-lg transition"><i class="bi bi-trash3-fill"></i></button>
              </div>
              <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500">NAMA UNIT</label>
                    <input v-model="row.unitName" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 outline-none font-bold" />
                  </div>
                  <div class="space-y-2">
                    <label class="text-xs font-bold text-gray-500">KELUHAN</label>
                    <input v-model="row.kerusakan" class="w-full p-3 bg-gray-50 rounded-xl border-2 border-transparent focus:border-orange-400 outline-none" />
                  </div>
                </div>
                <div class="space-y-4">
                  <div class="flex justify-between items-center border-b pb-2">
                    <label class="text-xs font-black text-orange-600 uppercase">Tindakan & Sparepart</label>
                    <button @click="addSolution(index)" class="text-[10px] bg-orange-100 text-orange-700 px-3 py-1 rounded-full font-bold">Tambah</button>
                  </div>
                  <div class="space-y-3">
                    <div v-for="(subRow, subIndex) in getRelatedRows(index)" :key="subIndex" class="flex flex-col md:flex-row gap-3 p-4 bg-gray-50 rounded-xl border items-end">
                      <div class="flex-1 w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Solusi</label>
                        <input v-model="subRow.solusi" class="w-full p-2 bg-white border rounded-lg text-sm" />
                      </div>
                      <div class="flex-[1.5] w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Sparepart</label>
                        <ItemSearchInput v-model="subRow.namaBarang" :items="masterItems" @select="(item) => onItemSelect(subRow, item)" />
                      </div>
                      <div class="flex gap-2 w-full md:w-auto">
                        <div class="w-20">
                          <label class="text-[9px] font-black text-gray-400 uppercase">Qty</label>
                          <input type="number" v-model="subRow.qty" class="w-full p-2 bg-white border rounded-lg text-sm text-center" min="1" />
                        </div>
                        <div class="flex-1 md:w-32">
                          <label class="text-[9px] font-black text-gray-400 uppercase text-right block">Harga</label>
                          <input type="number" v-model="subRow.harga" class="w-full p-2 bg-white border rounded-lg text-sm text-right font-mono font-bold" />
                        </div>
                        <button v-if="subRow.isChild" @click="removeRow(index + subIndex)" class="p-2 text-red-400"><i class="bi bi-x-circle-fill"></i></button>
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
          <button @click="addNewUnit" class="w-full p-6 border-2 border-dashed border-gray-300 rounded-3xl text-gray-400 hover:text-orange-600 hover:bg-orange-50 transition-all flex flex-col items-center justify-center gap-2 group font-black uppercase text-xs">
            <i class="bi bi-plus-circle-dotted text-3xl"></i> Tambah Unit Service
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex-1 flex flex-col md:flex-row overflow-hidden relative">
      
      <div class="md:hidden flex bg-white border-b flex-shrink-0 z-20">
        <button @click="activeTab = 'katalog'" :class="activeTab === 'katalog' ? 'border-blue-600 text-blue-600 bg-blue-50/50' : 'border-transparent text-gray-500'" class="flex-1 py-3 text-[10px] font-black border-b-2 uppercase transition-all">
          <i class="bi bi-grid-fill mr-1"></i> Katalog
        </button>
        <button @click="activeTab = 'keranjang'" :class="activeTab === 'keranjang' ? 'border-blue-600 text-blue-600 bg-blue-50/50' : 'border-transparent text-gray-500'" class="flex-1 py-3 text-[10px] font-black border-b-2 uppercase transition-all flex items-center justify-center gap-2">
          <i class="bi bi-cart-fill"></i> Keranjang
          <span v-if="cartCount > 0" class="bg-red-500 text-white text-[9px] px-1.5 py-0.5 rounded-full animate-bounce">{{ cartCount }}</span>
        </button>
      </div>

      <div :class="{'hidden md:flex': activeTab !== 'keranjang', 'flex': activeTab === 'keranjang'}" 
           class="w-full md:w-1/2 flex-col bg-white border-r shadow-xl z-10 overflow-hidden">
        
        <div class="bg-black p-4 md:p-6 text-center border-b border-gray-800 flex-shrink-0">
          <p class="text-green-500 text-[10px] font-mono uppercase mb-1">Total Tagihan (IDR)</p>
          <h1 class="text-3xl md:text-5xl font-mono text-green-400 tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-4">
          <div class="bg-gray-50 p-3 rounded-xl border grid grid-cols-1 md:grid-cols-2 gap-3 shadow-inner">
            <input v-model="form.customerNama" placeholder="Nama Customer *" class="border p-3 rounded-lg text-xs font-bold focus:ring-2 focus:ring-blue-100 outline-none" />
            <input v-model="form.customerTelp" placeholder="No Telp" class="border p-3 rounded-lg text-xs focus:ring-2 focus:ring-blue-100 outline-none" />
          </div>

          <div class="space-y-2">
            <div v-if="cartCount === 0" class="py-10 text-center text-gray-300">
              <i class="bi bi-cart-x text-4xl block mb-2"></i>
              <p class="text-xs font-bold uppercase">Keranjang Kosong</p>
            </div>
            <div v-for="(row, index) in rows" :key="index" v-show="row.itemId || row.namaBarang" class="p-3 md:p-4 bg-white border rounded-xl shadow-sm flex justify-between items-start group hover:border-blue-300 transition">
              <div class="flex-1 min-w-0">
                <p class="font-bold text-sm text-gray-800 truncate">{{ row.namaBarang || 'Manual Item' }}</p>
                <div class="flex items-center gap-3 mt-1">
                  <div class="flex items-center border rounded-lg overflow-hidden bg-gray-50">
                    <button @click="row.qty > 1 ? row.qty-- : null" class="px-3 py-1 text-gray-500 text-xs active:bg-gray-200">-</button>
                    <input type="number" v-model="row.qty" class="w-10 text-center text-xs outline-none font-bold bg-transparent" min="1" />
                    <button @click="row.qty++" class="px-3 py-1 text-gray-500 text-xs active:bg-gray-200">+</button>
                  </div>
                  <span class="text-[10px] text-gray-400 font-mono">@ {{ formatNumber(row.harga) }}</span>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
                <button type="button" @click="removeRow(index)" class="text-red-300 hover:text-red-500 transition mt-2"><i class="bi bi-trash-fill"></i></button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div :class="{'hidden md:flex': activeTab !== 'katalog', 'flex': activeTab === 'katalog'}" 
           class="flex-1 bg-gray-200 flex-col overflow-hidden">
        
        <div class="p-3 md:p-4 bg-white shadow-sm flex gap-3 flex-shrink-0">
          <div class="relative flex-1">
            <i class="bi bi-search absolute left-4 top-3 text-gray-400"></i>
            <input v-model="search" placeholder="Cari barang atau kode..." class="w-full pl-11 pr-4 py-2.5 md:py-3 border rounded-full bg-gray-50 focus:bg-white focus:ring-2 focus:ring-blue-100 outline-none transition text-sm" />
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-3 md:p-4">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3 md:gap-4">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" 
                 class="bg-white rounded-xl md:rounded-2xl shadow-sm border-2 border-transparent hover:border-blue-500 cursor-pointer overflow-hidden transition transform active:scale-95 group relative">
              
              <span :class="[
                  'absolute top-2 right-2 px-2 py-0.5 rounded-full font-bold text-[9px] z-10 shadow-sm',
                  item.stok <= 0 ? 'bg-red-600 text-white' : 
                  item.stok < 5 ? 'bg-orange-500 text-white animate-pulse' : 
                  'bg-blue-100 text-blue-700'
              ]">
                {{ item.stok }}
              </span>

              <div class="h-24 md:h-32 bg-gray-50 flex items-center justify-center overflow-hidden">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover transition group-hover:scale-110" />
                <i v-else class="bi bi-box text-2xl text-gray-300"></i>
              </div>
              <div class="p-2 md:p-3 bg-white border-t border-gray-50">
                <h4 class="text-[10px] md:text-[11px] font-bold text-gray-700 line-clamp-2 h-7 md:h-8 uppercase leading-tight">{{ item.nama }}</h4>
                <div class="text-xs md:text-sm font-black text-blue-700 text-right mt-1 md:mt-2">{{ formatNumber(item.harga) }}</div>
              </div>
              
              <div v-if="item.stok <= 0" class="absolute inset-0 bg-white/60 flex items-center justify-center backdrop-blur-[1px]">
                 <span class="bg-red-600 text-white text-[9px] px-3 py-1 rounded-full font-black uppercase rotate-12 shadow-lg">Habis</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="p-4 md:p-6 bg-white border-t shadow-[0_-4px_10px_rgba(0,0,0,0.05)] z-30 flex-shrink-0">
      <div class="container mx-auto flex flex-col md:flex-row justify-end items-center gap-4 md:gap-8">
        
        <div class="flex w-full md:w-auto items-center justify-between md:justify-end gap-4">
          <div class="flex items-center gap-3 bg-gray-50 p-2 px-4 rounded-xl border flex-1 md:flex-none">
            <span class="font-black text-gray-400 text-[9px] uppercase">DP</span>
            <input v-model="form.dp" type="number" class="w-full md:w-24 text-right bg-transparent border-b-2 border-blue-200 outline-none focus:border-blue-600 font-mono text-base font-black text-gray-700" />
          </div>
          <div class="text-right">
            <p class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1">Sisa</p>
            <p class="text-lg md:text-2xl font-black text-red-600 font-mono leading-none">Rp{{ formatNumber(grandTotal - form.dp) }}</p>
          </div>
        </div>

        <button @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" 
                class="w-full md:w-auto px-10 py-3.5 md:py-4 text-white rounded-xl md:rounded-2xl font-black shadow-lg flex items-center justify-center gap-3 transition transform active:scale-95 uppercase tracking-wider text-xs md:text-sm">
          <i class="bi bi-shield-check text-lg"></i> Simpan Transaksi
        </button>
      </div>
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
const activeTab = ref("katalog");
const kasirNama = ref(localStorage.getItem('username') || 'Kasir');

const form = ref({ 
  customerNama: "", 
  customerTelp: "", 
  customerAlamat: "", 
  tipe: 'JUAL', 
  dp: 0, 
  bayar: 0 
});

const rows = ref([{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
  } catch(e) { console.error("Gagal load item"); }
});

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));

// LOGIKA SISA & KEMBALIAN
const sisaBayar = computed(() => {
  const sisa = grandTotal.value - Number(form.value.dp) - Number(form.value.bayar);
  return sisa > 0 ? sisa : 0;
});

const kembalian = computed(() => {
  const bayarUser = Number(form.value.dp) + Number(form.value.bayar);
  return bayarUser > grandTotal.value ? bayarUser - grandTotal.value : 0;
});

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || i.kode.toLowerCase().includes(s));
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const switchMode = (mode) => {
  if(grandTotal.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const onProductClick = (item) => {
  if(item.stok <= 0) return alert("Stok habis!");
  if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
  const existing = rows.value.find(r => r.itemId === item.id);
  if(existing) existing.qty++;
  else rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
};

const addNewUnit = () => rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });

const addSolution = (index) => {
  rows.value.splice(index + 1, 0, { unitName: "", kerusakan: "", solusi: "Tindakan Baru", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: true });
};

const removeRow = (index) => {
  rows.value.splice(index, 1);
  if(rows.value.length === 0) addNewUnit();
};

const getRelatedRows = (index) => rows.value.filter((r, i) => i === index || (i > index && r.isChild && !rows.value.slice(index + 1, i).some(x => !x.isChild)));

const getRowNumber = (index) => rows.value.slice(0, index + 1).filter(r => !r.isChild).length;

const saveNota = async () => {
  if (!form.value.customerNama) return alert("Nama Customer wajib diisi!");
  const validItems = rows.value.filter(r => r.namaBarang || r.solusi);
  if (validItems.length === 0) return alert("Keranjang kosong!");

  const totalBayarUser = Number(form.value.dp) + Number(form.value.bayar);

  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    // Jika Bayar + DP >= Total maka LUNAS, jika tidak maka PROSES
    status: totalBayarUser >= grandTotal.value ? 'LUNAS' : 'PROSES',
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
    alert(totalBayarUser >= grandTotal.value ? "Transaksi LUNAS!" : "Transaksi Disimpan!");
    router.push('/nota/' + res.data.id);
  } catch (e) { alert("Gagal Simpan"); }
};
</script>

<style scoped>
/* Scrollbar Styling */
::-webkit-scrollbar { width: 4px; height: 4px; }
::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

/* Pulse Animation for Low Stock */
.animate-pulse {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .6; }
}

/* Hide Spin Button for Number Input */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>