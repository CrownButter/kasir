<template>
  <div class="flex flex-col h-screen bg-gray-100 overflow-hidden font-sans text-black relative">
    
    <transition name="toast">
      <div v-if="toast.show" class="fixed top-20 left-1/2 -translate-x-1/2 z-[100] bg-red-600 text-white px-6 py-3 rounded-2xl shadow-2xl flex items-center gap-3 border-2 border-white/20 backdrop-blur-md">
        <i class="bi bi-exclamation-triangle-fill text-xl"></i>
        <span class="font-bold text-sm">{{ toast.message }}</span>
      </div>
    </transition>

    <div class="p-3 bg-white shadow-sm flex justify-center items-center flex-shrink-0 z-40 border-b gap-4">
      <div class="flex bg-gray-100 p-1 rounded-full border">
        <button type="button" @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase">Retail / Jual</button>
        <button type="button" @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-md' : 'text-gray-500'" class="px-8 py-2 rounded-full text-xs font-bold transition-all uppercase">Service / Perbaikan</button>
      </div>
    </div>

    <div v-if="form.tipe === 'SERVICE'" class="flex-1 overflow-auto p-4 md:p-8 bg-gray-50">
      <div class="max-w-5xl mx-auto space-y-8 pb-40">
        
        <div class="bg-white rounded-2xl shadow-sm border p-6">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Nama Lengkap *</label>
              <input v-model="form.customerNama" placeholder="Nama Pelanggan" class="w-full p-3 bg-gray-50 border rounded-xl outline-none font-bold focus:ring-2 focus:ring-orange-200" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">WhatsApp</label>
              <input v-model="form.customerTelp" placeholder="08..." class="w-full p-3 bg-gray-50 border rounded-xl outline-none" />
            </div>
            <div class="space-y-1">
              <label class="text-[10px] font-black text-gray-400 uppercase">Alamat</label>
              <input v-model="form.customerAlamat" placeholder="Alamat" class="w-full p-3 bg-gray-50 border rounded-xl outline-none" />
            </div>
          </div>
        </div>

        <div class="space-y-6">
          <div v-for="(row, index) in rows" :key="index">
            <div v-if="!row.isChild" class="bg-white rounded-2xl shadow-md border overflow-hidden transition-all hover:shadow-lg">
              <div class="bg-orange-600 p-4 px-6 flex justify-between items-center text-white">
                <div class="flex items-center gap-3">
                  <span class="bg-white/20 w-8 h-8 flex items-center justify-center rounded-lg text-sm font-black">#{{ getRowNumber(index) }}</span>
                  <h3 class="font-bold uppercase tracking-wider text-sm">Unit Service</h3>
                </div>
                <button @click="removeRow(index)" class="hover:bg-red-500 p-2 rounded-lg transition"><i class="bi bi-trash3-fill"></i></button>
              </div>

              <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-1">
                    <label class="text-[10px] font-bold text-gray-400 uppercase">Nama Unit / Model</label>
                    <input v-model="row.unitName" placeholder="Contoh: Asus ROG Strix" class="w-full p-3 bg-gray-50 border rounded-xl font-bold outline-none focus:border-orange-400" />
                  </div>
                  <div class="space-y-1">
                    <label class="text-[10px] font-bold text-gray-400 uppercase">Keluhan / Kerusakan</label>
                    <input v-model="row.kerusakan" placeholder="Contoh: Mati Total" class="w-full p-3 bg-gray-50 border rounded-xl outline-none focus:border-orange-400" />
                  </div>
                </div>

                <div class="space-y-4">
                  <div class="flex justify-between items-center border-b pb-2">
                    <label class="text-[10px] font-black text-orange-600 uppercase tracking-widest">Tindakan & Sparepart</label>
                    <button @click="addSolution(index)" class="text-[10px] bg-orange-100 text-orange-700 px-3 py-1 rounded-full font-bold hover:bg-orange-200 transition">
                      <i class="bi bi-plus-lg"></i> Tambah Tindakan
                    </button>
                  </div>

                  <div class="space-y-3">
                    <div v-for="(subRow, subIndex) in getRelatedRows(index)" :key="subIndex" class="flex flex-col md:flex-row gap-3 p-4 bg-gray-50 rounded-xl border border-gray-100 items-end">
                      <div class="flex-1 w-full">
                        <label class="text-[9px] font-black text-gray-400 uppercase">Deskripsi</label>
                        <input v-model="subRow.solusi" placeholder="Tindakan atau Nama Barang..." class="w-full p-2 bg-white border rounded-lg text-sm outline-none font-bold" />
                      </div>
                      <div class="w-20">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-center block">Qty</label>
                        <input type="number" v-model="subRow.qty" class="w-full p-2 bg-white border rounded-lg text-sm text-center font-bold" min="1" />
                      </div>
                      <div class="w-32">
                        <label class="text-[9px] font-black text-gray-400 uppercase text-right block">Harga (Rp)</label>
                        <input 
                          type="text" 
                          :value="formatNumber(subRow.harga)" 
                          @input="onInputMoney($event, 'row_harga', {index: rows.indexOf(subRow)})"
                          class="w-full p-2 bg-white border rounded-lg text-sm text-right font-mono font-bold outline-none text-orange-600" 
                        />
                      </div>
                      <button v-if="subIndex > 0" @click="removeRow(rows.indexOf(subRow))" class="text-red-400 p-2 hover:text-red-600"><i class="bi bi-x-circle-fill"></i></button>
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

          <button @click="addNewUnit" class="w-full p-6 border-2 border-dashed border-gray-300 rounded-3xl text-gray-400 hover:text-orange-600 hover:bg-orange-50 transition-all flex flex-col items-center justify-center gap-2 group">
            <i class="bi bi-plus-circle-dotted text-3xl group-hover:scale-110 transition"></i>
            <span class="font-black uppercase text-[10px] tracking-widest">Tambah Unit Service Baru</span>
          </button>
        </div>
      </div>

      <div class="fixed bottom-0 left-0 right-0 p-6 bg-white border-t z-50 flex flex-col md:flex-row justify-center items-center gap-6 shadow-[0_-10px_20px_rgba(0,0,0,0.05)]">
         <div class="flex items-center gap-4 bg-orange-50 p-3 px-6 rounded-2xl border border-orange-200">
            <div class="text-left">
               <p class="text-[9px] font-bold text-orange-400 uppercase mb-1">Uang Muka (DP)</p>
               <input :value="formattedDp" @input="onInputMoney($event, 'dp')" type="text" class="w-32 text-left bg-transparent border-b-2 border-orange-300 outline-none focus:border-orange-600 font-mono text-xl font-black text-orange-700" placeholder="0" />
            </div>
         </div>
         <div class="text-right border-r pr-8 border-gray-200">
            <p class="text-[10px] font-bold text-gray-400 uppercase">Sisa Pembayaran</p>
            <p class="text-3xl font-black text-red-600 font-mono leading-none">{{ formatNumber(sisaBayar) }}</p>
         </div>
         <button @click="saveNota" class="bg-orange-600 text-white px-16 py-5 rounded-2xl font-black uppercase shadow-xl hover:bg-orange-700 transition transform active:scale-95">Simpan Nota Service</button>
      </div>
    </div>

    <div v-else class="flex-1 flex overflow-hidden">
      
      <div class="w-full md:w-[380px] flex flex-col bg-white border-r shadow-2xl z-30 overflow-hidden">
        <div class="bg-slate-900 p-6 text-center">
          <p class="text-blue-400 text-[10px] font-black uppercase tracking-widest mb-1">Total Tagihan</p>
          <h1 class="text-4xl font-mono text-white font-black tracking-tighter">{{ formatNumber(grandTotal) }}</h1>
        </div>

        <div class="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50/50">
          <input v-model="form.customerNama" placeholder="Nama Pelanggan *" class="w-full border-2 p-3 rounded-xl text-sm font-bold outline-none focus:border-blue-500 shadow-sm" />
          
          <div v-for="(row, index) in rows" :key="index" v-show="row.namaBarang && row.itemId" class="flex justify-between items-center p-3 bg-white rounded-xl border shadow-sm">
            <div class="flex-1 min-w-0">
              <p class="font-bold text-[11px] text-slate-800 uppercase truncate">{{ row.namaBarang }}</p>
              <p class="text-[10px] text-slate-400 font-mono">{{ row.qty }} x {{ formatNumber(row.harga) }}</p>
            </div>
            <div class="flex items-center gap-3">
               <p class="font-black text-sm text-blue-600 font-mono">{{ formatNumber(row.qty * row.harga) }}</p>
               <button @click="removeRow(index)" class="text-red-300 hover:text-red-500"><i class="bi bi-trash-fill"></i></button>
            </div>
          </div>
          <div v-if="cartCount === 0" class="text-center py-10 text-gray-300 italic text-xs">Keranjang Kosong</div>
        </div>

        <div class="p-4 bg-white border-t space-y-3">
          <div class="grid grid-cols-2 gap-3">
            <div class="bg-gray-50 p-2 rounded-lg border">
              <label class="text-[9px] font-black text-gray-400 uppercase block mb-1">Titipan (DP)</label>
              <input :value="formattedDp" @input="onInputMoney($event, 'dp')" type="text" class="w-full bg-transparent font-mono font-bold text-sm outline-none text-gray-700" placeholder="0" />
            </div>
            <div class="bg-blue-50 p-2 rounded-lg border border-blue-100">
              <label class="text-[9px] font-black text-blue-400 uppercase block mb-1">Bayar (Cash)</label>
              <input :value="formattedBayar" @input="onInputMoney($event, 'bayar')" type="text" class="w-full bg-transparent font-mono font-bold text-sm outline-none text-blue-600" placeholder="0" />
            </div>
          </div>
          <div class="flex justify-between p-3 bg-slate-100 rounded-xl">
            <div class="text-left border-r pr-4 border-gray-300 flex-1">
               <p class="text-[8px] font-bold text-gray-500 uppercase">Sisa</p>
               <p class="font-mono font-black text-red-600 text-sm">{{ formatNumber(sisaBayar) }}</p>
            </div>
            <div class="text-right flex-1 pl-4">
               <p class="text-[8px] font-bold text-gray-500 uppercase">Kembali</p>
               <p class="font-mono font-black text-green-600 text-sm">{{ formatNumber(kembalian) }}</p>
            </div>
          </div>
          <button @click="saveNota" class="w-full bg-blue-600 hover:bg-blue-700 text-white py-3.5 rounded-xl font-bold uppercase text-[10px] tracking-widest shadow-lg flex items-center justify-center gap-2">
            <i class="bi bi-check-circle-fill"></i> Simpan Transaksi
          </button>
        </div>
      </div>

      <div class="flex-1 bg-gray-100 flex flex-col overflow-hidden">
        <div class="p-4 bg-white border-b shadow-sm">
          <div class="relative max-w-2xl mx-auto">
            <i class="bi bi-upc-scan absolute left-5 top-4 text-blue-500"></i>
            <input v-model="search" @keyup.enter="handleBarcodeEnter" ref="barcodeField" placeholder="Ketik nama atau Scan Barcode..." class="w-full pl-12 p-4 rounded-2xl bg-gray-100 outline-none focus:bg-white focus:ring-4 focus:ring-blue-100 transition text-sm font-bold shadow-inner" />
          </div>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-5">
            <div v-for="item in filteredItems" :key="item.id" @click="onProductClick(item)" class="bg-white p-3 rounded-[1.5rem] border-2 border-transparent hover:border-blue-500 cursor-pointer shadow-sm active:scale-95 transition-all text-center group">
              <div class="w-full aspect-square bg-gray-50 rounded-[1rem] mb-2 flex items-center justify-center overflow-hidden border">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover group-hover:scale-110 transition duration-500" />
                <i v-else class="bi bi-box-seam text-2xl text-gray-200"></i>
              </div>
              <h4 class="text-[10px] font-black text-gray-700 uppercase h-8 line-clamp-2 px-1 leading-tight">{{ item.nama }}</h4>
              <p class="text-blue-600 font-black text-[11px] font-mono mt-1">{{ formatNumber(item.harga) }}</p>
            </div>
          </div>
        </div>
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

const form = ref({ customerNama: "", customerTelp: "", customerAlamat: "", tipe: 'JUAL', dp: 0, bayar: 0 });
const rows = ref([{ unitName: "", kerusakan: "", solusi: "Biaya Service", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false }]);

onMounted(async () => {
  try {
    const res = await api.get('/api/items');
    masterItems.value = res.data;
    if(barcodeField.value) barcodeField.value.focus();
  } catch(e) { console.error("Gagal load item"); }
});

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

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const sisaBayar = computed(() => Math.max(0, grandTotal.value - (Number(form.value.dp) + Number(form.value.bayar))));
const kembalian = computed(() => Math.max(0, (Number(form.value.dp) + Number(form.value.bayar)) - grandTotal.value));
const cartCount = computed(() => rows.value.filter(r => r.itemId).length);

const filteredItems = computed(() => {
  const s = search.value.toLowerCase();
  if(!s) return masterItems.value;
  return masterItems.value.filter(i => i.nama.toLowerCase().includes(s) || (i.kode && i.kode.toLowerCase().includes(s)));
});

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
  if(form.value.tipe === 'SERVICE') {
    let lastParentIndex = -1;
    for(let i = rows.value.length - 1; i >= 0; i--) { if(!rows.value[i].isChild) { lastParentIndex = i; break; } }
    rows.value.splice(lastParentIndex + 1, 0, { solusi: "Barang: " + item.nama, namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: true });
  } else {
    const existing = rows.value.find(r => r.itemId === item.id);
    if(existing) { existing.qty++; } else {
      if(rows.value.length === 1 && !rows.value[0].itemId && !rows.value[0].namaBarang) rows.value = [];
      rows.value.push({ namaBarang: item.nama, qty: 1, harga: item.harga, itemId: item.id, isChild: false });
    }
  }
};

const switchMode = (mode) => {
  if(grandTotal.value > 0 && !confirm("Keranjang akan dikosongkan?")) return;
  form.value.tipe = mode;
  form.value.dp = 0; form.value.bayar = 0;
  rows.value = [{ unitName: "", kerusakan: "", solusi: "Biaya Service", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

const addNewUnit = () => {
  rows.value.push({ unitName: "", kerusakan: "", solusi: "Biaya Service", namaBarang: "", qty: 1, harga: 0, itemId: null, isChild: false });
};

const addSolution = (index) => {
  let lastIdx = index;
  for(let i = index + 1; i < rows.value.length; i++) { if(rows.value[i].isChild) lastIdx = i; else break; }
  rows.value.splice(lastIdx + 1, 0, { solusi: "Tindakan Baru", harga: 0, qty: 1, isChild: true });
};

const removeRow = (index) => {
  if(!rows.value[index].isChild) {
    let count = 1;
    for(let i = index + 1; i < rows.value.length; i++) { if(rows.value[i].isChild) count++; else break; }
    rows.value.splice(index, count);
  } else { rows.value.splice(index, 1); }
  if(rows.value.length === 0) addNewUnit();
};

const getRelatedRows = (mainIdx) => {
  const res = []; res.push(rows.value[mainIdx]);
  for(let i = mainIdx + 1; i < rows.value.length; i++) { if(rows.value[i].isChild) res.push(rows.value[i]); else break; }
  return res;
};

const getRowNumber = (idx) => {
  let c = 0;
  for(let i = 0; i <= idx; i++) { if(!rows.value[i].isChild) c++; }
  return c;
};

const calculateUnitTotal = (mainIdx) => getRelatedRows(mainIdx).reduce((s, r) => s + (r.qty * r.harga), 0);

const saveNota = async () => {
  if(!form.value.customerNama) return triggerToast("Nama Pelanggan wajib diisi!");
  const payload = {
    ...form.value,
    kasirNama: kasirNama.value,
    kasirId: 1,
    status: form.value.tipe === 'SERVICE' ? 'PROSES' : (sisaBayar.value <= 0 ? 'LUNAS' : 'PROSES'),
    barangCustomer: form.value.tipe === 'SERVICE' ? (rows.value[0].unitName || 'Unit') : (rows.value[0]?.namaBarang || 'Retail'),
    items: rows.value.filter(r => r.namaBarang || r.solusi).map(r => ({
      itemId: r.itemId,
      namaBarang: r.itemId ? r.namaBarang : r.solusi,
      hargaSatuan: r.harga,
      jumlah: r.qty,
      catatan: form.value.tipe === 'SERVICE' ? 'Service Order' : 'Retail'
    }))
  };

  try {
    const res = await api.post('/api/nota', payload);
    router.push('/nota/' + res.data.id);
  } catch(e) { triggerToast("Gagal simpan transaksi"); }
};
</script>

<style scoped>
::-webkit-scrollbar { width: 5px; height: 5px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
</style>