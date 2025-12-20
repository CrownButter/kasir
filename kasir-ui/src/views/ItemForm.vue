<template>
  <div class="container mx-auto p-6 max-w-4xl">
    <transition name="fade">
      <div v-if="globalError" class="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded-xl flex items-start gap-3 shadow-md">
        <i class="bi bi-exclamation-triangle-fill text-red-500 text-xl"></i>
        <div class="flex-1">
          <h4 class="text-red-800 font-bold text-sm uppercase">Gagal Menyimpan</h4>
          <p class="text-red-700 text-sm leading-relaxed">{{ globalError }}</p>
        </div>
        <button @click="globalError = ''" class="text-red-400 hover:text-red-600">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
    </transition>

    <div class="bg-white shadow-xl rounded-2xl p-8 text-black border border-gray-100">
      <div class="flex items-center gap-4 mb-8 border-b pb-6">
        <button @click="$router.push('/inventory')" class="text-gray-400 hover:text-blue-600 transition">
          <i class="bi bi-arrow-left-circle-fill text-3xl"></i>
        </button>
        <div>
          <h2 class="text-2xl font-bold text-gray-800 uppercase tracking-tight">
            {{ isEdit ? 'Update Data Barang' : 'Tambah Barang Baru' }}
          </h2>
          <p class="text-sm text-gray-500 italic">Pastikan kode barang unik dan tidak duplikat.</p>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="col-span-1">
          <label class="block text-xs font-black text-gray-500 uppercase mb-3 tracking-widest">Foto Produk</label>
          <div 
            :class="['relative group border-4 border-dashed rounded-2xl p-4 bg-gray-50 transition-all min-h-[250px] flex flex-col items-center justify-center overflow-hidden', 
                     errors.image ? 'border-red-200 bg-red-50' : 'border-gray-100 hover:bg-blue-50 hover:border-blue-200']"
          >
            <img v-if="previewUrl || form.imageUrl" 
                 :src="previewUrl || getImageUrl(form.imageUrl)" 
                 class="w-full h-48 object-contain rounded-lg mb-4 shadow-sm" />
            
            <div v-else class="text-center text-gray-400">
              <i class="bi bi-camera-fill text-6xl block mb-2"></i>
              <p class="text-[10px] font-bold">KLIK UNTUK UNGGAH FOTO</p>
            </div>
            <input type="file" @change="handleFileChange" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10" />
          </div>
          <p v-if="errors.image" class="mt-2 text-[10px] text-red-500 font-bold uppercase tracking-tight">{{ errors.image }}</p>
        </div>

        <div class="col-span-1 md:col-span-2 space-y-5">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Kode Produk</label>
              <input v-model="form.kode" 
                     :class="['w-full border-2 p-3 rounded-xl outline-none font-mono font-bold uppercase transition', 
                              errors.kode ? 'border-red-300 bg-red-50' : 'border-gray-100 focus:border-blue-500']" />
              <p v-if="errors.kode" class="text-[10px] text-red-500 font-bold mt-1 uppercase">{{ errors.kode }}</p>
            </div>
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Stok Gudang</label>
              <input v-model="form.stok" type="number" 
                     class="w-full border-2 border-gray-100 p-3 rounded-xl focus:border-blue-500 outline-none font-bold text-center" />
            </div>
          </div>

          <div>
            <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Nama Barang</label>
            <input v-model="form.nama" 
                   :class="['w-full border-2 p-3 rounded-xl outline-none font-bold transition', 
                            errors.nama ? 'border-red-300 bg-red-50' : 'border-gray-100 focus:border-blue-500']" />
            <p v-if="errors.nama" class="text-[10px] text-red-500 font-bold mt-1 uppercase">{{ errors.nama }}</p>
          </div>

          <div class="grid grid-cols-2 gap-4 pt-4 border-t border-gray-50">
            <div>
              <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Harga Modal</label>
              <div class="relative">
                <span class="absolute left-4 top-3.5 font-bold text-gray-400">Rp</span>
                <input v-model="form.hargaBeli" type="number" class="w-full border-2 border-gray-100 p-3 pl-12 rounded-xl focus:border-blue-500 outline-none font-mono font-bold" />
              </div>
            </div>
            <div>
              <label class="block text-[10px] font-black text-blue-600 uppercase mb-1">Harga Jual</label>
              <div class="relative">
                <span class="absolute left-4 top-3.5 font-bold text-blue-400">Rp</span>
                <input v-model="form.harga" type="number" class="w-full border-2 border-blue-100 p-3 pl-12 rounded-xl focus:border-blue-500 outline-none font-mono font-bold text-blue-700" />
              </div>
            </div>
          </div>

          <div class="pt-6 flex gap-4">
            <button @click="saveItem" :disabled="loading" class="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-4 rounded-2xl font-black shadow-lg flex items-center justify-center gap-2 transition active:scale-95 disabled:bg-gray-300">
              <i v-if="loading" class="bi bi-hourglass-split animate-spin text-xl"></i>
              <i v-else class="bi bi-cloud-check-fill text-xl"></i>
              {{ isEdit ? 'SIMPAN PERUBAHAN' : 'TAMBAHKAN BARANG' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api, { getImageUrl } from '../api';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const isEdit = computed(() => !!route.params.id);

const errors = ref({});
const globalError = ref("");

const form = ref({
    id: null,
    kode: "",
    nama: "",
    stok: 0,
    hargaBeli: 0,
    harga: 0,
    imageUrl: null
});

const selectedFile = ref(null);
const previewUrl = ref(null);

onMounted(async () => {
    if (isEdit.value) {
        try {
            const res = await api.get(`/api/items`);
            const item = res.data.find(i => i.id == route.params.id);
            if (item) form.value = { ...item };
        } catch (e) { 
            globalError.value = "Data tidak ditemukan di server.";
        }
    }
});

const handleFileChange = (e) => {
    errors.value.image = "";
    const file = e.target.files[0];
    if (file) {
        if (file.size > 2 * 1024 * 1024) {
            errors.value.image = "Foto terlalu besar (Maks 2MB)";
            return;
        }
        selectedFile.value = file;
        previewUrl.value = URL.createObjectURL(file);
    }
};

/**
 * LOGIKA PENERJEMAH ERROR SPESIFIK
 */
const translateError = (e) => {
    if (!e.response) return "Server tidak merespon. Periksa koneksi database/server.";

    const status = e.response.status;
    const backendMessage = e.response.data?.message || "";

    // 1. Jika Backend mengirim pesan spesifik (Contoh: "Kode barang 'BRG01' sudah ada")
    // Kita langsung gunakan pesan dari Spring Boot tersebut.
    if (backendMessage) {
        return backendMessage;
    }

    // 2. Fallback berdasarkan Status Code jika tidak ada pesan dari backend
    if (status === 409) return "Data gagal disimpan karena duplikat (Kode atau Nama sudah ada).";
    if (status === 400) return "Data yang dikirim tidak lengkap atau tidak valid.";
    if (status === 403) return "Akses ditolak. Sesi Anda mungkin berakhir.";
    
    return "Terjadi kendala saat menghubungi server.";
};

const validateForm = () => {
    errors.value = {};
    if (!form.value.kode) { 
        errors.value.kode = "Kode wajib diisi"; 
        return false; 
    }
    if (!form.value.kode) { 
      errors.value.kode = "Kode wajib diisi"; return false; }
    if (!form.value.nama) { 
      errors.value.nama = "Nama wajib diisi"; return false; 
    }
    if (form.value.stok <= 0) {
        globalError.value = "Stok tidak boleh 0 atau negatif!";
        return false;
    }
    return true;
};

const saveItem = async () => {
    if (!validateForm()) return;
    
    loading.value = true;
    globalError.value = "";
    errors.value = {}; // Reset error
    
    try {
        const formData = new FormData();
        const itemData = { ...form.value };
        delete itemData.imageUrl;

        formData.append('item', JSON.stringify(itemData));
        if (selectedFile.value) formData.append('image', selectedFile.value);

        await api.post('/api/items', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });

        router.push('/inventory');
    } catch (e) {
        // AMBIL ALASAN SPESIFIK
        globalError.value = translateError(e);

        // Jika ada detail error per field (misal: kode: "Sudah ada")
        if (e.response?.data?.errors) {
            errors.value = e.response.data.errors;
        }
    } finally {
        loading.value = false;
    }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: all 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-10px); }
</style>