<template>
  <div class="min-h-screen bg-gray-100 flex">
    <div class="w-64 bg-gray-900 text-white flex flex-col shadow-xl">
       <div class="p-6 bg-gray-800">
          <h2 class="text-2xl font-bold tracking-wider flex items-center gap-2">
             <i class="bi bi-hdd-rack-fill"></i> Admin Panel
          </h2>
          <p class="text-gray-400 text-xs mt-1">Dwin Computer System</p>
       </div>
       <nav class="flex-1 p-4 space-y-2">
          <button @click="activeTab = 'TOKO'" :class="activeTab === 'TOKO' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-shop h-5 w-5"></i> <span>Identitas Toko</span>
          </button>
          <button @click="activeTab = 'KASIR'" :class="activeTab === 'KASIR' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-people-fill h-5 w-5"></i> <span>Data Kasir</span>
          </button>
          <button @click="activeTab = 'LAPORAN'" :class="activeTab === 'LAPORAN' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-file-earmark-bar-graph-fill h-5 w-5"></i> <span>Laporan</span>
          </button>
       </nav>
       <div class="p-4 border-t border-gray-700">
          <button @click="$router.push('/')" class="w-full bg-red-600 py-2 rounded font-bold hover:bg-red-700 transition flex justify-center items-center gap-2">
             <i class="bi bi-arrow-left-circle"></i> Kembali ke POS
          </button>
       </div>
    </div>

    <div class="flex-1 p-10 overflow-y-auto">
       
       <div v-if="activeTab === 'TOKO'" class="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow">
          <h3 class="text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2">
             <i class="bi bi-shop-window"></i> Pengaturan Identitas Toko
          </h3>
          <div class="space-y-6">
             <div class="grid grid-cols-2 gap-6">
                <div class="col-span-2 md:col-span-1">
                   <label class="block font-bold text-gray-700 mb-2">Logo Toko</label>
                   <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex flex-col items-center justify-center bg-gray-50 hover:bg-gray-100 transition relative min-h-[160px]">
                      <img v-if="toko.logoBase64" :src="toko.logoBase64" class="h-32 object-contain mb-4"/>
                      <div v-else class="flex flex-col items-center">
                         <i class="bi bi-cloud-arrow-up text-4xl text-gray-400 mb-2"></i>
                      </div>
                      <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"/>
                      <p class="text-sm text-gray-500 font-bold">Klik untuk ganti logo</p>
                      <p class="text-xs text-gray-400">(Format: PNG/JPG, Max 2MB)</p>
                   </div>
                </div>
                <div class="col-span-2 md:col-span-1 space-y-4">
                   <div>
                      <label class="block font-bold text-sm text-gray-600">Nama Toko</label>
                      <input v-model="toko.namaToko" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
                   </div>
                   <div>
                      <label class="block font-bold text-sm text-gray-600">No Telp / Footer</label>
                      <input v-model="toko.noTelp" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"/>
                   </div>
                </div>
             </div>
             <div>
                <label class="block font-bold text-sm text-gray-600">Alamat Lengkap</label>
                <textarea v-model="toko.alamatToko" class="w-full border p-2 rounded h-24 focus:ring-2 focus:ring-blue-500 outline-none resize-none"></textarea>
             </div>
             <div class="flex justify-end">
                <button @click="saveToko" class="bg-blue-600 text-white px-8 py-3 rounded-lg font-bold hover:bg-blue-700 shadow-lg transform transition active:scale-95 flex items-center gap-2">
                   <i class="bi bi-save2"></i> Simpan Perubahan
                </button>
             </div>
          </div>
       </div>

       <div v-if="activeTab === 'KASIR'" class="max-w-4xl mx-auto bg-white p-8 rounded-lg shadow">
          <h3 class="text-2xl font-bold mb-6 text-gray-800 flex items-center gap-2">
             <i class="bi bi-person-badge-fill"></i> Manajemen Kasir
          </h3>
          <div class="bg-gray-50 p-4 rounded-lg border mb-6 flex gap-4 items-end">
             <div class="flex-1">
                <label class="text-xs font-bold text-gray-500">Username Baru</label>
                <input v-model="newKasir.username" placeholder="user_kasir" class="w-full border p-2 rounded font-bold"/>
             </div>
             <div class="flex-1">
                <label class="text-xs font-bold text-gray-500">Password</label>
                <input v-model="newKasir.password" placeholder="*****" type="password" class="w-full border p-2 rounded"/>
             </div>
             <button @click="addKasir" class="bg-green-600 text-white px-6 py-2 rounded font-bold hover:bg-green-700 h-10 flex items-center gap-2">
                <i class="bi bi-person-plus-fill"></i> Tambah
             </button>
          </div>
          <table class="w-full border-collapse">
             <thead class="bg-gray-800 text-white">
                <tr>
                   <th class="p-3 text-left rounded-tl-lg">ID</th>
                   <th class="p-3 text-left">Username</th>
                   <th class="p-3 text-left">Role</th>
                   <th class="p-3 text-center rounded-tr-lg">Aksi</th>
                </tr>
             </thead>
             <tbody>
                <tr v-for="u in users" :key="u.id" class="border-b hover:bg-gray-50">
                   <td class="p-3 text-gray-500">#{{ u.id }}</td>
                   <td class="p-3 font-bold">{{ u.username }}</td>
                   <td class="p-3">
                      <span :class="u.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'" class="px-2 py-1 rounded text-xs font-bold">
                         {{ u.role }}
                      </span>
                   </td>
                   <td class="p-3 text-center">
                      <button v-if="u.role !== 'ADMIN'" @click="deleteUser(u.id)" class="text-red-500 font-bold hover:bg-red-50 px-3 py-1 rounded border border-red-200 flex items-center gap-1 mx-auto">
                         <i class="bi bi-trash"></i> Hapus
                      </button>
                   </td>
                </tr>
             </tbody>
          </table>
       </div>

       <div v-if="activeTab === 'LAPORAN'" class="max-w-6xl mx-auto bg-white p-8 rounded-lg shadow">
          <div class="flex justify-between items-center mb-6 border-b pb-4">
            <h3 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
                <i class="bi bi-journal-text"></i> Laporan Transaksi
            </h3>
            <button @click="exportPDF" class="bg-red-600 text-white px-4 py-2 rounded shadow hover:bg-red-700 font-bold flex items-center gap-2">
                <i class="bi bi-file-earmark-pdf-fill"></i> Export PDF
            </button>
          </div>

          <div class="bg-gray-50 p-4 rounded border mb-6 grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Dari Tanggal</label>
                <input type="date" v-model="filterDate.start" class="w-full border p-2 rounded outline-none focus:ring-2 focus:ring-blue-400"/>
             </div>
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Sampai Tanggal</label>
                <input type="date" v-model="filterDate.end" class="w-full border p-2 rounded outline-none focus:ring-2 focus:ring-blue-400"/>
             </div>
             <div>
                <label class="text-xs font-bold text-gray-500 uppercase block mb-1">Urutkan</label>
                <select v-model="sortBy" class="w-full border p-2 rounded outline-none focus:ring-2 focus:ring-blue-400 bg-white">
                    <option value="newest">Terbaru</option>
                    <option value="oldest">Terlama</option>
                    <option value="highPrice">Total Tertinggi</option>
                    <option value="lowPrice">Total Terendah</option>
                </select>
             </div>
             <div class="flex items-center h-full pb-2">
                <span class="text-sm font-bold text-gray-600">Total: <span class="text-blue-600">{{ filteredLaporan.length }} Data</span></span>
             </div>
          </div>

          <table class="w-full text-sm border-collapse">
             <thead class="bg-gray-800 text-white">
                <tr>
                   <th class="p-3 text-left">Tanggal</th>
                   <th class="p-3 text-left">No Nota</th>
                   <th class="p-3 text-left">Tipe</th>
                   <th class="p-3 text-left">Status</th>
                   <th class="p-3 text-left">Customer</th>
                   <th class="p-3 text-right">Total</th>
                </tr>
             </thead>
             <tbody>
                <tr v-if="filteredLaporan.length === 0">
                    <td colspan="6" class="p-6 text-center text-gray-500 italic">Tidak ada data yang sesuai filter.</td>
                </tr>
                <tr v-for="nota in filteredLaporan" :key="nota.id" class="border-b hover:bg-gray-50">
                   <td class="p-3">{{ new Date(nota.tanggal).toLocaleDateString('id-ID') }}</td>
                   <td class="p-3 font-mono text-blue-600 font-bold">{{ nota.kodeNota || nota.id }}</td>
                   <td class="p-3"><span class="px-2 py-1 rounded bg-gray-100 text-xs font-bold">{{ nota.tipe }}</span></td>
                   <td class="p-3 font-bold text-xs">{{ nota.status }}</td>
                   <td class="p-3">{{ nota.customerNama }}</td>
                   <td class="p-3 text-right font-bold">Rp {{ formatRupiah(nota.total) }}</td>
                </tr>
             </tbody>
             <tfoot v-if="filteredLaporan.length > 0">
                 <tr class="bg-gray-100 font-bold">
                     <td colspan="5" class="p-3 text-right">GRAND TOTAL</td>
                     <td class="p-3 text-right text-blue-700">Rp {{ formatRupiah(totalPendapatan) }}</td>
                 </tr>
             </tfoot>
          </table>
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
const toko = ref({ namaToko: "", alamatToko: "", noTelp: "", logoBase64: null });

// State untuk Filter & Sort
const filterDate = ref({ start: "", end: "" });
const sortBy = ref("newest");

onMounted(() => {
  loadToko();
  loadUsers();
  loadLaporan();
});

// --- Computed Logic untuk Filter, Sort & Total ---
const filteredLaporan = computed(() => {
    let data = [...laporan.value];

    // 1. Filter Tanggal
    if (filterDate.value.start) {
        data = data.filter(item => new Date(item.tanggal) >= new Date(filterDate.value.start));
    }
    if (filterDate.value.end) {
        // Set end date to end of day to include the day itself
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
    
    // Judul PDF
    doc.setFontSize(18);
    doc.text("Laporan Transaksi", 14, 15);
    
    doc.setFontSize(10);
    doc.text(`Periode: ${filterDate.value.start || 'Awal'} s/d ${filterDate.value.end || 'Akhir'}`, 14, 22);
    doc.text(`Total Pendapatan: Rp ${formatRupiah(totalPendapatan.value)}`, 14, 27);

    // Tabel
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
        headStyles: { fillColor: [41, 128, 185] }, // Warna Biru Header
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
    await api.post('/api/admin/toko', toko.value);
    alert("Pengaturan Toko Berhasil Disimpan!");
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
      laporan.value = res.data; // Tidak perlu sort disini, karena sudah di computed
    }
  } catch(e) {}
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID').format(v || 0);
</script>