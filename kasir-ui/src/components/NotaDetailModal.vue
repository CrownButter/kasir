<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-75 p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden">
      
      <div class="bg-gray-100 px-6 py-4 border-b flex justify-between items-center">
        <div>
            <h3 class="text-xl font-bold text-gray-800">Detail Nota</h3>
            <p class="text-sm font-mono text-blue-600 font-bold">{{ nota?.kodeNota || 'Loading...' }}</p>
        </div>
        <button @click="$emit('close')" class="text-gray-500 hover:text-red-500 text-2xl font-bold">&times;</button>
      </div>

      <div class="p-6 overflow-y-auto flex-1" v-if="nota">
        
        <div class="bg-blue-50 border border-blue-200 rounded p-4 mb-6 flex items-center justify-between">
            <div>
                <p class="text-xs font-bold text-blue-800 uppercase mb-1">Status Saat Ini</p>
                <span :class="getStatusColor(nota.status)" class="px-3 py-1 rounded text-sm font-bold border inline-block">
                    {{ nota.status }}
                </span>
            </div>
            <div class="flex items-end gap-2">
                <div>
                    <label class="text-xs font-bold text-gray-500 block mb-1">Ubah Status:</label>
                    <select v-model="newStatus" class="border p-2 rounded text-sm outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer bg-white">
                        <option value="PROSES">PROSES</option>
                        <option value="PENDING">PENDING</option>
                        <option value="SELESAI">SELESAI</option>
                        <option value="LUNAS">LUNAS</option>
                        <option value="BATAL">BATAL</option>
                    </select>
                </div>
                <button @click="updateStatus" :disabled="newStatus === nota.status" class="bg-blue-600 text-white px-4 py-2 rounded text-sm font-bold hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed">
                    Update
                </button>
            </div>
        </div>

        <div class="grid grid-cols-2 gap-4 mb-6 text-sm">
            <div>
                <p class="font-bold text-gray-500 text-xs uppercase">Pelanggan</p>
                <p class="font-bold text-gray-800 text-lg">{{ nota.customerNama }}</p>
                <p class="text-gray-600">{{ nota.customerTelp }}</p>
                <p class="text-gray-600 italic">{{ nota.customerAlamat }}</p>
            </div>
            <div class="text-right">
                <p class="font-bold text-gray-500 text-xs uppercase">Tanggal</p>
                <p class="font-bold text-gray-800">{{ formatDate(nota.tanggal) }}</p>
                <div v-if="nota.barangCustomer" class="mt-2">
                    <p class="font-bold text-gray-500 text-xs uppercase">Unit Service</p>
                    <p class="font-bold text-blue-700">{{ nota.barangCustomer }}</p>
                </div>
            </div>
        </div>

        <div class="border rounded overflow-hidden mb-4">
            <table class="w-full text-sm">
                <thead class="bg-gray-100 text-gray-700">
                    <tr>
                        <th class="p-2 text-left">Barang / Jasa</th>
                        <th class="p-2 text-center">Qty</th>
                        <th class="p-2 text-right">Harga</th>
                        <th class="p-2 text-right">Subtotal</th>
                    </tr>
                </thead>
                <tbody class="divide-y">
                    <tr v-for="(item, i) in nota.snapshots" :key="i">
                        <td class="p-2">
                            <div class="font-bold">{{ item.namaBarang }}</div>
                            <div class="text-xs text-gray-500 italic">{{ item.catatan }}</div>
                        </td>
                        <td class="p-2 text-center">{{ item.jumlah }}</td>
                        <td class="p-2 text-right">{{ formatRupiah(item.hargaSatuan) }}</td>
                        <td class="p-2 text-right font-bold">{{ formatRupiah(item.totalHarga) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="flex justify-end">
            <div class="w-1/2 space-y-1 text-right">
                <div class="flex justify-between">
                    <span class="text-gray-600">Total Tagihan:</span>
                    <span class="font-bold text-lg">Rp {{ formatRupiah(nota.total) }}</span>
                </div>
                <div class="flex justify-between text-gray-500 text-sm">
                    <span>DP:</span>
                    <span>- Rp {{ formatRupiah(nota.dp) }}</span>
                </div>
                <div class="flex justify-between border-t pt-2 mt-2">
                    <span class="font-bold text-blue-800">Sisa Bayar:</span>
                    <span class="font-bold text-xl text-blue-800">Rp {{ formatRupiah(nota.sisa) }}</span>
                </div>
            </div>
        </div>

      </div>
      <div class="flex justify-center items-center h-64" v-else>
          <i class="bi bi-arrow-clockwise animate-spin text-4xl text-gray-400"></i>
      </div>

      <div class="bg-gray-50 px-6 py-4 border-t flex justify-end gap-2">
        <button @click="$emit('close')" class="px-4 py-2 bg-gray-200 text-gray-700 rounded font-bold hover:bg-gray-300">Tutup</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const props = defineProps(['notaId']);
const emit = defineEmits(['close', 'refresh']); // Emit refresh agar tabel history reload

const nota = ref(null);
const newStatus = ref("");

// Load Detail Nota dari API
onMounted(async () => {
    try {
        const res = await api.get(`/api/nota/${props.notaId}`);
        nota.value = res.data;
        newStatus.value = res.data.status; // Set default dropdown
    } catch (e) {
        alert("Gagal memuat detail nota");
        emit('close');
    }
});

// Logic Update Status
const updateStatus = async () => {
    if (!confirm(`Ubah status menjadi ${newStatus.value}?`)) return;
    
    try {
        await api.put(`/api/nota/${props.notaId}/status`, { status: newStatus.value });
        nota.value.status = newStatus.value; // Update UI Modal
        alert("Status berhasil diperbarui!");
        emit('refresh'); // Beritahu parent (KasirHistory) untuk refresh data
    } catch (e) {
        alert("Gagal update: " + (e.response?.data?.message || e.message));
    }
};

// Helpers
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
const formatDate = (d) => new Date(d).toLocaleDateString('id-ID', {day: 'numeric', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit'});
const formatRupiah = (v) => new Intl.NumberFormat('id-ID').format(v || 0);
</script>