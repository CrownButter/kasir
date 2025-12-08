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
                <th class="p-4 font-bold">Status</th>
                <th class="p-4 font-bold">Total</th>
                <th class="p-4 font-bold text-center">Aksi</th>
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
                <td class="p-4">
                   <select :value="nota.status" :class="getStatusColor(nota.status)" @change="confirmStatusChange(nota, $event)" class="px-2 py-1 rounded text-xs font-bold border-2 cursor-pointer outline-none w-32 shadow-sm">
                      <option value="PROSES">PROSES</option>
                      <option value="PENDING">PENDING</option>
                      <option value="SELESAI">SELESAI</option>
                      <option value="LUNAS">LUNAS</option>
                      <option value="BATAL">BATAL</option>
                   </select>
                </td>
                <td class="p-4 font-bold">Rp {{ formatRupiah(nota.total) }}</td>
                <td class="p-4 text-center">
                   <button @click="selectedNotaId = nota.id" class="bg-gray-800 text-white px-3 py-1 rounded text-sm hover:bg-black flex items-center gap-2 mx-auto">
                      <i class="bi bi-file-earmark-pdf-fill"></i> PDF
                   </button>
                </td>
             </tr>
          </tbody>
       </table>
    </div>

    <div v-if="selectedNotaId" class="fixed inset-0 z-50 bg-black bg-opacity-80 flex items-center justify-center p-4">
       <NotaPrint :notaId="selectedNotaId" @close="selectedNotaId = null" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';
import NotaPrint from './NotaPrint.vue';

const listNota = ref([]);
const selectedNotaId = ref(null);

onMounted(async () => { fetchData(); });

const fetchData = async () => {
  try {
    const res = await api.get('/api/nota');
    if(Array.isArray(res.data)) {
      listNota.value = res.data.sort((a, b) => b.id - a.id);
    }
  } catch (e) { console.error(e); }
};

const confirmStatusChange = async (nota, event) => {
  const newStatus = event.target.value;
  const oldStatus = nota.status;
  
  if (confirm(`Apakah Anda yakin ingin mengubah status menjadi "${newStatus}"?`)) {
    try {
      await api.put(`/api/nota/${nota.id}/status`, { status: newStatus });
      nota.status = newStatus;
    } catch (e) {
      alert("Gagal menghubungi server!");
      event.target.value = oldStatus; 
    }
  } else {
    event.target.value = oldStatus;
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

const formatDate = (dateStr) => {
  if(!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute:'2-digit' });
};

const formatRupiah = (val) => new Intl.NumberFormat('id-ID').format(val || 0);
</script>