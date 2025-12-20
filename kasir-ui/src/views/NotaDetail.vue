<template>
  <div class="container mx-auto p-4 md:p-6 max-w-4xl" v-if="nota">
    <div class="flex justify-between items-center mb-6">
      <button @click="$router.push('/')" class="text-gray-600 flex items-center gap-2 font-bold hover:text-black">
        <i class="bi bi-arrow-left"></i> Kembali ke POS
      </button>
      <div class="flex gap-3">
        <button @click="printPdf" class="bg-gray-800 text-white px-4 py-2 rounded shadow hover:bg-black font-bold flex items-center gap-2">
          <i class="bi bi-printer-fill"></i> Cetak PDF
        </button>
        <button @click="$router.push('/edit/' + nota.id)" class="bg-yellow-500 text-white px-4 py-2 rounded shadow hover:bg-yellow-600 font-bold flex items-center gap-2">
          <i class="bi bi-pencil-square"></i> Edit Nota
        </button>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-lg overflow-hidden border">
      <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
        <div>
          <h2 class="text-2xl font-bold text-gray-800">Detail Nota</h2>
          <p class="font-mono text-blue-600 font-bold text-lg">{{ nota.kodeNota }}</p>
        </div>
        <div :class="getStatusColor(nota.status)" class="px-4 py-2 rounded-full font-black uppercase text-sm border shadow-sm">
          {{ nota.status }}
        </div>
      </div>

      <div class="p-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
          <div>
            <h4 class="text-xs font-black text-gray-400 uppercase tracking-widest mb-2">Pelanggan</h4>
            <p class="text-xl font-bold text-gray-800">{{ nota.customerNama }}</p>
            <p class="text-gray-600">{{ nota.customerTelp || '-' }}</p>
            <p class="text-gray-600 italic mt-1">{{ nota.customerAlamat || '-' }}</p>
          </div>
          <div class="md:text-right">
            <h4 class="text-xs font-black text-gray-400 uppercase tracking-widest mb-2">Informasi Nota</h4>
            <p class="text-gray-800 font-bold">Tanggal: {{ formatDate(nota.tanggal) }}</p>
            <p class="text-gray-800">Kasir: {{ nota.kasirNama }}</p>
            <p class="text-blue-700 font-bold mt-2" v-if="nota.barangCustomer">Unit: {{ nota.barangCustomer }}</p>
          </div>
        </div>

        <div class="border rounded-xl overflow-hidden mb-8">
          <table class="w-full text-sm text-left">
            <thead class="bg-gray-800 text-white uppercase text-xs">
              <tr>
                <th class="p-4">Barang/Jasa</th>
                <th class="p-4 text-center">Qty</th>
                <th class="p-4 text-right">Harga</th>
                <th class="p-4 text-right">Subtotal</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              <tr v-for="(item, i) in nota.snapshots" :key="i" class="hover:bg-gray-50">
                <td class="p-4">
                  <div class="font-bold text-gray-800">{{ item.namaBarang }}</div>
                  <div class="text-xs text-gray-500 italic" v-if="item.catatan">{{ item.catatan }}</div>
                </td>
                <td class="p-4 text-center">{{ item.jumlah }}</td>
                <td class="p-4 text-right">{{ formatRupiah(item.hargaSatuan) }}</td>
                <td class="p-4 text-right font-bold">{{ formatRupiah(item.totalHarga) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="flex justify-end">
          <div class="w-full md:w-1/2 space-y-3">
            <div v-if="nota.sisa > 0" class="flex justify-between text-red-600 font-bold">
              <span>Sisa Bayar:</span>
              <span>{{ formatRupiah(nota.sisa) }}</span>
            </div>

            <div v-if="nota.dp > 0" class="flex justify-between text-gray-600 font-medium">
              <span>Uang Muka (DP):</span>
              <span>{{ formatRupiah(nota.dp) }}</span>
            </div>

            <div class="flex justify-between border-t-2 pt-3 text-2xl font-black text-blue-800">
              <span>Total Tagihan:</span>
              <span>{{ formatRupiah(nota.total) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div v-else class="flex flex-col items-center justify-center h-screen text-gray-400">
    <i class="bi bi-arrow-clockwise animate-spin text-5xl mb-4"></i>
    <p class="font-bold uppercase tracking-widest">Memuat Detail Nota...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';

const route = useRoute();
const router = useRouter();
const nota = ref(null);

onMounted(async () => {
  try {
    const res = await api.get(`/api/nota/${route.params.id}`);
    nota.value = res.data;
  } catch (e) {
    alert("Nota tidak ditemukan!");
    router.push('/riwayat');
  }
});

const printPdf = async () => {
  try {
    const response = await api.get(`/api/nota/${nota.value.id}/pdf`, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `Nota-${nota.value.kodeNota}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (error) {
    alert("Gagal mendownload PDF.");
  }
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0 }).format(v || 0);
const formatDate = (d) => new Date(d).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
const getStatusColor = (status) => {
  switch(status) {
    case 'PROSES': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'SELESAI': return 'bg-green-100 text-green-800 border-green-200';
    case 'LUNAS': return 'bg-blue-100 text-blue-800 border-blue-200';
    default: return 'bg-gray-100 border-gray-200';
  }
};
</script>