<template>
  <div class="container mx-auto p-4 md:p-8 max-w-4xl min-h-screen pb-20">
    <div v-if="nota" class="bg-white shadow-2xl rounded-3xl overflow-hidden border">
      
      <div class="bg-slate-900 p-8 text-white flex flex-col md:flex-row justify-between items-center gap-6 relative">
        <div class="text-center md:text-left">
          <p class="text-blue-400 text-xs font-black uppercase tracking-widest mb-1">Invoice / Nomor Seri</p>
          <h1 class="text-4xl font-mono font-black italic">{{ nota.kodeNota || nota.nomorSeri }}</h1>
          <p class="mt-2 text-slate-400 font-bold tracking-widest">{{ formatDate(nota.tanggal) }}</p>
        </div>

        <div v-if="nota.sisa <= 0" class="absolute right-10 top-1/2 -translate-y-1/2 border-4 border-green-500 text-green-500 px-6 py-2 rounded-xl font-black text-3xl rotate-12 opacity-50 uppercase tracking-[0.3em] pointer-events-none">
          Lunas
        </div>

        <div class="flex gap-3">
          <button @click="printPdf" class="bg-white/10 hover:bg-white/20 px-6 py-3 rounded-2xl font-bold flex items-center gap-2 transition border border-white/10">
            <i class="bi bi-printer-fill"></i> Download PDF
          </button>
          <button @click="$router.push('/riwayat')" class="bg-red-500 hover:bg-red-600 px-6 py-3 rounded-2xl font-bold transition">
            Tutup
          </button>
        </div>
      </div>

      <div class="p-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10 pb-8 border-b">
           <div>
              <label class="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-2">Data Pelanggan</label>
              <h3 class="text-2xl font-black text-slate-800 uppercase">{{ nota.customerNama }}</h3>
              <p class="text-slate-500 font-bold mt-1">{{ nota.customerTelp || '-' }}</p>
              <p class="text-slate-500 text-sm mt-1 italic">{{ nota.customerAlamat || '-' }}</p>
           </div>
           <div class="md:text-right">
              <label class="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-2">Status Transaksi</label>
              <span :class="getStatusColor(nota.status)" class="text-lg font-black px-6 py-2 rounded-2xl border uppercase shadow-sm inline-block">
                {{ nota.status }}
              </span>
           </div>
        </div>

        <table class="w-full text-left mb-10">
          <thead>
            <tr class="text-[10px] font-black text-gray-400 uppercase border-b-2">
              <th class="pb-4">Deskripsi Produk / Jasa</th>
              <th class="pb-4 text-center">Qty</th>
              <th class="pb-4 text-right">Harga Satuan</th>
              <th class="pb-4 text-right">Subtotal</th>
            </tr>
          </thead>
          <tbody class="divide-y">
            <tr v-for="item in nota.snapshots" :key="item.id" class="group">
              <td class="py-5">
                <p class="font-black text-slate-800 uppercase">{{ item.namaBarang }}</p>
                <p v-if="item.catatan" class="text-xs text-slate-400 mt-1 italic">{{ item.catatan }}</p>
              </td>
              <td class="py-5 text-center font-bold text-slate-600">{{ item.jumlah }}</td>
              <td class="py-5 text-right font-mono text-slate-500">{{ formatRupiah(item.hargaSatuan) }}</td>
              <td class="py-5 text-right font-mono font-black text-slate-800">{{ formatRupiah(item.totalHarga) }}</td>
            </tr>
          </tbody>
        </table>

        <div class="flex flex-col items-end gap-3">
          <div class="flex justify-between w-full md:w-80 border-t pt-4">
            <span class="font-bold text-slate-400 uppercase text-xs">Total Tagihan</span>
            <span class="font-mono font-black text-xl text-slate-800">{{ formatRupiah(nota.total) }}</span>
          </div>
          
          <div v-if="nota.dp > 0" class="flex justify-between w-full md:w-80 text-orange-600">
            <span class="font-bold uppercase text-xs">Uang Muka (DP)</span>
            <span class="font-mono font-black">- {{ formatRupiah(nota.dp) }}</span>
          </div>

          <div v-if="nota.sisa > 0" class="flex justify-between w-full md:w-80 p-4 bg-red-50 rounded-2xl border border-red-100 text-red-600 mt-2">
            <span class="font-black uppercase text-xs">Sisa Tagihan</span>
            <span class="font-mono font-black text-xl">{{ formatRupiah(nota.sisa) }}</span>
          </div>

          <div v-else class="flex justify-between w-full md:w-80 p-4 bg-green-50 rounded-2xl border border-green-100 text-green-600 mt-2">
            <span class="font-black uppercase text-xs">Sisa Tagihan</span>
            <span class="font-black uppercase text-sm italic">SUDAH LUNAS</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../api';

const route = useRoute();
const nota = ref(null);

const fetchData = async () => {
  try {
    const res = await api.get(`/api/nota/${route.params.id}`);
    nota.value = res.data;
  } catch (e) { alert("Gagal memuat detail nota"); }
};

const printPdf = async () => {
  try {
    const response = await api.get(`/api/nota/${nota.value.id}/pdf`, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url; link.setAttribute('download', `Nota-${nota.value.kodeNota}.pdf`);
    document.body.appendChild(link); link.click(); link.remove();
  } catch (error) { alert("Gagal download PDF."); }
};

const getStatusColor = (s) => {
  switch(s) {
    case 'PROSES': return 'bg-yellow-50 text-yellow-600 border-yellow-200';
    case 'SELESAI': return 'bg-green-50 text-green-600 border-green-200';
    case 'LUNAS': return 'bg-blue-50 text-blue-600 border-blue-200';
    default: return 'bg-gray-50 text-gray-400';
  }
};

const formatDate = (d) => new Date(d).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });
const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);

onMounted(fetchData);
</script>