<template>
  <div class="container mx-auto p-2 md:p-4 max-w-[98%] pb-40"> <div class="bg-white shadow-lg rounded-lg p-4 md:p-6 min-h-screen">
      
      <div class="flex flex-col md:flex-row justify-between items-start mb-6 border-b pb-4 gap-4">
        
        <div class="w-full md:w-auto">
           <h1 class="text-xl md:text-2xl font-bold text-gray-800 mb-3 flex items-center gap-2">
              <i class="bi bi-file-earmark-text"></i> Form Transaksi
           </h1>
           <div class="grid grid-cols-2 md:flex gap-2">
              <button @click="switchMode('SERVICE')" 
                :class="form.tipe === 'SERVICE' ? 'bg-orange-600 text-white shadow-lg md:scale-105' : 'bg-gray-200 text-gray-600'" 
                class="px-4 py-2 rounded-lg md:rounded-full font-bold transition transform duration-200 flex justify-center items-center gap-2 text-xs md:text-base">
                  <i class="bi bi-tools"></i> Service
              </button>
              <button @click="switchMode('JUAL')" 
                :class="form.tipe === 'JUAL' ? 'bg-blue-600 text-white shadow-lg md:scale-105' : 'bg-gray-200 text-gray-600'" 
                class="px-4 py-2 rounded-lg md:rounded-full font-bold transition transform duration-200 flex justify-center items-center gap-2 text-xs md:text-base">
                  <i class="bi bi-cart4"></i> Retail
              </button>
           </div>
        </div>

        <div class="grid grid-cols-1 gap-2 w-full md:w-1/3 bg-gray-50 p-3 rounded border border-gray-200">
           <div class="relative">
              <i class="bi bi-person absolute top-2.5 left-3 text-gray-400"></i>
              <input v-model="form.customerNama" placeholder="Nama Customer *" 
                class="w-full pl-9 pr-3 py-2 border rounded bg-white font-bold outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
           </div>
           <div class="relative">
              <i class="bi bi-whatsapp absolute top-2.5 left-3 text-gray-400"></i>
              <input v-model="form.customerTelp" type="tel" placeholder="No Telp / WA" 
                class="w-full pl-9 pr-3 py-2 border rounded bg-white outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
           </div>
           <textarea v-model="form.customerAlamat" placeholder="Alamat Lengkap" 
             class="w-full p-2 border rounded bg-white h-16 resize-none outline-none focus:ring-2 focus:ring-blue-500 text-sm"></textarea>
        </div>
      </div>

      <div class="overflow-x-auto rounded border border-gray-200 mb-4">
        <table class="w-full border-collapse min-w-[900px]">
           
           <thead :class="form.tipe === 'SERVICE' ? 'bg-orange-600' : 'bg-blue-600'" class="text-white text-sm transition-colors duration-300">
             <tr>
               <th class="p-3 w-10 text-center border-r border-white/20">No</th>
               
               <template v-if="form.tipe === 'SERVICE'">
                   <th class="p-3 text-left w-[20%] border-r border-white/20">Unit / Barang</th>
                   <th class="p-3 text-left w-[15%] border-r border-white/20">Kerusakan</th>
                   <th class="p-3 text-left w-[15%] border-r border-white/20">Solusi / Tindakan</th>
                   <th class="p-3 text-left w-[20%] bg-orange-700 border-r border-white/20">Ganti Sparepart (Stok)</th>
               </template>

               <template v-else>
                   <th class="p-3 text-left w-[40%] border-r border-white/20">Nama Barang (Stok)</th>
                   <th class="p-3 text-left w-[30%] border-r border-white/20">Catatan</th>
               </template>

               <th class="p-3 w-16 text-center border-r border-white/20">Qty</th>
               <th class="p-3 w-32 text-right border-r border-white/20">Harga</th>
               <th class="p-3 w-32 text-right border-r border-white/20">Total</th>
               <th class="p-3 w-16 text-center">Aksi</th>
             </tr>
           </thead>

           <tbody class="text-sm text-gray-700">
             <tr v-for="(row, index) in rows" :key="index" class="hover:bg-gray-50 border-b last:border-0 relative group">
               
               <td class="border-r p-2 text-center bg-gray-50 text-xs font-bold text-gray-500">
                   {{ row.isChild ? '' : getRowNumber(index) }}
               </td>
               
               <template v-if="form.tipe === 'SERVICE'">
                   <td class="border-r p-1 align-top">
                       <input v-if="!row.isChild" v-model="row.unitName" class="w-full p-2 rounded outline-none focus:bg-blue-50 font-bold placeholder-gray-300" placeholder="Contoh: Laptop Acer..."/>
                       <div v-else class="flex items-center h-full pl-4 opacity-50 select-none">
                           <span class="text-2xl leading-none">↳</span>
                           <span class="text-[10px] italic ml-1">(Item Tambahan)</span>
                       </div>
                   </td>
                   
                   <td class="border-r p-1 align-top">
                       <textarea v-if="!row.isChild" v-model="row.kerusakan" rows="1" class="w-full p-2 rounded outline-none focus:bg-blue-50 resize-none placeholder-gray-300" placeholder="Mati total..."></textarea>
                       <div v-else class="text-center text-gray-300 select-none pt-2">"</div>
                   </td>

                   <td class="border-r p-1 align-top">
                       <textarea v-model="row.solusi" rows="1" class="w-full p-2 rounded outline-none focus:bg-blue-50 text-blue-700 font-medium resize-none placeholder-gray-300" placeholder="Install Ulang..."></textarea>
                   </td>

                   <td class="border-r p-1 bg-yellow-50 align-top relative">
                       <ItemSearchInput 
                            v-model="row.namaBarang"
                            :items="masterItems"
                            @select="(item) => onItemSelect(row, item)"
                       />
                       <div v-if="row.itemId" class="absolute right-1 top-1 text-[9px] bg-green-100 text-green-700 px-1 rounded border border-green-200 pointer-events-none font-bold">STOK</div>
                   </td>
               </template>

               <template v-else>
                   <td class="border-r p-1 align-top relative">
                       <ItemSearchInput v-model="row.namaBarang" :items="masterItems" @select="(item) => onItemSelect(row, item)" />
                       <div v-if="row.itemId" class="absolute right-2 top-2 text-[10px] bg-green-100 text-green-700 px-1 rounded border border-green-200 font-bold">STOK</div>
                   </td>
                   <td class="border-r p-1 align-top">
                       <input v-model="row.catatan" class="w-full p-2 rounded outline-none focus:bg-blue-50 placeholder-gray-300" placeholder="Keterangan (SN/Warna)..."/>
                   </td>
               </template>

               <td class="border-r p-1 align-top">
                   <input type="number" v-model="row.qty" class="w-full p-2 text-center rounded outline-none focus:bg-blue-50 font-bold" min="1" />
               </td>
               <td class="border-r p-1 align-top">
                   <input type="number" v-model="row.harga" class="w-full p-2 text-right rounded outline-none focus:bg-yellow-100 font-mono" placeholder="0"/>
               </td>
               <td class="border-r p-2 text-right font-bold text-gray-700 font-mono bg-gray-50 align-top">
                   {{ formatNumber(row.qty * row.harga) }}
               </td>
               
               <td class="p-1 text-center align-top pt-2">
                   <div class="flex items-center justify-center gap-1">
                       <button v-if="form.tipe === 'SERVICE' && !row.isChild" @click="addSolution(index)" class="w-7 h-7 bg-green-100 hover:bg-green-200 text-green-700 rounded shadow-sm flex items-center justify-center transition" title="Tambah Sparepart/Tindakan Lain">
                           <i class="bi bi-plus-lg text-xs font-bold"></i>
                       </button>

                       <button @click="removeRow(index)" class="w-7 h-7 bg-red-50 hover:bg-red-100 text-red-500 rounded shadow-sm flex items-center justify-center transition">
                           <i class="bi bi-trash-fill text-xs"></i>
                       </button>
                   </div>
               </td>
             </tr>
           </tbody>
        </table>
      </div>
      
      <button @click="addNewUnit" class="w-full md:w-auto bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-bold hover:bg-gray-200 border border-gray-300 flex items-center justify-center gap-2 transition shadow-sm">
         <i class="bi bi-plus-circle-fill text-blue-600"></i> 
         {{ form.tipe === 'SERVICE' ? 'Tambah Unit / Perangkat Baru' : 'Tambah Baris Baru' }}
      </button>

      <div class="fixed bottom-0 left-0 w-full bg-white border-t p-3 md:p-4 shadow-[0_-4px_10px_rgba(0,0,0,0.1)] z-50">
          <div class="container mx-auto max-w-[98%] flex flex-col md:flex-row justify-end items-center gap-3 md:gap-8">
              
              <div class="w-full md:w-auto flex flex-row justify-between md:justify-end items-center gap-4">
                  <div class="flex items-center gap-2 bg-gray-50 px-3 py-1 rounded border">
                      <span class="text-xs md:text-sm font-bold text-gray-600">DP:</span>
                      <input v-model="form.dp" type="number" class="w-20 md:w-24 text-right font-bold outline-none bg-transparent border-b border-gray-300 focus:border-blue-500 text-sm md:text-base"/>
                  </div>

                  <div class="text-right">
                      <div class="text-[10px] md:text-xs text-gray-500 font-bold uppercase">Sisa Bayar</div>
                      <div class="text-lg md:text-2xl font-bold text-red-600 font-mono">
                          Rp {{ formatNumber(grandTotal - form.dp) }}
                      </div>
                  </div>
              </div>

              <button @click="saveNota" 
                :class="form.tipe === 'SERVICE' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-blue-600 hover:bg-blue-700'" 
                class="w-full md:w-auto text-white px-8 py-3 rounded-lg font-bold shadow-lg transform active:scale-95 transition flex justify-center items-center gap-2">
                  <i class="bi bi-save2-fill"></i> SIMPAN
              </button>
          </div>
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

// Row Template
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
        unitName: "", 
        kerusakan: "", 
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
              lastUnitName = r.unitName || "(Unit Tanpa Nama)";
              lastKerusakan = r.kerusakan || "-";
          }

          if (r.itemId) {
              // Kasus 1: Ganti Sparepart
              finalNamaBarang = r.namaBarang; 
              finalCatatan = `Unit: ${lastUnitName} | Status: ${lastKerusakan} | Tindakan: ${r.solusi || 'Ganti Part'}`;
          } else {
              // Kasus 2: Jasa Service
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