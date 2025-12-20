<template>
  <div class="container mx-auto p-4 md:p-8 max-w-4xl min-h-screen pb-20">
    <div v-if="nota" class="bg-white shadow-2xl rounded-3xl overflow-hidden border">
      
      <div class="bg-slate-900 p-8 text-white flex flex-col md:flex-row justify-between items-center gap-6 relative">
        <div class="text-center md:text-left">
          <p class="text-blue-400 text-xs font-black uppercase tracking-widest mb-1 italic">
            {{ nota.tipe === 'SERVICE' ? 'Service Order' : 'Sales Invoice' }}
          </p>
          <h1 class="text-4xl font-mono font-black italic tracking-tighter">{{ nota.kodeNota || nota.nomorSeri }}</h1>
          <p class="mt-2 text-slate-400 font-bold tracking-widest text-xs">{{ formatDate(nota.tanggal) }}</p>
        </div>

        <div v-if="nota.status === 'LUNAS' || nota.sisa <= 0" 
             class="absolute right-1/2 md:right-64 top-1/2 -translate-y-1/2 border-8 border-green-500/30 text-green-500/40 px-8 py-2 rounded-2xl font-black text-6xl rotate-[-15deg] uppercase tracking-[0.5em] pointer-events-none select-none">
          Lunas
        </div>

        <div class="flex gap-3 z-10">
          <button @click="printPdf" class="bg-white/10 hover:bg-white/20 px-6 py-3 rounded-2xl font-bold flex items-center gap-2 transition border border-white/10 text-sm">
            <i class="bi bi-printer-fill"></i> Cetak / PDF
          </button>
          <button @click="$router.push('/riwayat')" class="bg-slate-700 hover:bg-slate-600 px-6 py-3 rounded-2xl font-bold transition text-sm">
            Kembali
          </button>
        </div>
      </div>

      <div class="p-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10 pb-8 border-b border-dashed">
           <div>
              <label class="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-2">Pelanggan</label>
              <h3 class="text-2xl font-black text-slate-800 uppercase">{{ nota.customerNama }}</h3>
              <p class="text-slate-500 font-bold mt-1 text-sm"><i class="bi bi-whatsapp"></i> {{ nota.customerTelp || '-' }}</p>
           </div>
           <div class="md:text-right">
              <label class="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-2">Status Nota</label>
              <span :class="getStatusColor(nota.status)" class="text-sm font-black px-6 py-2 rounded-full border-2 uppercase shadow-sm inline-block">
                {{ nota.status }}
              </span>
           </div>
        </div>

        <div class="mb-10">
          <table class="w-full text-left">
            <thead>
              <tr class="text-[10px] font-black text-gray-400 uppercase border-b-2 border-slate-100">
                <th class="pb-4">Item / Deskripsi</th>
                <th class="pb-4 text-center">Qty</th>
                <th class="pb-4 text-right">Harga Satuan</th>
                <th class="pb-4 text-right">Subtotal</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr v-for="item in nota.snapshots" :key="item.id">
                <td class="py-5">
                  <p class="font-black text-slate-800 uppercase text-sm">{{ item.namaBarang }}</p>
                  <p v-if="item.catatan" class="text-[10px] text-slate-400 font-bold mt-0.5 uppercase tracking-tighter">{{ item.catatan }}</p>
                </td>
                <td class="py-5 text-center font-bold text-slate-600">{{ item.jumlah }}</td>
                <td class="py-5 text-right font-mono text-slate-500 text-sm">{{ formatRupiah(item.hargaSatuan) }}</td>
                <td class="py-5 text-right font-mono font-black text-slate-800">{{ formatRupiah(item.totalHarga) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="flex flex-col items-end">
          <div class="w-full md:w-80 space-y-3">
            
            <div class="flex justify-between items-center text-slate-400 font-bold text-sm">
              <span>TOTAL TAGIHAN</span>
              <span class="text-slate-800 font-black text-lg">{{ formatRupiah(nota.total) }}</span>
            </div>
            
            <div v-if="nota.dp > 0" class="flex justify-between items-center text-orange-600 font-bold text-sm">
              <span>UANG MUKA (DP)</span>
              <span class="font-black">- {{ formatRupiah(nota.dp) }}</span>
            </div>

            <div v-if="nota.sisa > 0" class="flex justify-between items-center p-4 bg-red-600 text-white rounded-2xl shadow-lg shadow-red-100 mt-4">
              <div class="flex flex-col">
                <span class="text-[9px] font-black uppercase opacity-80">Sisa Tagihan</span>
                <span class="text-xl font-black font-mono">{{ formatRupiah(nota.sisa) }}</span>
              </div>
              <i class="bi bi-exclamation-circle-fill text-2xl opacity-50"></i>
            </div>

            <div v-else class="flex justify-between items-center p-4 bg-green-600 text-white rounded-2xl shadow-lg shadow-green-100 mt-4">
              <div class="flex flex-col">
                <span class="text-[9px] font-black uppercase opacity-80">Status Pembayaran</span>
                <span class="text-xl font-black font-mono">LUNAS</span>
              </div>
              <i class="bi bi-check-all text-3xl"></i>
            </div>

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
  } catch (e) {
    console.error(e);
    alert("Gagal memuat detail nota");
  }
};

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
    window.URL.revokeObjectURL(url);
  } catch (error) {
    alert("Gagal mengunduh PDF.");
  }
};

const getStatusColor = (s) => {
  switch(s) {
    case 'PROSES': return 'bg-yellow-50 text-yellow-600 border-yellow-200';
    case 'SELESAI': return 'bg-emerald-50 text-emerald-600 border-emerald-200';
    case 'LUNAS': return 'bg-blue-50 text-blue-600 border-blue-200';
    case 'BATAL': return 'bg-red-50 text-red-600 border-red-200';
    default: return 'bg-gray-50 text-gray-500 border-gray-200';
  }
};

const formatDate = (d) => {
  if (!d) return '-';
  return new Date(d).toLocaleDateString('id-ID', { 
    day: 'numeric', month: 'long', year: 'numeric', 
    hour: '2-digit', minute: '2-digit' 
  });
};

const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);

onMounted(fetchData);
</script>

<style scoped>
.font-mono {
  font-family: 'Courier New', Courier, monospace;
}
</style>