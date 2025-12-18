<template>
  <div class="min-h-screen bg-gray-100 flex flex-col md:flex-row">
    <div class="w-full md:w-64 bg-gray-900 text-white flex flex-col shadow-xl flex-shrink-0">
       <div class="p-4 md:p-6 bg-gray-800 flex justify-between items-center md:block">
          <div>
            <h2 class="text-xl md:text-2xl font-bold tracking-wider flex items-center gap-2">
               <i class="bi bi-hdd-rack-fill"></i> Panel
            </h2>
            <p class="text-gray-400 text-xs mt-1 hidden md:block uppercase">Admin System</p>
          </div>
       </div>

       <nav class="flex md:flex-col overflow-x-auto md:overflow-visible p-2 md:p-4 space-x-2 md:space-x-0 md:space-y-2 bg-gray-900 font-bold uppercase text-xs">
          <button @click="activeTab = 'TOKO'" :class="activeTab === 'TOKO' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-shop h-5 w-5"></i><span>Toko & Bank</span>
          </button>
          <button @click="activeTab = 'KASIR'" :class="activeTab === 'KASIR' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-people-fill h-5 w-5"></i><span>Management Kasir</span>
          </button>
          <button @click="activeTab = 'LAPORAN'" :class="activeTab === 'LAPORAN' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-file-earmark-bar-graph-fill h-5 w-5"></i><span>Laporan Transaksi</span>
          </button>
       </nav>

       <div class="p-4 border-t border-gray-700 mt-auto">
          <button @click="$router.push('/')" class="w-full bg-red-600 py-2 rounded font-bold hover:bg-red-700 transition flex justify-center items-center gap-2 text-sm">
             <i class="bi bi-arrow-left-circle"></i> Back to POS
          </button>
       </div>
    </div>

    <div class="flex-1 p-4 md:p-10 overflow-y-auto w-full">
       
       <div v-if="activeTab === 'TOKO'" class="max-w-4xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
          <h3 class="text-xl md:text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2 uppercase tracking-wider">
             <i class="bi bi-shop-window"></i> Pengaturan Toko
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
             <div class="col-span-1 text-black">
                <label class="block font-bold text-gray-700 mb-2 uppercase text-xs">Logo Toko</label>
                <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex flex-col items-center justify-center bg-gray-50 hover:bg-gray-100 transition relative min-h-[160px] overflow-hidden">
                   <img v-if="previewUrl || toko.logoUrl" :src="previewUrl || getImageUrl(toko.logoUrl)" class="h-32 object-contain mb-4"/>
                   <div v-else class="flex flex-col items-center">
                      <i class="bi bi-cloud-arrow-up text-4xl text-gray-400 mb-2"></i>
                   </div>
                   <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"/>
                   <p class="text-sm text-gray-500 font-bold uppercase text-[10px]">Klik untuk ganti logo</p>
                </div>
             </div>

             <div class="col-span-1 space-y-4">
                <div>
                   <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">Nama Toko</label>
                   <input v-model="toko.namaToko" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold text-black"/>
                </div>
                <div>
                   <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">No Telp / WhatsApp</label>
                   <input v-model="toko.noTelp" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold text-black"/>
                </div>
             </div>

             <div class="col-span-1 md:col-span-2 text-black">
                <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">Alamat Lengkap</label>
                <textarea v-model="toko.alamatToko" class="w-full border p-2 rounded h-24 focus:ring-2 focus:ring-blue-500 outline-none resize-none font-bold"></textarea>
             </div>
          </div>
          
          <div class="mt-10 border-t pt-6 text-black">
             <div class="flex justify-between items-center mb-4">
                <h4 class="font-bold text-gray-700 uppercase flex items-center gap-2 tracking-widest">
                   <i class="bi bi-credit-card"></i> Rekening Pembayaran
                </h4>
                <button @click="addRekening" class="bg-green-600 text-white px-4 py-1 rounded text-xs font-bold hover:bg-green-700 shadow transition">
                   + Tambah Rekening
                </button>
             </div>
             <div class="space-y-3">
                <div v-for="(bank, index) in toko.daftarRekening" :key="index" class="flex flex-wrap md:flex-nowrap gap-2 bg-gray-50 p-4 rounded-xl border border-gray-200 relative shadow-sm">
                   <div class="w-full md:w-1/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Nama Bank</label>
                       <input v-model="bank.namaBank" placeholder="BCA / BRI / Mandiri" class="w-full border p-2 rounded text-xs font-bold"/>
                   </div>
                   <div class="w-full md:w-2/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Nomor Rekening</label>
                       <input v-model="bank.noRekening" placeholder="XXXX-XXXX-XXXX" class="w-full border p-2 rounded text-xs font-bold font-mono"/>
                   </div>
                   <div class="w-full md:w-1/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Atas Nama</label>
                       <input v-model="bank.atasNama" placeholder="Nama Pemilik" class="w-full border p-2 rounded text-xs font-bold"/>
                   </div>
                   <button @click="removeRekening(index)" class="text-red-500 hover:text-red-700 self-end p-2 mb-0.5">
                      <i class="bi bi-trash-fill"></i>
                   </button>
                </div>
             </div>
          </div>

          <div class="flex justify-end mt-10">
             <button @click="saveToko" class="bg-blue-600 text-white px-10 py-3 rounded-xl font-bold hover:bg-blue-700 shadow-xl flex items-center gap-2 transition transform active:scale-95 uppercase tracking-widest">
                <i class="bi bi-save2"></i> Simpan Pengaturan
             </button>
          </div>
       </div>

       </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { getImageUrl } from '../api'; // Import helper getImageUrl

const activeTab = ref('TOKO');
const toko = ref({ namaToko: "", alamatToko: "", noTelp: "", logoUrl: null, daftarRekening: [] });

// Logic File Upload
const selectedFile = ref(null);
const previewUrl = ref(null);

onMounted(() => { loadToko(); });

const loadToko = async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
    if (!toko.value.daftarRekening) toko.value.daftarRekening = [];
  } catch(e) { console.error("Gagal load toko"); }
};

const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if(file) {
    if(file.size > 2 * 1024 * 1024) return alert("Maksimal file 2MB!");
    selectedFile.value = file;
    // Buat URL blob untuk preview instan tanpa Base64
    previewUrl.value = URL.createObjectURL(file);
  }
};

const saveToko = async () => {
  try {
    const formData = new FormData();
    
    // 1. Tambahkan bagian JSON (RequestPart 'toko')
    const storeData = {
      id: toko.value.id,
      namaToko: toko.value.namaToko,
      alamatToko: toko.value.alamatToko,
      noTelp: toko.value.noTelp,
      daftarRekening: toko.value.daftarRekening
    };
    formData.append('toko', JSON.stringify(storeData));

    // 2. Tambahkan file logo jika ada perubahan (RequestPart 'logo')
    if (selectedFile.value) {
      formData.append('logo', selectedFile.value);
    }

    // 3. Kirim via Multipart Form Data
    await api.post('/api/admin/toko', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    alert("Berhasil disimpan!");
    window.location.reload(); 
  } catch(e) { 
    alert("Gagal: " + (e.response?.data?.message || e.message)); 
  }
};

const addRekening = () => { toko.value.daftarRekening.push({ namaBank: "", noRekening: "", atasNama: "" }); };
const removeRekening = (index) => { toko.value.daftarRekening.splice(index, 1); };
</script>