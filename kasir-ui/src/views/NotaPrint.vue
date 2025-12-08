<template>
  <div v-if="nota" id="printable-area" class="bg-white p-8 w-full max-w-4xl mx-auto shadow-2xl rounded-lg relative font-sans text-sm text-black">
    
    <div class="text-center border-b-2 border-black pb-4 mb-6 flex flex-col items-center">
        <img v-if="toko.logoBase64" :src="toko.logoBase64" class="h-24 object-contain mb-2"/>
        
        <h1 class="text-3xl font-extrabold tracking-widest uppercase">{{ toko.namaToko || 'DWIN COMPUTER' }}</h1>
        <p class="text-gray-600 mt-1 whitespace-pre-line">{{ toko.alamatToko || 'Alamat Toko' }}</p>
        <p class="text-gray-600 font-bold">Telp/WA: {{ toko.noTelp || '-' }}</p>
    </div>

    <div class="flex justify-between mb-6">
        <div class="space-y-1">
            <div class="flex"><span class="w-24 font-bold">No. Nota</span> <span>: {{ nota.kodeNota || nota.id }}</span></div>
            <div class="flex"><span class="w-24 font-bold">Tanggal</span> <span>: {{ formatDate(nota.tanggal) }}</span></div>
            <div class="flex"><span class="w-24 font-bold">Kasir</span> <span>: {{ nota.kasirNama }}</span></div>
        </div>
        <div class="space-y-1">
            <div class="flex"><span class="w-24 font-bold">Customer</span> <span>: {{ nota.customerNama }}</span></div>
            <div class="flex"><span class="w-24 font-bold">Telp</span> <span>: {{ nota.customerTelp }}</span></div>
            <div class="flex"><span class="w-24 font-bold">Unit</span> <span>: {{ nota.barangCustomer || '-' }}</span></div>
        </div>
    </div>

    <table class="w-full border-collapse border border-black mb-8">
        <thead class="bg-gray-100">
            <tr>
                <th class="border border-black p-2 text-center w-10">No</th>
                <th class="border border-black p-2 text-left">Deskripsi</th>
                <th class="border border-black p-2 text-left">Detail/Solusi</th>
                <th class="border border-black p-2 text-center w-16">Qty</th>
                <th class="border border-black p-2 text-right w-32">Harga</th>
                <th class="border border-black p-2 text-right w-32">Total</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(item, i) in (nota.items || nota.snapshots)" :key="i">
                <td class="border border-black p-2 text-center">{{ i + 1 }}</td>
                <td class="border border-black p-2 font-bold">{{ item.namaBarang }}</td>
                <td class="border border-black p-2 text-gray-700 text-xs">{{ item.catatan }}</td>
                <td class="border border-black p-2 text-center">{{ item.jumlah }}</td>
                <td class="border border-black p-2 text-right">{{ formatNumber(item.hargaSatuan) }}</td>
                <td class="border border-black p-2 text-right font-bold">{{ formatNumber(item.totalHarga || (item.hargaSatuan * item.jumlah)) }}</td>
            </tr>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="5" class="border border-black p-2 text-right font-bold text-lg">TOTAL TAGIHAN</td>
                <td class="border border-black p-2 text-right font-bold text-lg bg-gray-100">Rp {{ formatNumber(nota.total) }}</td>
            </tr>
        </tfoot>
    </table>

    <div class="flex justify-between mt-12 px-10 mb-8">
        <div class="text-center">
            <p class="mb-16">Penerima</p>
            <p class="font-bold border-b border-black pb-1 px-4">({{ nota.customerNama }})</p>
        </div>
        <div class="text-center">
            <p class="mb-16">Hormat Kami,</p>
            <p class="font-bold border-b border-black pb-1 px-4">({{ toko.namaToko || 'Admin' }})</p>
        </div>
    </div>

    <div class="flex justify-end gap-4 border-t pt-6 no-print">
        <button @click="$emit('close')" 
                class="bg-gray-500 text-white px-6 py-2 rounded font-bold hover:bg-gray-600 flex items-center gap-2">
            <i class="bi bi-x-circle"></i> Tutup
        </button>
        <button @click="printNow" 
                class="bg-blue-600 text-white px-8 py-2 rounded font-bold hover:bg-blue-700 flex items-center gap-2 shadow-lg">
            <i class="bi bi-printer-fill"></i> CETAK / PDF
        </button>
    </div>

  </div>
  
  <div v-else class="text-center p-10 font-bold text-white text-xl flex flex-col items-center justify-center h-full">
      <i class="bi bi-arrow-clockwise animate-spin text-4xl mb-2"></i>
      <span>Sedang memuat data nota...</span>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const props = defineProps(['notaId']);
const emit = defineEmits(['close']);
const nota = ref(null);
const toko = ref({}); 

onMounted(async () => {
    try {
        // 1. Ambil Setting Toko
        const resToko = await api.get('/api/admin/toko');
        toko.value = resToko.data;

        // 2. Ambil Nota
        if(!props.notaId) return;
        const res = await api.get(`/api/nota/${props.notaId}`);
        nota.value = res.data;
    } catch(e) {
        alert("Gagal memuat data!");
        emit('close');
    }
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);
const formatDate = (d) => {
    if(!d) return '-';
    return new Date(d).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });
};
const printNow = () => window.print();
</script>