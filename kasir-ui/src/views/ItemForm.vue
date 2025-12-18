<template>
  <div class="container mx-auto p-6 max-w-4xl">
    <div class="bg-white shadow-xl rounded-2xl p-8 text-black">
      
      <div class="flex items-center gap-4 mb-8 border-b pb-6">
        <button @click="$router.push('/inventory')" class="text-gray-400 hover:text-blue-600 transition">
            <i class="bi bi-arrow-left-circle-fill text-3xl"></i>
        </button>
        <div>
            <h2 class="text-2xl font-bold text-gray-800 uppercase tracking-tight">
                {{ isEdit ? 'Update Data Barang' : 'Tambah Barang Baru' }}
            </h2>
            <p class="text-sm text-gray-500 italic">Pastikan data harga dan stok sudah benar.</p>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="col-span-1">
            <label class="block text-xs font-black text-gray-500 uppercase mb-3 tracking-widest">Foto Produk</label>
            <div class="relative group border-4 border-dashed border-gray-100 rounded-2xl p-4 bg-gray-50 hover:bg-blue-50 hover:border-blue-200 transition-all min-h-[250px] flex flex-col items-center justify-center overflow-hidden">
                
                <img v-if="previewUrl || form.imageUrl" 
                     :src="previewUrl || getImageUrl(form.imageUrl)" 
                     class="w-full h-48 object-contain rounded-lg mb-4 shadow-sm" />
                
                <div v-else class="text-center text-gray-400">
                    <i class="bi bi-camera-fill text-6xl block mb-2"></i>
                    <p class="text-[10px] font-bold">KLIK UNTUK UNGGAH FOTO</p>
                </div>

                <input type="file" @change="handleFileChange" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10" />
            </div>
            <p class="mt-3 text-[10px] text-gray-400 text-center uppercase font-bold tracking-tighter">Format: JPG, PNG (Max. 2MB)</p>
        </div>

        <div class="col-span-1 md:col-span-2 space-y-5">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Kode Produk</label>
                    <input v-model="form.kode" class="w-full border-2 border-gray-100 p-3 rounded-xl focus:border-blue-500 outline-none font-mono font-bold uppercase" placeholder="BRG-001" />
                </div>
                <div>
                    <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Stok Gudang</label>
                    <input v-model="form.stok" type="number" class="w-full border-2 border-gray-100 p-3 rounded-xl focus:border-blue-500 outline-none font-bold text-center" />
                </div>
            </div>

            <div>
                <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Nama Barang / Sparepart</label>
                <input v-model="form.nama" class="w-full border-2 border-gray-100 p-3 rounded-xl focus:border-blue-500 outline-none font-bold" placeholder="Contoh: RAM DDR4 8GB" />
            </div>

            <div class="grid grid-cols-2 gap-4 pt-4 border-t border-gray-50">
                <div>
                    <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Harga Modal (Beli)</label>
                    <div class="relative">
                        <span class="absolute left-4 top-3.5 font-bold text-gray-400">Rp</span>
                        <input v-model="form.hargaBeli" type="number" class="w-full border-2 border-gray-100 p-3 pl-12 rounded-xl focus:border-blue-500 outline-none font-mono font-bold" />
                    </div>
                </div>
                <div>
                    <label class="block text-[10px] font-black text-blue-600 uppercase mb-1">Harga Jual (Retail)</label>
                    <div class="relative">
                        <span class="absolute left-4 top-3.5 font-bold text-blue-400">Rp</span>
                        <input v-model="form.harga" type="number" class="w-full border-2 border-blue-100 p-3 pl-12 rounded-xl focus:border-blue-500 outline-none font-mono font-bold text-blue-700" />
                    </div>
                </div>
            </div>

            <div class="pt-6 flex gap-4">
                <button @click="saveItem" :disabled="loading" class="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-4 rounded-2xl font-black shadow-lg shadow-blue-100 flex items-center justify-center gap-2 transition transform active:scale-95 disabled:bg-gray-300">
                    <i v-if="loading" class="bi bi-hourglass-split animate-spin"></i>
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
        } catch (e) { alert("Gagal mengambil data barang"); }
    }
});

const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
        if (file.size > 2 * 1024 * 1024) return alert("File terlalu besar (Maks 2MB)");
        selectedFile.value = file;
        previewUrl.value = URL.createObjectURL(file); // Preview sementara
    }
};

const saveItem = async () => {
    if (!form.value.kode || !form.value.nama) return alert("Kode dan Nama wajib diisi!");
    
    loading.value = true;
    try {
        const formData = new FormData();
        
        // Buat objek data tanpa imageUrl lama agar tidak konflik
        const itemData = { ...form.value };
        delete itemData.imageUrl;

        formData.append('item', JSON.stringify(itemData));
        
        if (selectedFile.value) {
            formData.append('image', selectedFile.value);
        }

        await api.post('/api/items', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });

        alert("Berhasil disimpan!");
        router.push('/inventory');
    } catch (e) {
        alert("Gagal menyimpan: " + (e.response?.data?.message || e.message));
    } finally {
        loading.value = false;
    }
};
</script>