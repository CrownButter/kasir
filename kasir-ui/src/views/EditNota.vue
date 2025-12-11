<template>
  <div class="container mx-auto p-4 max-w-6xl">
    <div class="bg-white shadow-lg rounded-lg p-6 min-h-screen border-t-4 border-yellow-500">
      
      <div class="flex justify-between items-center mb-6 border-b pb-4">
        <div>
           <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
             <i class="bi bi-pencil-square"></i> Edit Nota: <span class="text-blue-600 font-mono">{{ kodeNota }}</span>
           </h1>
           <p class="text-xs text-red-500 font-bold mt-1 bg-red-50 px-2 py-1 rounded inline-block border border-red-200">
             <i class="bi bi-exclamation-triangle"></i> PERINGATAN: Menyimpan perubahan akan me-reset stok lama ke gudang, lalu memotong stok baru!
           </p>
        </div>
        <button @click="$router.push('/')" class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded font-bold transition flex items-center gap-2">
           <i class="bi bi-arrow-left"></i> Kembali
        </button>
      </div>

      <div class="flex gap-6 mb-6">
        <div class="w-1/3 space-y-3">
           <h3 class="font-bold text-gray-700 border-b pb-1">Data Pelanggan</h3>
           
           <div>
             <label class="text-xs font-bold text-gray-500">Nama Pelanggan</label>
             <input v-model="form.customerNama" class="w-full border p-2 rounded bg-gray-50 font-bold outline-none focus:ring-2 focus:ring-yellow-400" placeholder="Nama Customer"/>
           </div>
           
           <div>
             <label class="text-xs font-bold text-gray-500">No. Telp / WA</label>
             <input v-model="form.customerTelp" class="w-full border p-2 rounded outline-none focus:ring-2 focus:ring-yellow-400" placeholder="08xxx"/>
           </div>
           
           <div>
             <label class="text-xs font-bold text-gray-500">Alamat</label>
             <textarea v-model="form.customerAlamat" class="w-full border p-2 rounded h-20 resize-none outline-none focus:ring-2 focus:ring-yellow-400"></textarea>
           </div>
        </div>

        <div class="w-1/3 space-y-3">
           <h3 class="font-bold text-gray-700 border-b pb-1">Informasi Unit</h3>

           <div>
             <label class="text-xs font-bold text-gray-500">Unit / Barang Service</label>
             <input v-model="form.barangCustomer" class="w-full border p-2 rounded outline-none focus:ring-2 focus:ring-yellow-400" placeholder="Contoh: Laptop Asus..."/>
           </div>
           
           <div>
             <label class="text-xs font-bold text-gray-500">Keluhan / Masalah</label>
             <textarea v-model="form.keluhan" class="w-full border p-2 rounded h-20 resize-none outline-none focus:ring-2 focus:ring-yellow-400"></textarea>
           </div>
           
           <div>
               <label class="text-xs font-bold text-gray-500">Status Pengerjaan</label>
               <div class="border p-2 rounded bg-gray-100 text-gray-600 font-bold cursor-not-allowed select-none">
                   {{ form.status }}
               </div>
               <p class="text-[10px] text-blue-500 italic mt-1 flex items-center gap-1">
                  <i class="bi bi-info-circle"></i> Gunakan tombol "Detail" di halaman depan untuk ubah status.
               </p>
           </div>
        </div>
        
        <div class="w-1/3 flex flex-col justify-end">
            <div class="bg-blue-50 p-4 rounded border border-blue-200 shadow-sm">
                <p class="text-sm font-bold text-blue-800 mb-2"><i class="bi bi-info-square-fill"></i> Info Item & Stok</p>
                <ul class="text-xs text-gray-600 list-disc pl-4 space-y-1">
                    <li>Item dengan label <span class="bg-green-100 text-green-800 px-1 rounded font-bold">Stok</span> akan otomatis memotong stok gudang.</li>
                    <li>Harga modal akan terkunci saat tombol simpan ditekan.</li>
                    <li>Item tanpa label stok dianggap sebagai Jasa/Manual.</li>
                </ul>
            </div>
        </div>
      </div>

      <div class="overflow-x-auto mb-24 border rounded-lg">
        <table class="w-full border-collapse">
           <thead class="bg-gray-800 text-white text-sm">
             <tr>
               <th class="p-3 w-10 text-center">No</th>
               <th class="p-3 text-left w-1/3">Nama Barang / Jasa</th>
               <th class="p-3 w-20 text-center">Qty</th>
               <th class="p-3 text-left">Catatan / Spek</th>
               <th class="p-3 w-32 text-right">Harga Satuan</th>
               <th class="p-3 w-32 text-right">Subtotal</th>
               <th class="p-3 w-16 text-center">Hapus</th>
             </tr>
           </thead>
           <tbody class="divide-y divide-gray-200">
             <tr v-for="(row, index) in rows" :key="index" class="hover:bg-yellow-50 transition-colors">
               <td class="p-2 text-center text-gray-500">{{ index + 1 }}</td>
               
               <td class="p-2 relative">
                   <ItemSearchInput 
                        v-model="row.namaBarang"
                        :items="masterItems"
                        @select="(item) => onItemSelect(row, item)"
                   />
                   <div v-if="row.itemId" class="absolute right-2 top-2 text-[10px] bg-green-100 text-green-700 px-2 py-0.5 rounded-full border border-green-200 pointer-events-none font-bold">
                       <i class="bi bi-box-seam"></i> Stok
                   </div>
               </td>

               <td class="p-2">
                   <input type="number" v-model="row.qty" class="w-full text-center outline-none bg-transparent border-b border-dashed focus:border-blue-500" min="1"/>
               </td>
               <td class="p-2">
                   <input v-model="row.catatan" class="w-full outline-none bg-transparent text-sm border-b border-dashed focus:border-blue-500 placeholder-gray-300" placeholder="-"/>
               </td>
               <td class="p-2">
                   <input type="number" v-model="row.harga" class="w-full text-right outline-none bg-transparent border-b border-dashed focus:border-blue-500 font-mono" placeholder="0"/>
               </td>
               <td class="p-2 text-right font-bold text-gray-700 font-mono">
                   {{ formatNumber(row.qty * row.harga) }}
               </td>
               <td class="p-2 text-center">
                 <button @click="rows.splice(index, 1)" class="text-red-400 hover:text-red-600 font-bold transition">
                    <i class="bi bi-trash-fill"></i>
                 </button>
               </td>
             </tr>
           </tbody>
        </table>
        
        <div class="p-2 bg-gray-50 border-t flex justify-between items-center">
            <button @click="addRow" class="bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded font-bold hover:bg-gray-100 hover:text-blue-600 transition flex items-center gap-2 shadow-sm">
                <i class="bi bi-plus-circle"></i> Tambah Baris Manual
            </button>
            <span class="text-xs text-gray-400 italic">Pastikan data benar sebelum disimpan</span>
        </div>
      </div>

      <div class="fixed bottom-0 left-0 w-full bg-white border-t p-4 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.1)] flex justify-end gap-8 items-center z-40">
          <div class="text-right">
              <p class="text-gray-500 text-xs font-bold uppercase">Total Tagihan</p>
              <p class="text-2xl font-bold text-blue-800 font-mono">Rp {{ formatNumber(grandTotal) }}</p>
          </div>
          
          <div class="flex items-center gap-2 bg-gray-50 p-2 rounded border">
              <span class="font-bold text-gray-600 text-sm">DP (Uang Muka):</span>
              <span class="font-bold text-gray-600">Rp</span>
              <input v-model="form.dp" type="number" class="w-24 text-right outline-none bg-transparent font-bold border-b border-gray-400 focus:border-blue-600"/>
          </div>
          
          <div class="text-right border-l pl-6 pr-4">
              <p class="text-gray-500 text-xs font-bold uppercase">Sisa Pembayaran</p>
              <p class="text-xl font-bold text-red-600 font-mono">Rp {{ formatNumber(grandTotal - form.dp) }}</p>
          </div>
          
          <button @click="updateNota" class="bg-yellow-500 hover:bg-yellow-600 text-white px-8 py-3 rounded-lg font-bold shadow-lg transform active:scale-95 transition flex items-center gap-2">
               <i class="bi bi-save-fill"></i> SIMPAN PERUBAHAN
          </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import ItemSearchInput from '../components/ItemSearchInput.vue'; // Import komponen pencarian

const route = useRoute();
const router = useRouter();
const kodeNota = ref("");
const masterItems = ref([]); // Menyimpan data stok dari backend

const form = ref({
  customerNama: "", 
  customerTelp: "", 
  customerAlamat: "",
  barangCustomer: "", 
  keluhan: "", 
  status: "PROSES", 
  dp: 0,
  tipe: "SERVICE"
});

const rows = ref([]);

// 1. Load Data (Nota Detail + Master Stok)
onMounted(async () => {
   try {
     const id = route.params.id;
     
     // Load Master Item untuk Autocomplete
     const resItems = await api.get('/api/items');
     masterItems.value = resItems.data;

     // Load Detail Nota
     const res = await api.get(`/api/nota/${id}`);
     const data = res.data;

     kodeNota.value = data.kodeNota;
     form.value = {
        customerNama: data.customerNama,
        customerTelp: data.customerTelp,
        customerAlamat: data.customerAlamat,
        barangCustomer: data.barangCustomer,
        keluhan: data.keluhan,
        status: data.status,
        dp: data.dp,
        tipe: data.tipe
     };

     // Map snapshots backend ke format rows frontend
     if(data.snapshots) {
         rows.value = data.snapshots.map(item => ({
            namaBarang: item.namaBarang,
            qty: item.jumlah,
            harga: item.hargaSatuan,
            catatan: item.catatan,
            itemId: item.itemId // Penting: Jika ada ID, ini barang stok
         }));
     }

   } catch(e) {
     console.error(e);
     alert("Gagal memuat data!");
     router.push('/');
   }
});

const addRow = () => {
   rows.value.push({ namaBarang: "", qty: 1, harga: 0, catatan: "", itemId: null });
};

// Handler saat barang dipilih dari dropdown
const onItemSelect = (row, item) => {
    row.namaBarang = item.nama;
    row.itemId = item.id;   // Set ID Stok
    row.harga = item.harga; // Set Harga Jual otomatis
    // Catatan dibiarkan kosong atau bisa diisi default
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const updateNota = async () => {
  if(!confirm("PERINGATAN SISTEM:\n\nStok barang lama di nota ini akan DIKEMBALIKAN ke gudang.\nStok barang baru akan DIPOTONG dari gudang.\n\nLanjutkan simpan perubahan?")) return;

  const payload = {
      ...form.value,
      // Map rows ke format backend
      items: rows.value.map(r => ({
          itemId: r.itemId, // null = Manual/Jasa, angka = Stok
          namaBarang: r.namaBarang,
          catatan: r.catatan,
          hargaSatuan: r.harga,
          jumlah: r.qty
      }))
  };

  try {
      await api.put(`/api/nota/${route.params.id}`, payload);
      alert("Nota berhasil diupdate!");
      router.push('/'); 
  } catch (e) {
      alert("Gagal update: " + (e.response?.data?.message || e.message));
  }
};
</script>