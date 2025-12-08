<template>
  <div class="flex flex-col h-[calc(100vh-80px)] relative">
    
    <div class="bg-white p-3 shadow mb-3 flex justify-center gap-4 rounded mx-4 mt-2">
      <button @click="switchMode('JUAL')"
        :class="mode === 'JUAL' ? 'bg-blue-600 text-white shadow-lg scale-105' : 'bg-gray-200 text-gray-600'"
        class="px-8 py-2 rounded-full font-bold transition-all duration-200 flex items-center gap-2">
        <i class="bi bi-bag-check-fill"></i> Penjualan (Retail)
      </button>

      <button @click="switchMode('SERVICE')"
        :class="mode === 'SERVICE' ? 'bg-orange-500 text-white shadow-lg scale-105' : 'bg-gray-200 text-gray-600'"
        class="px-8 py-2 rounded-full font-bold transition-all duration-200 flex items-center gap-2">
        <i class="bi bi-tools"></i> Service Masuk
      </button>
    </div>

    <div class="flex gap-4 px-4 h-full overflow-hidden pb-4">
      <div class="w-2/3 flex flex-col gap-3">
        <div class="bg-white p-3 rounded shadow flex items-center gap-2 border">
          <i class="bi bi-search text-gray-400 pl-2"></i>
          <input v-model="search" placeholder="Cari Barang / Sparepart..."
            class="w-full p-2 outline-none text-gray-700"/>
        </div>

        <div class="bg-gray-50 rounded p-2 overflow-y-auto flex-1 border">
          <div class="grid grid-cols-3 gap-3">
            <div @click="openManualModal"
              class="bg-yellow-100 p-4 rounded cursor-pointer hover:bg-yellow-200 border-2 border-yellow-400 border-dashed flex flex-col items-center justify-center h-32 transition group">
              <i class="bi bi-keyboard text-4xl text-yellow-800 mb-2 group-hover:scale-110 transition"></i>
              <span class="font-bold text-yellow-800">Input Manual</span>
              <span class="text-xs text-center text-yellow-700">(Jasa / Biaya Lain)</span>
            </div>

            <div v-for="item in filteredItems" :key="item.id"
              @click="addToCart(item)"
              class="bg-white p-4 rounded shadow cursor-pointer hover:bg-blue-50 hover:border-blue-300 border border-transparent transition h-32 flex flex-col justify-between group">
              <div>
                <div class="font-bold text-gray-800 leading-tight group-hover:text-blue-600">{{ item.nama }}</div>
                <div class="text-xs text-gray-500 mt-1 flex items-center gap-1">
                   <i class="bi bi-box-seam"></i> Stok: {{ item.stok }}
                </div>
              </div>
              <div class="text-blue-600 font-bold text-lg">Rp {{ formatRupiah(item.harga) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="w-1/3 bg-white p-4 rounded shadow flex flex-col h-full border-t-4"
        :class="mode === 'JUAL' ? 'border-t-blue-600' : 'border-t-orange-500'">
        
        <h2 class="text-xl font-bold mb-4 flex justify-between items-center">
          <span class="flex items-center gap-2">
             <i :class="mode === 'JUAL' ? 'bi-cart3' : 'bi-clipboard-data'"></i>
             {{ mode === 'JUAL' ? 'Kasir Retail' : 'Form Service' }}
          </span>
          <span class="text-xs bg-gray-100 px-2 py-1 rounded flex items-center gap-1">
             <i class="bi bi-calendar3"></i> {{ new Date().toLocaleDateString() }}
          </span>
        </h2>

        <div class="overflow-y-auto flex-1 pr-2">
            <div class="space-y-3 mb-4">
               <div class="bg-gray-50 p-3 rounded border">
                 <label class="text-xs font-bold text-gray-500 uppercase mb-1 block">Data Pelanggan</label>
                 <input v-model="form.customerNama" placeholder="Nama Pelanggan *" class="w-full p-2 border rounded mt-1 text-sm font-bold"/>
                 <input v-model="form.customerTelp" placeholder="No Telp / WA" class="w-full p-2 border rounded mt-2 text-sm"/>
                 <textarea v-model="form.customerAlamat" placeholder="Alamat" class="w-full p-2 border rounded mt-2 text-sm h-16"></textarea>
               </div>
               
               <div v-if="mode === 'SERVICE'" class="bg-orange-50 p-3 rounded border border-orange-200">
                  <label class="text-xs font-bold text-orange-800 uppercase flex items-center gap-1">
                    <i class="bi bi-laptop"></i> Unit & Keluhan
                  </label>
                  <input v-model="form.barangCustomer" placeholder="Contoh: Laptop Acer Nitro 5" class="w-full p-2 border rounded mt-1 text-sm"/>
                  <textarea v-model="form.keluhan" placeholder="Detail Keluhan: (Mati total, Layar pecah, dll)" class="w-full p-2 border rounded mt-2 text-sm h-20"></textarea>
                  
                  <label class="text-xs font-bold text-orange-800 uppercase mt-2 block">Status Awal</label>
                  <select v-model="form.status" class="w-full p-2 border rounded text-sm font-bold bg-white">
                     <option value="PROSES">Sedang Dikerjakan (PROSES)</option>
                     <option value="PENDING">Menunggu Sparepart (PENDING)</option>
                     <option value="SELESAI">Selesai (DONE)</option>
                  </select>
               </div>
            </div>

            <div class="border-t pt-2">
               <label class="text-xs font-bold text-gray-500 uppercase mb-2 block">Rincian Biaya / Barang</label>
               
               <div v-if="cart.length === 0" class="text-center py-8 bg-gray-50 rounded border border-dashed text-gray-400 text-sm flex flex-col items-center">
                  <i class="bi bi-basket text-3xl mb-2"></i>
                  {{ mode === 'SERVICE' ? 'Belum ada biaya/sparepart' : 'Keranjang Kosong' }}
               </div>

               <div v-else class="space-y-2">
                  <div v-for="(item, index) in cart" :key="index" class="flex justify-between items-start bg-gray-50 p-2 rounded border">
                     <div>
                        <div class="font-bold text-sm">{{ item.namaBarang }}</div>
                        <div class="text-xs text-gray-500">{{ item.jumlah }} x {{ formatRupiah(item.hargaSatuan) }}</div>
                        <div v-if="item.catatan" class="text-xs text-blue-500 italic">"{{ item.catatan }}"</div>
                     </div>
                     <div class="flex flex-col items-end">
                        <span class="font-bold text-sm">Rp {{ formatRupiah(item.jumlah * item.hargaSatuan) }}</span>
                        <button @click="cart.splice(index, 1)" class="text-red-500 text-sm mt-1 hover:text-red-700 transition" title="Hapus Item">
                           <i class="bi bi-trash-fill"></i>
                        </button>
                     </div>
                  </div>
               </div>
            </div>
        </div>

        <div class="border-t pt-3 mt-2 bg-white">
           <div class="flex justify-between items-center mb-2">
              <span class="text-gray-600 font-bold">Total Estimasi</span>
              <span class="text-2xl font-bold text-blue-600">Rp {{ formatRupiah(grandTotal) }}</span>
           </div>

           <div v-if="mode === 'SERVICE'" class="bg-yellow-50 text-yellow-800 text-xs p-2 rounded mb-3 border border-yellow-200 flex items-start gap-2">
              <i class="bi bi-info-circle-fill mt-0.5"></i>
              <span>Harga ini adalah estimasi awal. Harga final dapat berubah jika ada pergantian part tambahan.</span>
           </div>

           <button @click="processTransaction" 
             :disabled="loading"
             :class="mode === 'JUAL' ? 'bg-blue-600 hover:bg-blue-700' : 'bg-orange-500 hover:bg-orange-600'"
             class="w-full text-white py-3 rounded-lg font-bold shadow-lg transition-transform active:scale-95 disabled:bg-gray-400 disabled:cursor-not-allowed flex justify-center items-center gap-2">
              <i v-if="loading" class="bi bi-arrow-clockwise animate-spin"></i>
              <i v-else :class="mode === 'JUAL' ? 'bi-cash-coin' : 'bi-save2-fill'"></i>
              {{ loading ? 'Memproses...' : (mode === 'JUAL' ? 'BAYAR SEKARANG' : 'SIMPAN TANDA TERIMA') }}
           </button>
        </div>
      </div>
    </div>

    <div v-if="showManualModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
       <div class="bg-white rounded-lg shadow-xl w-96 p-6 transform transition-all scale-100">
          <h3 class="text-lg font-bold mb-4 border-b pb-2 flex items-center gap-2">
             <i class="bi bi-pencil-square"></i> Tambah Item / Jasa Manual
          </h3>
          <div class="space-y-4">
             <div>
                <label class="block text-sm font-bold mb-1">Nama Jasa / Barang *</label>
                <input v-model="manualForm.nama" placeholder="Contoh: Install Ulang Windows" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
             </div>
             <div>
                <label class="block text-sm font-bold mb-1">Harga / Biaya</label>
                <input v-model="manualForm.harga" type="number" placeholder="0" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
                <p class="text-xs text-gray-500 mt-1">* Isi 0 jika harga belum ditentukan (Service)</p>
             </div>
             <div>
                <label class="block text-sm font-bold mb-1">Catatan (Opsional)</label>
                <textarea v-model="manualForm.catatan" placeholder="Keterangan tambahan..." class="w-full border p-2 rounded h-20 focus:ring-2 focus:ring-blue-500 outline-none"></textarea>
             </div>
             <div class="flex gap-2 mt-6 justify-end">
                <button @click="showManualModal = false" class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded font-bold">Batal</button>
                <button @click="submitManualItem" class="px-6 py-2 bg-blue-600 text-white rounded font-bold hover:bg-blue-700 flex items-center gap-2">
                   <i class="bi bi-plus-lg"></i> Tambahkan
                </button>
             </div>
          </div>
       </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import api from '../api'; //

const mode = ref('JUAL'); 
const items = ref([]);
const cart = ref([]);
const search = ref("");
const loading = ref(false);

const showManualModal = ref(false);
const manualForm = ref({ nama: "", harga: 0, catatan: "" });

const form = ref({
  customerNama: "", customerTelp: "", customerAlamat: "",
  barangCustomer: "", keluhan: "", status: 'LUNAS'
});

const fetchItems = async () => {
  try {
    const res = await api.get('/api/items'); //
    items.value = res.data; 
  } catch(e) { console.error("Gagal load item"); }
};
onMounted(fetchItems);

const filteredItems = computed(() => {
  return items.value.filter(i => i.nama.toLowerCase().includes(search.value.toLowerCase())); //
});

const switchMode = (newMode) => {
  if(cart.value.length > 0 && !confirm("Keranjang akan dikosongkan saat ganti mode. Lanjut?")) return;
  mode.value = newMode;
  cart.value = [];
  form.value.status = newMode === 'SERVICE' ? 'PROSES' : 'LUNAS'; //
};

const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val); //
const grandTotal = computed(() => cart.value.reduce((sum, item) => sum + (item.hargaSatuan * item.jumlah), 0));

const addToCart = (item) => {
  if(item.stok <= 0) return alert("Stok Habis!");
  cart.value.push({
    itemId: item.id, namaBarang: item.nama, hargaSatuan: item.harga, jumlah: 1, catatan: ""
  });
};

const openManualModal = () => {
  manualForm.value = { nama: "", harga: "", catatan: "" };
  showManualModal.value = true;
};

const submitManualItem = () => {
  if(!manualForm.value.nama) { alert("Nama Jasa/Barang wajib diisi!"); return; }
  cart.value.push({
    itemId: null, namaBarang: manualForm.value.nama,
    hargaSatuan: parseInt(manualForm.value.harga || 0), jumlah: 1, catatan: manualForm.value.catatan
  });
  showManualModal.value = false;
};

const processTransaction = async () => {
  if(!form.value.customerNama) return alert("Nama Customer Wajib Diisi!");
  if(mode.value === 'SERVICE' && !form.value.barangCustomer) return alert("Nama Barang Customer Wajib Diisi!");
  if(mode.value === 'JUAL' && cart.value.length === 0) return alert("Pilih barang dulu!");

  if(!confirm(mode.value === 'SERVICE' ? "Simpan Data Service ini?" : "Proses Pembayaran?")) return;

  loading.value = true;
  const payload = {
    kasirId: 1,
    kasirNama: localStorage.getItem('username') || 'Kasir',
    customerNama: form.value.customerNama,
    customerTelp: form.value.customerTelp,
    customerAlamat: