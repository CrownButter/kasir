<template>
  <div class="container mx-auto p-6 max-w-7xl">
    <div class="bg-white shadow-xl rounded-2xl p-6 min-h-screen">
      <div class="flex justify-between items-center mb-8 border-b pb-4">
        <div>
          <h2 class="text-3xl font-black text-gray-800 uppercase tracking-tight">
            Laporan Penjualan
          </h2>
          <p class="text-gray-500 text-sm">Rekapitulasi transaksi dan pendapatan toko.</p>
        </div>
        <button @click="exportData" class="bg-green-600 text-white px-4 py-2 rounded-lg font-bold hover:bg-green-700 transition flex items-center gap-2">
          <i class="bi bi-file-earmark-excel"></i> Export Excel
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8 bg-gray-50 p-4 rounded-xl border">
        <div>
          <label class="block text-xs font-bold text-gray-500 uppercase mb-1">Dari Tanggal</label>
          <input type="date" v-model="filter.startDate" class="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label class="block text-xs font-bold text-gray-500 uppercase mb-1">Sampai Tanggal</label>
          <input type="date" v-model="filter.endDate" class="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500" />
        </div>
        <div class="flex items-end">
          <button @click="loadLaporan" class="w-full bg-blue-600 text-white p-2 rounded-lg font-bold hover:bg-blue-700 transition">
            Filter Laporan
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-blue-600 p-6 rounded-2xl text-white shadow-lg shadow-blue-100">
          <p class="text-sm opacity-80 font-bold uppercase">Total Transaksi</p>
          <h3 class="text-3xl font-black mt-1">{{ dataLaporan.length }}</h3>
        </div>
        <div class="bg-emerald-600 p-6 rounded-2xl text-white shadow-lg shadow-emerald-100">
          <p class="text-sm opacity-80 font-bold uppercase">Total Omzet (Penjualan)</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalOmzet) }}</h3>
        </div>
        <div class="bg-purple-600 p-6 rounded-2xl text-white shadow-lg shadow-purple-100">
          <p class="text-sm opacity-80 font-bold uppercase">Estimasi Laba Kotor</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalLaba) }}</h3>
        </div>
      </div>

      <div class="overflow-x-auto border rounded-xl shadow-inner">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-800 text-white font-bold uppercase">
            <tr>
              <th class="p-4">Tanggal</th>
              <th class="p-4">No. Nota</th>
              <th class="p-4">Customer</th>
              <th class="p-4 text-right">Total Jual</th>
              <th class="p-4 text-right">Profit</th>
            </tr>
          </thead>
          <tbody class="divide-y">
            <tr v-for="nota in dataLaporan" :key="nota.id" class="hover:bg-gray-50">
              <td class="p-4">{{ formatDate(nota.createdAt) }}</td>
              <td class="p-4 font-mono font-bold">{{ nota.noNota }}</td>
              <td class="p-4">{{ nota.customerName || 'Cash' }}</td>
              <td class="p-4 text-right font-bold">{{ formatRupiah(nota.totalHarga) }}</td>
              <td class="p-4 text-right text-emerald-600 font-bold">{{ formatRupiah(nota.totalHarga - nota.totalModal) }}</td>
            </tr>
            <tr v-if="dataLaporan.length === 0">
              <td colspan="5" class="p-10 text-center text-gray-400">Tidak ada data untuk periode ini.</td>
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
const totalLaba = computed(() => dataLaporan.value.reduce((sum, n) => sum + (n.totalHarga - n.totalModal), 0));

const loadLaporan = async () => {
  try {
    const res = await api.get(`/api/nota/report?start=${filter.startDate}&end=${filter.endDate}`);
    dataLaporan.value = res.data;
  } catch (e) {
    alert("Gagal memuat laporan");
  }
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(v || 0);
const formatDate = (s) => new Date(s).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' });

onMounted(loadLaporan);
</script>