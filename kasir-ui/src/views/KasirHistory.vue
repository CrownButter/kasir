<template>
  <div class="container mx-auto p-4 md:p-6 relative">
    
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
      <h2 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
        <i class="bi bi-clock-history"></i> Riwayat Transaksi
      </h2>
      <button @click="$router.push('/tambah')" class="w-full md:w-auto bg-blue-600 text-white px-6 py-2 rounded shadow hover:bg-blue-700 font-bold flex items-center justify-center gap-2">
        <i class="bi bi-plus-lg"></i> Buat Nota Baru
      </button>
    </div>

    <div class="bg-white p-4 rounded shadow mb-4">
      <div class="flex flex-col md:flex-row justify-between gap-4 items-center">
        
        <div class="flex bg-gray-100 p-1 rounded-lg w-full md:w-auto">
          <button @click="activeTab = 'SERVICE'"
            :class="activeTab === 'SERVICE' ? 'bg-white text-blue-600 shadow' : 'text-gray-500 hover:text-gray-700'"
            class="flex-1 md:flex-none px-6 py-2 rounded-md font-bold transition-all duration-200 flex items-center justify-center gap-2">
            <i class="bi bi-tools"></i> Service
          </button>
          <button @click="activeTab = 'JUAL'"
            :class="activeTab === 'JUAL' ? 'bg-white text-green-600 shadow' : 'text-gray-500 hover:text-gray-700'"
            class="flex-1 md:flex-none px-6 py-2 rounded-md font-bold transition-all duration-200 flex items-center justify-center gap-2">
            <i class="bi bi-cart-fill"></i> Penjualan
          </button>
        </div>

        <div class="flex gap-2 w-full md:w-auto">
          <select v-model="selectedMonth" class="flex-1 md:flex-none border p-2 rounded bg-gray-50 focus:ring-2 focus:ring-blue-500 outline-none cursor-pointer">
            <option v-for="(bulan, index) in namaBulan" :key="index" :value="index + 1">
              {{ bulan }}
            </option>
          </select>
          <select v-model="selectedYear" class="flex-1 md:flex-none border p-2 rounded bg-gray-50 focus:ring-2 focus:ring-blue-500 outline-none cursor-pointer">
            <option v-for="year in yearList" :key="year" :value="year">
              {{ year }}
            </option>
          </select>
        </div>

      </div>
    </div>

    <div class="bg-white rounded shadow overflow-hidden border border-gray-200">
       <div class="overflow-x-auto">
          <table class="w-full text-left border-collapse whitespace-nowrap">
            <thead class="bg-gray-100 border-b">
              <tr>
                <th class="p-4 font-bold">No. Nota</th>
                <th class="p-4 font-bold">Tanggal</th>
                <th class="p-4 font-bold">Customer</th>
                <th class="p-4 font-bold text-center">Status</th>
                <th class="p-4 font-bold text-right">Total</th>
                <th class="p-4 font-bold text-center w-48">Aksi</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="nota in filteredNota" :key="nota.id" class="border-b hover:bg-gray-50">
                <td class="p-4 font-mono text-blue-600 font-bold">{{ nota.kodeNota || nota.id }}</td>
                <td class="p-4">
                  <div>{{ formatDate(nota.tanggal) }}</div>
                  <div v-if="nota.tipe === 'SERVICE'" class="text-xs text-gray-500 flex items-center gap-1 mt-1">
                    <i class="bi bi-box"></i> {{ nota.barangCustomer || '-' }}
                  </div>
                </td>
                <td class="p-4 font-bold">{{ nota.customerNama }}</td>
                <td class="p-4 text-center">
                  <span :class="getStatusColor(nota.status)" class="px-3 py-1 rounded-full text-xs font-bold border inline-block shadow-sm">
                    {{ nota.status }}
                  </span>
                </td>
                <td class="p-4 font-bold text-right">Rp {{ formatRupiah(nota.total) }}</td>
                <td class="p-4 text-center">
                  <div class="flex justify-center gap-2">
                    <button @click="openDetail(nota.id)" class="bg-cyan-600 text-white p-2 rounded hover:bg-cyan-700 shadow" title="Lihat Detail">
                      <i class="bi bi-eye-fill"></i>
                    </button>
                    <button @click="printPdf(nota)" class="bg-gray-800 text-white p-2 rounded hover:bg-black shadow" title="Download PDF">
                      <i class="bi bi-printer-fill"></i>
                    </button>
                    <button @click="$router.push(`/edit/${nota.id}`)" class="bg-yellow-500 text-white p-2 rounded hover:bg-yellow-600 shadow" title="Edit Data">
                      <i class="bi bi-pencil-square"></i>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredNota.length === 0">
                <td colspan="6" class="p-8 text-center text-gray-500 italic">
                  Tidak ada data {{ activeTab === 'SERVICE' ? 'Service' : 'Penjualan' }} pada bulan {{ namaBulan[selectedMonth - 1] }} {{ selectedYear }}.
                </td>
              </tr>
            </tbody>
          </table>
       </div>
    </div>

    <NotaDetailModal
      v-if="selectedDetailId"
      :notaId="selectedDetailId"
      @close="selectedDetailId = null"
      @refresh="fetchData"
    />

  </div> </template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../api';
import NotaDetailModal from '../components/NotaDetailModal.vue';

const listNota = ref([]);
const selectedDetailId = ref(null);

// STATE FILTER
const activeTab = ref('SERVICE');
const selectedMonth = ref(new Date().getMonth() + 1);
const selectedYear = ref(new Date().getFullYear());

const namaBulan = [
  "Januari", "Februari", "Maret", "April", "Mei", "Juni",
  "Juli", "Agustus", "September", "Oktober", "November", "Desember"
];

// Generate List Tahun
const yearList = computed(() => {
  const current = new Date().getFullYear();
  const years = [];
  for (let i = current - 5; i <= current + 1; i++) {
    years.push(i);
  }
  return years.reverse();
});

onMounted(async () => { fetchData(); });

const fetchData = async () => {
  try {
    const res = await api.get('/api/nota');
    if(Array.isArray(res.data)) {
      listNota.value = res.data;
    }
  } catch (e) { console.error(e); }
};

// LOGIC FILTERING
const filteredNota = computed(() => {
  return listNota.value
    .filter(nota => {
      // 1. Filter by Tipe
      if (nota.tipe !== activeTab.value) return false;
      
      // 2. Filter by Tanggal
      if (!nota.tanggal) return false;
      const date = new Date(nota.tanggal);
      const monthMatch = (date.getMonth() + 1) == parseInt(selectedMonth.value);
      const yearMatch = date.getFullYear() == parseInt(selectedYear.value);
      
      return monthMatch && yearMatch;
    })
    .sort((a, b) => b.id - a.id);
});

const openDetail = (id) => {
  selectedDetailId.value = id;
};

// PRINT PDF
const printPdf = async (nota) => {
  try {
    const response = await api.get(`/api/nota/${nota.id}/pdf`, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `Nota-${nota.kodeNota}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Gagal download PDF", error);
    alert("Gagal mendownload PDF.");
  }
};

const getStatusColor = (status) => {
  switch(status) {
    case 'PROSES': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    case 'PENDING': return 'bg-orange-100 text-orange-800 border-orange-200';
    case 'SELESAI': return 'bg-green-100 text-green-800 border-green-200';
    case 'LUNAS': return 'bg-blue-100 text-blue-800 border-blue-200';
    case 'BATAL': return 'bg-red-100 text-red-800 border-red-200';
    default: return 'bg-gray-100 border-gray-200';
  }
};

const formatDate = (d) => {
  if(!d) return '-';
  return new Date(d).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit' });
};

const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);
</script>