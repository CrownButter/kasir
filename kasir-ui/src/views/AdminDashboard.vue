<template>
  <div class="min-h-screen bg-gray-100 flex flex-col md:flex-row">
    
    <div class="w-full md:w-64 bg-gray-900 text-white flex flex-col shadow-xl flex-shrink-0">
       <div class="p-4 md:p-6 bg-gray-800 flex justify-between items-center md:block">
          <div>
            <h2 class="text-xl md:text-2xl font-bold tracking-wider flex items-center gap-2">
               <i class="bi bi-hdd-rack-fill"></i> Panel
            </h2>
            <p class="text-gray-400 text-xs mt-1 hidden md:block">Dwin Computer System</p>
          </div>
          </div>

       <nav class="flex md:flex-col overflow-x-auto md:overflow-visible p-2 md:p-4 space-x-2 md:space-x-0 md:space-y-2 bg-gray-900">
          <button @click="activeTab = 'TOKO'" :class="activeTab === 'TOKO' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-shop h-5 w-5"></i><span>Toko</span>
          </button>
          <button @click="activeTab = 'KASIR'" :class="activeTab === 'KASIR' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-people-fill h-5 w-5"></i><span>Kasir</span>
          </button>
          <button @click="activeTab = 'LAPORAN'" :class="activeTab === 'LAPORAN' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-file-earmark-bar-graph-fill h-5 w-5"></i><span>Laporan</span>
          </button>
       </nav>

       <div class="p-4 border-t border-gray-700 mt-auto">
          <button @click="$router.push('/')" class="w-full bg-red-600 py-2 rounded font-bold hover:bg-red-700 transition flex justify-center items-center gap-2 text-sm">
             <i class="bi bi-arrow-left-circle"></i> Back to POS
          </button>
       </div>
    </div>

    <div class="flex-1 p-4 md:p-10 overflow-y-auto w-full">
       
       <div v-if="activeTab === 'TOKO'" class="max-w-3xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow">
          <h3 class="text-xl md:text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2">
             <i class="bi bi-shop-window"></i> Pengaturan Toko
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
             <div class="col-span-1">
                <label class="block font-bold text-gray-700 mb-2">Logo Toko</label>
                <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex flex-col items-center justify-center bg-gray-50 hover:bg-gray-100 transition relative min-h-[160px]">
                   <img v-if="toko.logoBase64" :src="toko.logoBase64" class="h-32 object-contain mb-4"/>
                   <div v-else class="flex flex-col items-center">
                      <i class="bi bi-cloud-arrow-up text-4xl text-gray-400 mb-2"></i>
                   </div>
                   <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"/>
                   <p class="text-sm text-gray-500 font-bold">Klik untuk ganti logo</p>
                </div>
             </div>
             <div class="col-span-1 space-y-4">
                <div>
                   <label class="block font-bold text-sm text-gray-600">Nama Toko</label>
                   <input v-model="toko.namaToko" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
                </div>
                <div>
                   <label class="block font-bold text-sm text-gray-600">No Telp / Footer</label>
                   <input v-model="toko.noTelp" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
                </div>
             </div>
             <div class="col-span-1 md:col-span-2">
                <label class="block font-bold text-sm text-gray-600">Alamat Lengkap</label>
                <textarea v-model="toko.alamatToko" class="w-full border p-2 rounded h-24 focus:ring-2 focus:ring-blue-500 outline-none resize-none"></textarea>
             </div>
          </div>
          <div class="flex justify-end mt-6">
             <button @click="saveToko" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-blue-700 shadow flex items-center gap-2">
                <i class="bi bi-save2"></i> Simpan
             </button>
          </div>
       </div>

       <div v-if="activeTab === 'LAPORAN'" class="max-w-6xl mx-auto bg-white p-4 md:p-8 rounded-lg shadow">
          <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 border-b pb-4 gap-4">
             <h3 class="text-xl md:text-2xl font-bold text-gray-800 flex items-center gap-2">
                <i class="bi bi-journal-text"></i> Laporan
             </h3>
             <button @click="exportPDF" class="bg-red-600 text-white px-4 py-2 rounded shadow font-bold flex items-center gap-2 text-sm">
                <i class="bi bi-file-earmark-pdf-fill"></i> Export PDF
             </button>
          </div>
          
          <div class="bg-gray-50 p-4 rounded border mb-6 grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Dari</label>
                <input type="date" v-model="filterDate.start" class="w-full border p-2 rounded outline-none"/>
             </div>
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Sampai</label>
                <input type="date" v-model="filterDate.end" class="w-full border p-2 rounded outline-none"/>
             </div>
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Urutkan</label>
                <select v-model="sortBy" class="w-full border p-2 rounded bg-white">
                   <option value="newest">Terbaru</option>
                   <option value="oldest">Terlama</option>
                </select>
             </div>
          </div>

          <div class="overflow-x-auto rounded border border-gray-200">
             <table class="w-full text-sm border-collapse min-w-[600px]"> <thead class="bg-gray-800 text-white">
                   <tr>
                      <th class="p-3 text-left">Tanggal</th>
                      <th class="p-3 text-left">No Nota</th>
                      <th class="p-3 text-left">Tipe</th>
                      <th class="p-3 text-left">Status</th>
                      <th class="p-3 text-right">Total</th>
                   </tr>
                </thead>
                <tbody>
                   <tr v-for="nota in filteredLaporan" :key="nota.id" class="border-b hover:bg-gray-50">
                      <td class="p-3">{{ new Date(nota.tanggal).toLocaleDateString('id-ID') }}</td>
                      <td class="p-3 font-mono text-blue-600 font-bold">{{ nota.kodeNota || nota.id }}</td>
                      <td class="p-3"><span class="px-2 py-1 rounded bg-gray-100 text-xs font-bold">{{ nota.tipe }}</span></td>
                      <td class="p-3 font-bold text-xs">{{ nota.status }}</td>
                      <td class="p-3 text-right font-bold">Rp {{ formatRupiah(nota.total) }}</td>
                   </tr>
                </tbody>
             </table>
          </div>
       </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../api';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const activeTab = ref('TOKO');
const users = ref([]);
const laporan = ref([]);
const newKasir = ref({ username: "", password: "" });

// Update Struktur Data Toko (tambah daftarRekening)
const toko = ref({ 
    namaToko: "", 
    alamatToko: "", 
    noTelp: "", 
    logoBase64: null,
    daftarRekening: [] // Array untuk menampung banyak bank
});

// State untuk Filter & Sort
const filterDate = ref({ start: "", end: "" });
const sortBy = ref("newest");

onMounted(() => {
  loadToko();
  loadUsers();
  loadLaporan();
});

// --- Logic Bank (Baru) ---
const addRekening = () => {
    // Menambah object kosong ke array daftarRekening
    if(!toko.value.daftarRekening) toko.value.daftarRekening = [];
    toko.value.daftarRekening.push({
        namaBank: "",
        noRekening: "",
        atasNama: ""
    });
};

const removeRekening = (index) => {
    // Menghapus item berdasarkan index
    toko.value.daftarRekening.splice(index, 1);
};

// --- Computed Logic untuk Filter, Sort & Total ---
const filteredLaporan = computed(() => {
    let data = [...laporan.value];

    // 1. Filter Tanggal
    if (filterDate.value.start) {
        data = data.filter(item => new Date(item.tanggal) >= new Date(filterDate.value.start));
    }
    if (filterDate.value.end) {
        const endDate = new Date(filterDate.value.end);
        endDate.setHours(23, 59, 59, 999);
        data = data.filter(item => new Date(item.tanggal) <= endDate);
    }

    // 2. Sorting
    data.sort((a, b) => {
        if (sortBy.value === 'newest') return new Date(b.tanggal) - new Date(a.tanggal);
        if (sortBy.value === 'oldest') return new Date(a.tanggal) - new Date(b.tanggal);
        if (sortBy.value === 'highPrice') return b.total - a.total;
        if (sortBy.value === 'lowPrice') return a.total - b.total;
        return 0;
    });

    return data;
});

const totalPendapatan = computed(() => {
    return filteredLaporan.value.reduce((sum, item) => sum + (item.total || 0), 0);
});

// --- Logic Export PDF ---
const exportPDF = () => {
    const doc = new jsPDF();
    doc.setFontSize(18);
    doc.text("Laporan Transaksi", 14, 15);
    doc.setFontSize(10);
    doc.text(`Periode: ${filterDate.value.start || 'Awal'} s/d ${filterDate.value.end || 'Akhir'}`, 14, 22);
    doc.text(`Total Pendapatan: Rp ${formatRupiah(totalPendapatan.value)}`, 14, 27);

    const tableColumn = ["Tanggal", "No Nota", "Tipe", "Customer", "Status", "Total"];
    const tableRows = filteredLaporan.value.map(nota => [
        new Date(nota.tanggal).toLocaleDateString('id-ID'),
        nota.kodeNota || nota.id,
        nota.tipe,
        nota.customerNama,
        nota.status,
        `Rp ${formatRupiah(nota.total)}`
    ]);

    autoTable(doc, {
        head: [tableColumn],
        body: tableRows,
        startY: 32,
        theme: 'grid',
        headStyles: { fillColor: [41, 128, 185] },
        foot: [['', '', '', '', 'GRAND TOTAL', `Rp ${formatRupiah(totalPendapatan.value)}`]],
        footStyles: { fillColor: [240, 240, 240], textColor: [0, 0, 0], fontStyle: 'bold' }
    });

    doc.save(`Laporan_Transaksi_${new Date().toISOString().slice(0,10)}.pdf`);
};

// --- Logic Standard ---
const loadToko = async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
    // Pastikan daftarRekening berupa array (jika null dari DB)
    if (!toko.value.daftarRekening) {
        toko.value.daftarRekening = [];
    }
  } catch(e) { console.error("Gagal load toko"); }
};

const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if(file) {
    if(file.size > 2 * 1024 * 1024) { alert("Ukuran file terlalu besar! Max 2MB."); return; }
    const reader = new FileReader();
    reader.onload = (e) => { toko.value.logoBase64 = e.target.result; };
    reader.readAsDataURL(file);
  }
};

const saveToko = async () => {
  try {
    // Data toko.value sudah termasuk daftarRekening
    await api.post('/api/admin/toko', toko.value);
    alert("Pengaturan Toko & Bank Berhasil Disimpan!");
    window.location.reload(); 
  } catch(e) { alert("Gagal Simpan: " + e.message); }
};

const loadUsers = async () => {
  try {
    const res = await api.get('/api/admin/users');
    users.value = res.data;
  } catch(e) {}
};

const addKasir = async () => {
  if(!newKasir.value.username || !newKasir.value.password) return alert("Username & Password wajib diisi!");
  try {
    await api.post('/api/admin/users', { ...newKasir.value, role: 'KASIR' });
    alert("Kasir Ditambahkan!");
    newKasir.value = { username: "", password: "" };
    loadUsers();
  } catch(e) { alert("Gagal: " + (e.response?.data || e.message)); }
};

const deleteUser = async (id) => {
  if(confirm("Yakin ingin menghapus user ini?")) {
    try {
      await api.delete(`/api/admin/users/${id}`);
      loadUsers();
    } catch(e) { alert("Gagal hapus"); }
  }
};

const loadLaporan = async () => {
  try {
    const res = await api.get('/api/nota');
    if(Array.isArray(res.data)) {
      laporan.value = res.data;
    }
  } catch(e) {}
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID').format(v || 0);
</script>