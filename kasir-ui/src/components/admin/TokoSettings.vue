<template>
  <div class="max-w-4xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
      <h3 class="text-xl md:text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2 uppercase tracking-wider">
         <i class="bi bi-shop-window"></i> Pengaturan Toko
      </h3>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
         <div class="col-span-1 text-black">
            <label class="block font-bold text-gray-700 mb-2 uppercase text-xs">Logo Toko</label>
            <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex flex-col items-center justify-center relative min-h-[160px]">
                  <img v-if="previewUrl || toko.logoUrl" :src="previewUrl || getImageUrl(toko.logoUrl)" class="h-32 object-contain mb-4"/>
                  <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"/>
             </div>
         </div>
         
         <div class="col-span-1 space-y-4">
             <div>
                <label class="block font-bold text-[10px] text-gray-500 uppercase">Nama Toko</label>
                <input v-model="toko.namaToko" class="w-full border p-2 rounded text-black"/>
             </div>
             </div>
      </div>
      
      <div class="flex justify-end mt-10">
         <button @click="saveToko" class="bg-blue-600 text-white px-10 py-3 rounded-xl font-bold hover:bg-blue-700 shadow-xl">Simpan</button>
      </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api, { getImageUrl } from '../../api';

const toko = ref({ namaToko: "", alamatToko: "", noTelp: "", logoUrl: null, daftarRekening: [] });
const selectedFile = ref(null);
const previewUrl = ref(null);

onMounted(() => loadToko());

const loadToko = async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
    if (!toko.value.daftarRekening) toko.value.daftarRekening = [];
  } catch(e) {}
};

const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if(file) {
    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
};

const saveToko = async () => {
  try {
    const formData = new FormData();
    const storeData = { ...toko.value };
    delete storeData.logoUrl;
    formData.append('toko', JSON.stringify(storeData));
    if (selectedFile.value) formData.append('logo', selectedFile.value);

    await api.post('/api/admin/toko', formData, { headers: { 'Content-Type': 'multipart/form-data' }});
    alert("Berhasil disimpan!");
    window.location.reload();
  } catch(e) { alert("Gagal Simpan"); }
};
</script>