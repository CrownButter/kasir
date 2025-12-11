<template>
  <div class="container mx-auto p-6 relative">
    <div class="flex justify-between items-center mb-6">
       <h2 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
          <i class="bi bi-clock-history"></i> Riwayat Service & Penjualan
       </h2>
       <button @click="$router.push('/tambah')" class="bg-blue-600 text-white px-6 py-2 rounded shadow hover:bg-blue-700 font-bold flex items-center gap-2">
          <i class="bi bi-plus-lg"></i> Buat Nota Baru
       </button>
    </div>

    <div class="bg-white rounded shadow overflow-hidden">
       <table class="w-full text-left border-collapse">
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
             <tr v-for="nota in listNota" :key="nota.id" class="border-b hover:bg-gray-50">
                <td class="p-4 font-mono text-blue-600 font-bold">{{ nota.kodeNota }}</td>
                
                <td class="p-4">
                   {{ formatDate(nota.tanggal) }}
                   <div class="text-xs text-gray-500 flex items-center gap-1 mt-1">
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
                       <button @click="openDetail(nota.id)" class="bg-cyan-600 text-white p-2 rounded hover:bg-cyan-700 shadow" title="Lihat Detail & Update Status">
                           <i class="bi bi-eye-fill"></i>
                       </button>

                       <button @click="selectedPrintId = nota.id" class="bg-gray-800 text-white p-2 rounded hover:bg-black shadow" title="Cetak PDF">
                          <i class="bi bi-printer-fill"></i>
                       </button>
                       
                       <button @click="$router.push(`/edit/${nota.id}`)" class="bg-yellow-500 text-white p-2 rounded hover:bg-yellow-600 shadow" title="Edit Data Nota">
                           <i class="bi bi-pencil-square"></i>
                       </button>
                   </div>
                </td>
             </tr>
          </tbody>
       </table>
    </div>

    <NotaDetailModal 
        v-if="selectedDetailId" 
        :notaId="selectedDetailId" 
        @close="selectedDetailId = null" 
        @refresh="fetchData" 
    />

    <div v-if="selectedPrintId" class="fixed inset-0 z-50 bg-black bg-opacity-80 flex items-center justify-center p-4">
       <NotaPrint :notaId="selectedPrintId" @close="selectedPrintId = null" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';
import NotaPrint from './NotaPrint.vue';
import NotaDetailModal from '../components/NotaDetailModal.vue'; // Import komponen baru

const listNota = ref([]);
const selectedPrintId = ref(null);
const selectedDetailId = ref(null); // State untuk modal detail

onMounted(async () => { fetchData(); });

const fetchData = async () => {
  try {
    const res = await api.get('/api/nota');
    if(Array.isArray(res.data)) {
      listNota.value = res.data.sort((a, b) => b.id - a.id);
    }
  } catch (e) { console.error(e); }
};

const openDetail = (id) => {
    selectedDetailId.value = id;
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