<template>
  <div class="container mx-auto p-4 max-w-6xl">
    <div class="bg-white shadow-lg rounded-lg p-6 min-h-screen">
        
        <div class="flex justify-between items-start mb-6 border-b pb-4">
            <div>
                <h1 class="text-2xl font-bold text-gray-800 mb-2">Form Transaksi</h1>
                <div class="flex gap-2">
                    <button @click="form.tipe = 'SERVICE'" 
                        :class="form.tipe === 'SERVICE' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-600'" 
                        class="px-4 py-1 rounded font-bold transition">Service</button>
                    <button @click="form.tipe = 'JUAL'" 
                        :class="form.tipe === 'JUAL' ? 'bg-green-600 text-white' : 'bg-gray-200 text-gray-600'" 
                        class="px-4 py-1 rounded font-bold transition">Penjualan</button>
                </div>
            </div>
            
            <div class="grid grid-cols-1 gap-2 w-1/3">
                <input v-model="form.customerNama" placeholder="Nama Customer" class="border p-2 rounded bg-gray-50 font-bold focus:ring-2 focus:ring-blue-500 outline-none" />
                <input v-model="form.customerTelp" placeholder="No Telp / WhatsApp" class="border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" />
                <textarea v-model="form.customerAlamat" placeholder="Alamat Customer" class="border p-2 rounded h-20 focus:ring-2 focus:ring-blue-500 outline-none resize-none"></textarea>
            </div>
        </div>

        <div class="overflow-x-auto">
            <table class="w-full border-collapse border border-blue-400">
                <thead class="bg-blue-600 text-white">
                    <tr>
                        <th class="border border-blue-500 p-2 w-10 text-center">No</th>
                        <th class="border border-blue-500 p-2 text-left w-1/4">Nama Barang</th>
                        <th class="border border-blue-500 p-2 text-left w-1/5">Status / Kerusakan</th>
                        <th class="border border-blue-500 p-2 w-12 text-center">QTY</th>
                        <th class="border border-blue-500 p-2 text-left">Solusi / Tindakan</th>
                        <th class="border border-blue-500 p-2 w-32 text-right">Biaya</th>
                        <th class="border border-blue-500 p-2 w-32 text-right">Jumlah</th>
                        <th class="border border-blue-500 p-2 w-24 text-center">Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(row, index) in rows" :key="index" :class="row.isChild ? 'bg-blue-50' : 'hover:bg-gray-50'">
                        <td class="border border-blue-300 p-2 text-center text-gray-500 text-xs">
                            {{ row.isChild ? '' : getRowNumber(index) }}
                        </td>
                        
                        <td class="border border-blue-300 p-1 relative">
                            <input v-if="!row.isChild" 
                                   v-model="row.namaBarang" 
                                   class="w-full p-1 outline-none bg-transparent font-bold" 
                                   placeholder="Nama Barang..." />
                            
                            <div v-else class="flex items-center text-gray-400 px-2 italic select-none">
                                <span class="mr-2 text-2xl leading-none">↳</span>
                                <span class="text-xs">(Mengikuti atas)</span>
                            </div>
                        </td>
                        
                        <td class="border border-blue-300 p-1">
                            <input v-model="row.kerusakan" class="w-full p-1 outline-none bg-transparent text-sm" placeholder="-" />
                        </td>
                        
                        <td class="border border-blue-300 p-1">
                            <input type="number" v-model="row.qty" class="w-full p-1 text-center outline-none bg-transparent" min="1" />
                        </td>
                        
                        <td class="border border-blue-300 p-1">
                            <input v-model="row.solusi" class="w-full p-1 outline-none bg-transparent font-medium text-blue-800" placeholder="Ketik Solusi..." />
                        </td>
                        
                        <td class="border border-blue-300 p-1">
                            <input type="number" v-model="row.harga" class="w-full p-1 text-right outline-none bg-transparent" placeholder="0"/>
                        </td>
                        
                        <td class="border border-blue-300 p-2 text-right font-bold text-gray-700">
                            {{ formatNumber(row.qty * row.harga) }}
                        </td>
                        
                        <td class="border border-blue-300 p-1 text-center">
                            <div class="flex justify-center gap-1">
                                <button v-if="!row.isChild" @click="addSolution(index)" title="Tambah Solusi Lain"
                                        class="bg-green-100 text-green-700 hover:bg-green-200 px-2 rounded text-xs font-bold border border-green-300">
                                    + Solusi
                                </button>
                                <button @click="removeRow(index)" title="Hapus Baris"
                                        class="bg-red-100 text-red-600 hover:bg-red-200 px-2 rounded font-bold border border-red-300">
                                    ×
                                </button>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="flex justify-between items-center mt-4 pt-4 border-t">
            <button @click="addRow" class="bg-gray-100 text-gray-700 px-4 py-2 rounded font-bold hover:bg-gray-200 border">
                + Tambah Barang Baru
            </button>
            
            <div class="flex items-center gap-6">
                <div class="text-right">
                    <div class="text-gray-500 text-sm">Total Tagihan</div>
                    <div class="text-2xl font-bold text-blue-800">Rp {{ formatNumber(grandTotal) }}</div>
                </div>
                
                <div class="flex gap-2">
                    <button @click="$router.push('/')" class="px-6 py-3 rounded border hover:bg-gray-100 font-bold">Batal</button>
                    <button @click="saveNota" class="px-8 py-3 rounded bg-blue-600 text-white font-bold hover:bg-blue-700 shadow-lg">
                        SIMPAN NOTA
                    </button>
                </div>
            </div>
        </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import api from '../api';
import { useRouter } from 'vue-router';

const router = useRouter();

const form = ref({
    customerNama: '',
    customerTelp: '',
    customerAlamat: '', // Field Baru
    tipe: 'SERVICE'
});

const rows = ref([
    { namaBarang: '', kerusakan: '', qty: 1, solusi: '', harga: 0, isChild: false }
]);

const getRowNumber = (idx) => {
    let count = 0;
    for(let i=0; i<=idx; i++) {
        if(!rows.value[i].isChild) count++;
    }
    return count;
};

const addRow = () => {
    rows.value.push({ namaBarang: '', kerusakan: '', qty: 1, solusi: '', harga: 0, isChild: false });
};

const addSolution = (index) => {
    const parentRow = rows.value[index];
    rows.value.splice(index + 1, 0, {
        namaBarang: '',
        kerusakan: parentRow.kerusakan,
        qty: 1,
        solusi: '',
        harga: 0,
        isChild: true
    });
};

const removeRow = (idx) => {
    if (rows.value[idx].isChild === false) {
        if (rows.value[idx + 1] && rows.value[idx + 1].isChild) {
             rows.value[idx + 1].isChild = false;
             rows.value[idx + 1].namaBarang = rows.value[idx].namaBarang;
        }
    }
    if(rows.value.length > 1) rows.value.splice(idx, 1); 
    else rows.value[0] = { namaBarang: '', kerusakan: '', qty: 1, solusi: '', harga: 0, isChild: false };
};

const grandTotal = computed(() => rows.value.reduce((sum, r) => sum + (r.qty * r.harga), 0));
const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const saveNota = async () => {
    if(!form.value.customerNama) return alert("Nama Customer wajib diisi!");

    let lastParentName = "";
    
    // Logic: Jika ini anak, ambil nama dari bapaknya (lastParentName)
    const itemsPayload = rows.value.map(r => {
        if (!r.isChild) {
            lastParentName = r.namaBarang;
            return r;
        } else {
            return { ...r, namaBarang: lastParentName };
        }
    })
    .filter(r => r.solusi.trim() !== '' || r.namaBarang.trim() !== '')
    .map(r => ({
        namaBarang: r.namaBarang,
        hargaSatuan: r.harga,
        jumlah: r.qty,
        catatan: `Status: ${r.kerusakan || '-'} | Solusi: ${r.solusi || '-'}`,
        itemId: null
    }));

    if(itemsPayload.length === 0) return alert("Isi minimal 1 solusi/tindakan!");

    const payload = {
        kasirId: 1,
        kasirNama: localStorage.getItem('username'),
        customerNama: form.value.customerNama,
        customerTelp: form.value.customerTelp,
        customerAlamat: form.value.customerAlamat, // Kirim Alamat
        tipe: form.value.tipe,
        status: 'PROSES',
        // Otomatis ambil nama barang dari baris pertama tabel untuk judul Service
        barangCustomer: itemsPayload[0].namaBarang, 
        keluhan: 'Lihat rincian item',
        items: itemsPayload
    };

    try {
        await api.post('/api/nota', payload);
        alert("Nota Berhasil Disimpan!");
        router.push('/');
    } catch(e) {
        alert("Gagal: " + e.message);
    }
};
</script>