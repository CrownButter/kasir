<template>
  <div class="max-w-6xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
     <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 border-b pb-4 gap-4">
         <h3 class="text-xl md:text-2xl font-bold text-gray-800 flex items-center gap-2 uppercase tracking-wider">
            <i class="bi bi-journal-text"></i> Laporan Transaksi
         </h3>
         <button @click="exportPDF" class="bg-red-600 text-white px-6 py-2 rounded-lg font-bold">Export PDF</button>
     </div>

     <div class="bg-gray-50 p-4 rounded-xl border mb-6 grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
         <div>
            <label class="text-[10px] font-bold text-gray-400 uppercase">Dari Tanggal</label>
            <input type="date" v-model="filterDate.start" class="w-full border p-2 rounded-lg text-black font-bold"/>
         </div>
         <div>
            <label class="text-[10px] font-bold text-gray-400 uppercase">Sampai Tanggal</label>
            <input type="date" v-model="filterDate.end" class="w-full border p-2 rounded-lg text-black font-bold"/>
         </div>
         <div class="bg-blue-600 text-white p-3 rounded-xl shadow-lg md:col-start-4">
            <p class="text-[9px] font-bold uppercase opacity-80">Total Pendapatan</p>
            <p class="text-lg font-black font-mono">Rp {{ formatRupiah(totalPendapatan) }}</p>
         </div>
     </div>

     <div class="overflow-x-auto rounded-xl border text-black font-bold">
         <table class="w-full text-xs border-collapse">
            <thead class="bg-gray-800 text-white uppercase tracking-wider text-[10px]">
               <tr>
                  <th class="p-3 text-left">Tanggal</th>
                  <th class="p-3 text-left">No Nota</th>
                  <th class="p-3 text-left">Tipe</th>
                  <th class="p-3 text-right">Total</th>
               </tr>
            </thead>
            <tbody>
               <tr v-for="nota in filteredLaporan" :key="nota.id" class="border-b hover:bg-gray-50">
                  <td class="p-3">{{ new Date(nota.tanggal).toLocaleDateString('id-ID') }}</td>
                  <td class="p-3 font-mono text-blue-600">{{ nota.kodeNota || nota.id }}</td>
                  <td class="p-3">{{ nota.tipe }}</td>
                  <td class="p-3 text-right">Rp {{ formatRupiah(nota.total) }}</td>
               </tr>
            </tbody>
         </table>
     </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../../api';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const laporan = ref([]);
const filterDate = ref({ start: "", end: "" });

onMounted(async () => {
  try {
    const res = await api.get('/api/nota'); // atau api report khusus
    if(Array.isArray(res.data)) laporan.value = res.data;
  } catch(e) {}
});

const filteredLaporan = computed(() => {
    // ... Logic filter yang sama dengan sebelumnya ...
    let data = [...laporan.value];
    if (filterDate.value.start) data = data.filter(i => new Date(i.tanggal) >= new Date(filterDate.value.start));
    if (filterDate.value.end) {
        const end = new Date(filterDate.value.end);
        end.setHours(23, 59, 59, 999);
        data = data.filter(i => new Date(i.tanggal) <= end);
    }
    return data.sort((a, b) => new Date(b.tanggal) - new Date(a.tanggal));
});

const totalPendapatan = computed(() => filteredLaporan.value.reduce((sum, item) => sum + (item.total || 0), 0));

const formatRupiah = (v) => new Intl.NumberFormat('id-ID').format(v || 0);
const exportPDF = () => { /* ... Logic PDF ... */ };
</script>