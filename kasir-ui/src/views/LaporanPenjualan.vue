<template>
  <div class="container mx-auto p-6 max-w-7xl">
    <div class="bg-white shadow-xl rounded-2xl p-6 min-h-screen">
      <div class="flex justify-between items-center mb-8 border-b pb-4">
        <div>
          <h2 class="text-3xl font-black text-gray-800 uppercase tracking-tight flex items-center gap-3">
            <i class="bi bi-file-earmark-bar-graph text-blue-600"></i> Laporan Penjualan
          </h2>
          <p class="text-gray-500 text-sm">Rekapitulasi transaksi sesuai rentang tanggal (Nota Batal diabaikan).</p>
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
          <button @click="loadLaporan" class="w-full bg-blue-600 text-white p-2 rounded-lg font-bold hover:bg-blue-700 transition shadow-md">
            Cari Laporan
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div class="bg-blue-600 p-6 rounded-2xl text-white shadow-lg relative overflow-hidden group">
          <i class="bi bi-check-all absolute -right-4 -bottom-4 text-8xl opacity-10 group-hover:scale-110 transition"></i>
          <p class="text-xs opacity-80 font-black uppercase tracking-wider">Transaksi Sukses</p>
          <h3 class="text-3xl font-black mt-1">{{ countSukses }} <span class="text-sm font-normal opacity-60">Nota</span></h3>
        </div>
        <div class="bg-emerald-600 p-6 rounded-2xl text-white shadow-lg relative overflow-hidden group">
          <i class="bi bi-cash-stack absolute -right-4 -bottom-4 text-8xl opacity-10 group-hover:scale-110 transition"></i>
          <p class="text-xs opacity-80 font-black uppercase tracking-wider">Total Omzet (Lunas)</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalOmzet) }}</h3>
        </div>
        <div class="bg-purple-600 p-6 rounded-2xl text-white shadow-lg relative overflow-hidden group">
          <i class="bi bi-graph-up-arrow absolute -right-4 -bottom-4 text-8xl opacity-10 group-hover:scale-110 transition"></i>
          <p class="text-xs opacity-80 font-black uppercase tracking-wider">Estimasi Laba Bersih</p>
          <h3 class="text-3xl font-black mt-1">{{ formatRupiah(totalLaba) }}</h3>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div class="bg-amber-500 p-6 rounded-2xl text-white shadow-md border-b-4 border-amber-600">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-[10px] font-black uppercase tracking-widest opacity-80">Menunggu Pembayaran (Pending/Proses)</p>
              <h3 class="text-2xl font-black mt-1">{{ formatRupiah(totalPending) }}</h3>
            </div>
            <div class="bg-white/20 p-2 rounded-lg">
              <i class="bi bi-hourglass-split text-xl"></i>
            </div>
          </div>
          <p class="text-[10px] mt-2 font-bold italic">* Belum masuk ke hitungan Omzet</p>
        </div>

        <div class="bg-slate-700 p-6 rounded-2xl text-white shadow-md border-b-4 border-slate-800">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-[10px] font-black uppercase tracking-widest opacity-80">Jumlah Nota Aktif</p>
              <h3 class="text-2xl font-black mt-1">{{ countPending }} <span class="text-sm font-normal opacity-50">Antrian</span></h3>
            </div>
            <div class="bg-white/20 p-2 rounded-lg">
              <i class="bi bi-layers-half text-xl"></i>
            </div>
          </div>
          <p class="text-[10px] mt-2 font-bold italic uppercase opacity-60">Total {{ dataLaporan.length }} Nota ({{ countBatal }} Dibatalkan)</p>
        </div>
      </div>

      <div class="overflow-x-auto border rounded-xl shadow-inner bg-white">
        <table class="w-full text-sm text-left">
          <thead class="bg-gray-800 text-white font-bold uppercase text-[11px]">
            <tr>
              <th class="p-4">Tanggal</th>
              <th class="p-4">No. Nota</th>
              <th class="p-4">Customer</th>
              <th class="p-4 text-center">Status</th>
              <th class="p-4 text-right">Total Jual</th>
              <th class="p-4 text-right">Profit</th>
            </tr>
          </thead>
          <tbody class="divide-y">
            <tr v-for="nota in sortedLaporan" :key="nota.id" :class="nota.status === 'BATAL' ? 'bg-red-50 opacity-50' : 'hover:bg-gray-50 transition'">
              <td class="p-4">{{ formatDate(nota.tanggal) }}</td>
              <td class="p-4 font-mono font-bold text-blue-600">{{ nota.nomorSeri }}</td>
              <td class="p-4 font-bold">{{ nota.customerNama || 'Cash' }}</td>
              <td class="p-4 text-center">
                <span :class="getStatusBadge(nota.status)" class="px-2 py-0.5 rounded text-[9px] font-black uppercase border">
                  {{ nota.status }}
                </span>
              </td>
              <td class="p-4 text-right font-black text-gray-800">
                <span :class="nota.status === 'BATAL' ? 'line-through text-gray-400' : ''">
                  {{ formatRupiah(nota.total) }}
                </span>
              </td>
              <td class="p-4 text-right font-black" :class="nota.status === 'BATAL' ? 'text-gray-400' : 'text-emerald-600'">
                {{ nota.status === 'BATAL' ? 'Rp 0' : formatRupiah(nota.total - (nota.totalModal || 0)) }}
              </td>
            </tr>
            <tr v-if="dataLaporan.length === 0">
              <td colspan="6" class="p-20 text-center text-gray-400">
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

// 1. Logika Omzet: Hanya hitung yang LUNAS atau SELESAI
const totalOmzet = computed(() => {
  return dataLaporan.value
    .filter(n => n.status === 'LUNAS' || n.status === 'SELESAI')
    .reduce((sum, n) => sum + (n.total || 0), 0);
});

// 2. Logika Laba: Hanya hitung yang LUNAS atau SELESAI
const totalLaba = computed(() => {
  return dataLaporan.value
    .filter(n => n.status === 'LUNAS' || n.status === 'SELESAI')
    .reduce((sum, n) => sum + ((n.total || 0) - (n.totalModal || 0)), 0);
});

// 3. Logika Pending/Proses (Card Baru): Uang yang masih dalam proses
const totalPending = computed(() => {
  return dataLaporan.value
    .filter(n => n.status === 'PENDING' || n.status === 'PROSES')
    .reduce((sum, n) => sum + (n.total || 0), 0);
});

// 4. Counter Berdasarkan Status
const countSukses = computed(() => dataLaporan.value.filter(n => n.status === 'LUNAS' || n.status === 'SELESAI').length);
const countPending = computed(() => dataLaporan.value.filter(n => n.status === 'PENDING' || n.status === 'PROSES').length);
const countBatal = computed(() => dataLaporan.value.filter(n => n.status === 'BATAL').length);

// 5. Sorting: Urutkan dari ID terbaru
const sortedLaporan = computed(() => [...dataLaporan.value].sort((a,b) => b.id - a.id));

const loadLaporan = async () => {
  try {
    const res = await api.get(`/api/nota/report?start=${filter.startDate}&end=${filter.endDate}`);
    dataLaporan.value = res.data;
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

const getStatusBadge = (s) => {
  switch(s) {
    case 'LUNAS': return 'bg-blue-50 text-blue-600 border-blue-200';
    case 'SELESAI': return 'bg-green-50 text-green-600 border-green-200';
    case 'PROSES': return 'bg-amber-50 text-amber-600 border-amber-200';
    case 'PENDING': return 'bg-orange-50 text-orange-600 border-orange-200';
    case 'BATAL': return 'bg-red-50 text-red-400 border-red-100';
    default: return 'bg-gray-50 text-gray-400';
  }
}

onMounted(loadLaporan);
</script>