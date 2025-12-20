<template>
  <div class="container mx-auto p-6 max-w-7xl">
    <div class="bg-white shadow-xl rounded-2xl p-6 min-h-screen">
      <div class="flex justify-between items-center mb-8 border-b pb-4">
        <div>
          <h2 class="text-3xl font-black text-gray-800 uppercase tracking-tight flex items-center gap-3">
            <i class="bi bi-file-earmark-bar-graph text-blue-600"></i> Laporan Penjualan
          </h2>
          <p class="text-gray-500 text-sm">Rekapitulasi transaksi sesuai rentang tanggal.</p>
        </div>
        <button @click="loadLaporan" class="bg-blue-600 text-white px-6 py-2 rounded-lg font-bold hover:bg-blue-700 transition">
          Refresh Data
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8 bg-gray-50 p-4 rounded-xl border">
        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Dari Tanggal</label>
          <input type="date" v-model="filter.startDate" class="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
        </div>
        <div>
          <label class="block text-[10px] font-black text-gray-400 uppercase mb-1">Sampai Tanggal</label>
          <input type="date" v-model="filter.endDate" class="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
        </div>
        <div class="flex items-end">
          <button @click="loadLaporan" class="w-full bg-blue-600 text-white p-2 rounded-lg font-bold hover:bg-blue-700 transition">
            Cari Laporan
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-blue-600 p-6 rounded-2xl text-white shadow-lg">
          <p class="text-xs opacity-80 font-bold uppercase">Total Transaksi</p>
          <h3 class="text-3xl font-black mt-1">{{ dataLaporan.length }}</h3>
        </div>
        <div class="bg-emerald-600 p-6 rounded-2xl text-white shadow-lg">
          <p class="text-xs opacity-80 font-bold uppercase">Total Omzet</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalOmzet) }}</h3>
        </div>
        <div class="bg-purple-600 p-6 rounded-2xl text-white shadow-lg">
          <p class="text-xs opacity-80 font-bold uppercase">Estimasi Laba</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalLaba) }}</h3>
        </div>
      </div>

      <div class="overflow-x-auto border rounded-xl shadow-inner bg-white">
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
          <tbody class="divide-y">
            <tr v-for="nota in dataLaporan" :key="nota.id" class="hover:bg-gray-50 transition">
              <td class="p-4">{{ formatDate(nota.tanggal) }}</td> <td class="p-4 font-mono font-bold text-blue-600">{{ nota.nomorSeri }}</td> <td class="p-4 font-bold">{{ nota.customerNama || 'Cash' }}</td> <td class="p-4 text-right font-black text-gray-800">{{ formatRupiah(nota.total) }}</td> <td class="p-4 text-right text-emerald-600 font-black">{{ formatRupiah(nota.total - (nota.totalModal || 0)) }}</td>
            </tr>
            <tr v-if="dataLaporan.length === 0">
              <td colspan="5" class="p-20 text-center text-gray-400">
                <i class="bi bi-inbox text-5xl block mb-2"></i>
                Tidak ada data transaksi ditemukan.
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

// Menghitung omzet menggunakan field 'total' sesuai NotaEntity
const totalOmzet = computed(() => {
  return dataLaporan.value.reduce((sum, n) => sum + (n.total || 0), 0);
});

// Menghitung laba: total - totalModal
const totalLaba = computed(() => {
  return dataLaporan.value.reduce((sum, n) => sum + ((n.total || 0) - (n.totalModal || 0)), 0);
});

const loadLaporan = async () => {
  try {
    const res = await api.get(`/api/nota/report?start=${filter.startDate}&end=${filter.endDate}`);
    dataLaporan.value = res.data;
    console.log("Data diterima:", res.data); // Untuk debug di console browser
  } catch (e) {
    console.error("Gagal load laporan:", e);
    alert("Gagal memuat laporan");
  }
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(v || 0);

const formatDate = (s) => {
  if (!s) return '-';
  return new Date(s).toLocaleDateString('id-ID', { day: '2-digit', month: 'short', year: 'numeric' });
};

onMounted(loadLaporan);
</script>