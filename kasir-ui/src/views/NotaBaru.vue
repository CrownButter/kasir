<template>
  <div class="container mx-auto p-4 max-w-[98%]">
    <div class="bg-white shadow-lg rounded-lg p-6 min-h-screen">
      <div class="flex justify-between items-start mb-6 border-b pb-4">
        <div>
           <h1 class="text-2xl font-bold text-gray-800 mb-2">Form Transaksi</h1>
           <div class="flex gap-2">
              <button @click="switchMode('SERVICE')" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-lg scale-105' : 'bg-gray-200 text-gray-600'" class="px-6 py-2 rounded-full font-bold transition transform duration-200 flex items-center gap-2">
                  <i class="bi bi-tools"></i> Service
              </button>
              <button @click="switchMode('JUAL')" :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-lg scale-105' : 'bg-gray-200 text-gray-600'" class="px-6 py-2 rounded-full font-bold transition transform duration-200 flex items-center gap-2">
                  <i class="bi bi-cart4"></i> Penjualan Retail
              </button>
           </div>
        </div>
        <div class="grid grid-cols-1 gap-2 w-1/3">
           <input v-model="form.customerNama" placeholder="Nama Customer *" class="border p-2 rounded bg-gray-50 font-bold outline-none focus:ring-2 focus:ring-blue-500" />
           <input v-model="form.customerTelp" placeholder="No Telp / WA" class="border p-2 rounded outline-none" />
           <textarea v-model="form.customerAlamat" placeholder="Alamat" class="border p-2 rounded h-16 resize-none outline-none"></textarea>
        </div>
      </div>

      <div class="overflow-x-auto mb-24">
        <table class="w-full border-collapse border border-gray-300">
           
           <thead :class="form.tipe === 'SERVICE' ? 'bg-orange-600' : 'bg-blue-600'" class="text-white text-sm transition-colors duration-300">
             <tr>
               <th class="border border-white/20 p-2 w-8 text-center">No</th>
               
               <template v-if="form.tipe === 'SERVICE'">
                   <th class="border border-white/20 p-2 text-left w-[20%]">Unit / Barang Customer</th>
                   <th class="border border-white/20 p-2 text-left w-[15%]">Kerusakan</th>
                   <th class="border border-white/20 p-2 text-left w-[15%]">Solusi / Tindakan</th>
                   <th class="border border-white/20 p-2 text-left w-[20%] bg-orange-700">Ganti Sparepart / (Stok)</th>
               </template>

               <template v-else>
                   <th class="border border-white/20 p-2 text-left w-[40%]">Nama Barang (Stok)</th>
                   <th class="border border-white/20 p-2 text-left w-[30%]">Catatan</th>
               </template>

               <th class="border border-white/20 p-2 w-12 text-center">Qty</th>
               <th class="border border-white/20 p-2 w-28 text-right">Harga</th>
               <th class="border border-white/20 p-2 w-28 text-right">Total</th>
               <th class="border border-white/20 p-2 w-16 text-center">Aksi</th>
             </tr>
           </thead>

           <tbody>
             <tr v-for="(row, index) in rows" :key="index" class="hover:bg-gray-50 border-b relative">
               
               <td class="border-r p-2 text-center text-xs text-gray-500 bg-white">
                   {{ row.isChild ? '' : getRowNumber(index) }}
               </td>
               
               <template v-if="form.tipe === 'SERVICE'">
                   <td class="border-r p-1 relative">
                       <input v-if="!row.isChild" v-model="row.unitName" class="w-full p-1 outline-none bg-transparent font-bold placeholder-gray-300" placeholder="Laptop/PC..."/>
                       <div v-else class="flex items-center h-full pl-4">
                           <span class="text-gray-300 text-2xl leading-none select-none">↳</span>
                           <span class="text-xs text-gray-400 italic ml-2">(Sama dengan atas)</span>
                       </div>
                   </td>
                   
                   <td class="border-r p-1">
                       <input v-if="!row.isChild" v-model="row.kerusakan" class="w-full p-1 outline-none bg-transparent text-sm placeholder-gray-300" placeholder="Kondisi..."/>
                       <div v-else class="text-center text-gray-300 select-none">"</div>
                   </td>

                   <td class="border-r p-1">
                       <input v-model="row.solusi" class="w-full p-1 outline-none bg-transparent text-sm placeholder-gray-300 text-blue-700 font-medium" placeholder="Tindakan..."/>
                   </td>

                   <td class="border-r p-1 bg-yellow-50 relative">
                       <ItemSearchInput 
                            v-model="row.namaBarang"
                            :items="masterItems"
                            @select="(item) => onItemSelect(row, item)"
                       />
                       <div v-if="row.itemId" class="absolute right-1 top-1 text-[9px] bg-green-100 text-green-700 px-1 rounded border border-green-200 pointer-events-none font-bold">STOK</div>
                   </td>
               </template>

               <template v-else>
                   <td class="border-r p-1 relative">
                       <ItemSearchInput v-model="row.namaBarang" :items="masterItems" @select="(item) => onItemSelect(row, item)" />
                       <div v-if="row.itemId" class="absolute right-2 top-2 text-[10px] bg-green-100 text-green-700 px-1 rounded border border-green-200 font-bold">STOK</div>
                   </td>
                   <td class="border-r p-1">
                       <input v-model="row.catatan" class="w-full p-1 outline-none bg-transparent text-sm" placeholder="Keterangan..."/>
                   </td>
               </template>

               <td class="border-r p-1">
                   <input type="number" v-model="row.qty" class="w-full p-1 text-center outline-none bg-transparent" min="1" />
               </td>
               <td class="border-r p-1">
                   <input type="number" v-model="row.harga" class="w-full p-1 text-right outline-none bg-transparent font-mono focus:bg-yellow-100" placeholder="0"/>
               </td>
               <td class="border-r p-2 text-right font-bold text-gray-700 font-mono bg-gray-50">
                   {{ formatNumber(row.qty * row.harga) }}
               </td>
               
               <td class="p-1 text-center">
                   <div class="flex items-center justify-center gap-1">
                       <button v-if="form.tipe === 'SERVICE' && !row.isChild" @click="addSolution(index)" class="bg-green-100 hover:bg-green-200 text-green-700 p-1 rounded transition" title="Tambah Tindakan/Part Lain untuk Unit ini">
                           <i class="bi bi-plus-lg text-xs font-bold"></i>
                       </button>

                       <button @click="removeRow(index)" class="bg-red-50 hover:bg-red-100 text-red-500 p-1 rounded transition">
                           <i class="bi bi-trash-fill text-xs"></i>
                       </button>
                   </div>
               </td>
             </tr>
           </tbody>
        </table>
        
        <div class="mt-2">
            <button @click="addNewUnit" class="bg-gray-100 text-gray-700 px-4 py-2 rounded font-bold hover:bg-gray-200 border border-gray-300 flex items-center gap-2 transition">
                <i class="bi bi-pc-display"></i> Tambah Unit / Item Baru
            </button>
        </div>
      </div>

      <div class="fixed bottom-0 left-0 w-full bg-white border-t p-4 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.1)] flex justify-end gap-8 items-center z-40">
          <div class="text-right">
              <p class="text-gray-500 text-xs font-bold uppercase">Total Tagihan</p>
              <p class="text-2xl font-bold text-blue-800 font-mono">Rp {{ formatNumber(grandTotal) }}</p>
          </div>
          <div class="flex items-center gap-2 bg-gray-50 p-2 rounded border">
              <span class="font-bold text-gray-600">DP:</span>
              <input v-model="form.dp" type="number" class="border-b border-gray-400 w-24 text-right font-bold outline-none bg-transparent focus:border-blue-600"/>
          </div>
          <div class="text-right border-l pl-4">
              <p class="text-gray-500 text-xs font-bold uppercase">Sisa Bayar</p>
              <p class="text-xl font-bold text-red-600 font-mono">Rp {{ formatNumber(grandTotal - form.dp) }}</p>
          </div>
          <button @click="saveNota" :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" class="text-white px-8 py-3 rounded-lg font-bold shadow-lg transform active:scale-95 transition flex items-center gap-2">
              <i class="bi bi-save2"></i> SIMPAN NOTA
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

const form = ref({
  customerNama: "", customerTelp: "", customerAlamat: "",
  tipe: 'SERVICE', dp: 0
});

// Row Template: unitName & kerusakan untuk Parent; solusi & namaBarang untuk Action
const rows = ref([
  { unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }
]);

onMounted(async () => {
    try {
        const res = await api.get('/api/items');
        masterItems.value = res.data;
    } catch(e) { console.error("Gagal load stok"); }
});

const switchMode = (mode) => {
    form.value.tipe = mode;
    rows.value = [{ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false }];
};

// Tambah Unit Baru (Parent)
const addNewUnit = () => {
   rows.value.push({ unitName: "", kerusakan: "", solusi: "", namaBarang: "", catatan: "", qty: 1, harga: 0, itemId: null, isChild: false });
};

// Tambah Solusi/Sparepart pada Unit yang sama (Child)
const addSolution = (index) => {
    rows.value.splice(index + 1, 0, {
        unitName: "", // Kosong di UI, tapi nanti mewarisi saat save
        kerusakan: "", // Kosong di UI
        solusi: "",
        namaBarang: "", 
        catatan: "",
        qty: 1,
        harga: 0,
        itemId: null,
        isChild: true
    });
};

const removeRow = (index) => {
    rows.value.splice(index, 1);
    if(rows.value.length === 0) addNewUnit();
};

// Auto fill data sparepart
const onItemSelect = (row, item) => {
    row.namaBarang = item.nama;
    row.itemId = item.id;
    row.harga = item.harga;
};

// Helper Nomor Urut (Hanya Parent yang dihitung)
const getRowNumber = (currentIndex) => {
    let count = 0;
    for(let i=0; i<=currentIndex; i++) {
        if(!rows.value[i].isChild) count++;
    }
    return count;
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const saveNota = async () => {
  if(!form.value.customerNama) return alert("Nama Customer wajib diisi!");

  let lastUnitName = "-";
  let lastKerusakan = "-";

  // MAPPING DATA KE FORMAT BACKEND
  const itemsPayload = rows.value.map(r => {
      let finalNamaBarang = "";
      let finalCatatan = "";

      if (form.value.tipe === 'SERVICE') {
          // LOGIKA WARISAN (INHERITANCE)
          if (!r.isChild) {
              // Jika ini Parent, update data terakhir
              lastUnitName = r.unitName || "(Unit Tanpa Nama)";
              lastKerusakan = r.kerusakan || "-";
          }
          // Jika Child, dia otomatis pakai lastUnitName & lastKerusakan dari parent di atasnya

          // LOGIKA NAMA BARANG & CATATAN
          if (r.itemId) {
              // Kasus 1: Ganti Sparepart (Stok)
              finalNamaBarang = r.namaBarang; // Nama barang sesuai stok
              finalCatatan = `Unit: ${lastUnitName} | Status: ${lastKerusakan} | Tindakan: ${r.solusi || 'Ganti Part'}`;
          } else {
              // Kasus 2: Jasa Service (Non Stok)
              // Nama Barang = Gabungan Tindakan + Unit (agar informatif di history)
              finalNamaBarang = (r.solusi || "Jasa Service") + ` (${lastUnitName})`;
              finalCatatan = `Status: ${lastKerusakan}`;
          }
      } else {
          // MODE JUAL
          finalNamaBarang = r.namaBarang;
          finalCatatan = r.catatan;
      }

      if(!finalNamaBarang.trim()) finalNamaBarang = "Item Tanpa Nama";

      return {
          itemId: r.itemId,
          namaBarang: finalNamaBarang,
          catatan: finalCatatan,
          hargaSatuan: r.harga,
          jumlah: r.qty
      };
  });

  if(itemsPayload.length === 0) return alert("Belum ada item transaksi!");

  // Ambil data untuk ringkasan di tabel history
  const firstRow = rows.value[0];
  const summaryBarang = form.value.tipe === 'SERVICE' ? firstRow.unitName : itemsPayload[0].namaBarang;

  const payload = {
      kasirId: 1, 
      kasirNama: localStorage.getItem('username') || 'Kasir',
      customerNama: form.value.customerNama,
      customerTelp: form.value.customerTelp,
      customerAlamat: form.value.customerAlamat,
      tipe: form.value.tipe,
      status: form.value.tipe === 'SERVICE' ? 'PROSES' : 'LUNAS',
      barangCustomer: summaryBarang,
      keluhan: firstRow.kerusakan || '-',
      dp: form.value.dp,
      items: itemsPayload
  };

  try {
      await api.post('/api/nota', payload);
      alert("Nota Berhasil Disimpan!");
      router.push('/');
  } catch(e) {
      alert("Gagal: " + (e.response?.data?.message || e.message));
  }
};
</script>