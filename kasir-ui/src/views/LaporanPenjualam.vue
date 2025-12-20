<template>
  <div class="container mx-auto p-6 max-w-7xl">
    <div class="bg-white shadow-xl rounded-2xl p-6 min-h-screen">
      <div class="flex flex-col md:flex-row justify-between items-center mb-8 border-b pb-4 gap-4">
        <div>
          <h2 class="text-3xl font-black text-gray-800 uppercase tracking-tight flex items-center gap-3">
            <i class="bi bi-file-earmark-bar-graph text-blue-600"></i> Laporan Penjualan
          </h2>
          <p class="text-gray-500 text-sm">Rekapitulasi transaksi dan pendapatan toko.</p>
        </div>
        <div class="flex gap-2">
          <button @click="loadLaporan" class="bg-blue-100 text-blue-700 px-4 py-2 rounded-lg font-bold hover:bg-blue-200 transition">
             <i class="bi bi-arrow-clockwise"></i> Refresh
          </button>
          <button @click="exportData" class="bg-green-600 text-white px-4 py-2 rounded-lg font-bold hover:bg-green-700 transition flex items-center gap-2">
            <i class="bi bi-file-earmark-excel"></i> Export Excel
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8 bg-gray-50 p-4 rounded-xl border">
        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Dari Tanggal</label>
          <input type="date" v-model="filter.startDate" class="w-full p-2.5 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
        </div>
        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Sampai Tanggal</label>
          <input type="date" v-model="filter.endDate" class="w-full p-2.5 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
        </div>
        <div class="flex items-end">
          <button @click="loadLaporan" class="w-full bg-blue-600 text-white p-2.5 rounded-lg font-bold hover:bg-blue-700 transition shadow-md">
            Cari Data
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-gradient-to-br from-blue-600 to-blue-700 p-6 rounded-2xl text-white shadow-lg shadow-blue-100">
          <p class="text-xs opacity-80 font-bold uppercase tracking-wider">Total Transaksi</p>
          <h3 class="text-3xl font-black mt-1">{{ dataLaporan.length }}</h3>
        </div>
        <div class="bg-gradient-to-br from-emerald-600 to-emerald-700 p-6 rounded-2xl text-white shadow-lg shadow-emerald-100">
          <p class="text-xs opacity-80 font-bold uppercase tracking-wider">Total Omzet</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalOmzet) }}</h3>
        </div>
        <div class="bg-gradient-to-br from-purple-600 to-purple-700 p-6 rounded-2xl text-white shadow-lg shadow-purple-100">
          <p class="text-xs opacity-80 font-bold uppercase tracking-wider">Estimasi Laba</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalLaba) }}</h3>
        </div>
      </div>

      <div class="overflow-x-auto border rounded-xl shadow-inner bg-gray-50">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-800 text-white font-bold uppercase text-[11px]">
            <tr>
              <th class="p-4">Tanggal</th>
              <th class="p-4">No. Nota</th>
              <th class="p-4">Customer</th>
              <th class="p-4 text-right">Total Jual</th>
              <th class="p-4 text-right">Profit</th>
            </tr>
          </thead>
          <tbody class="divide-y bg-white">
            <tr v-for="nota in dataLaporan" :key="nota.id" class="hover:bg-blue-50 transition">
              <td class="p-4 text-gray-600">{{ formatDate(nota.createdAt) }}</td>
              <td class="p-4 font-mono font-bold text-blue-600">{{ nota.noNota }}</td>
              <td class="p-4 font-bold text-gray-700">{{ nota.customerName || 'Cash' }}</td>
              <td class="p-4 text-right font-black text-gray-800">{{ formatRupiah(nota.totalHarga) }}</td>
              <td class="p-4 text-right text-emerald-600 font-black">{{ formatRupiah(nota.totalHarga - (nota.totalModal || 0)) }}</td>
            </tr>
            <tr v-if="dataLaporan.length === 0">
              <td colspan="5" class="p-20 text-center text-gray-400">
                 <i class="bi bi-calendar-x text-5xl block mb-3"></i>
                 Tidak ada data transaksi ditemukan untuk periode ini.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import api from '@/api';

const filter = reactive({
  startDate: new Date().toISOString().substr(0, 10),
  endDate: new Date().toISOString().substr(0, 10)
});

const dataLaporan = ref([]);

const totalOmzet = computed(() => dataLaporan.value.reduce((sum, n) => sum + n.totalHarga, 0));
const totalLaba = computed(() => dataLaporan.value.reduce((sum, n) => sum + (n.totalHarga - (n.totalModal || 0)), 0));

const loadLaporan = async () => {
  try {
    const res = await api.get(`/api/nota/report?start=${filter.startDate}&end=${filter.endDate}`);
    dataLaporan.value = res.data;
  } catch (e) {
    console.error("Gagal load laporan:", e);
  }
};

const exportData = () => {
  window.print(); // Cara instan, atau buat fungsi export excel nantinya
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(v || 0);
const formatDate = (s) => new Date(s).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' });

onMounted(loadLaporan);
</script>