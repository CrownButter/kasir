<template>
  <div class="min-h-screen bg-gray-100 flex flex-col md:flex-row">
    <div class="w-full md:w-64 bg-gray-900 text-white flex flex-col shadow-xl flex-shrink-0">
       <div class="p-4 md:p-6 bg-gray-800 flex justify-between items-center md:block">
          <div>
            <h2 class="text-xl md:text-2xl font-bold tracking-wider flex items-center gap-2">
               <i class="bi bi-hdd-rack-fill"></i> Panel
            </h2>
            <p class="text-gray-400 text-xs mt-1 hidden md:block uppercase">Admin System</p>
          </div>
       </div>

       <nav class="flex md:flex-col overflow-x-auto md:overflow-visible p-2 md:p-4 space-x-2 md:space-x-0 md:space-y-2 bg-gray-900 font-bold uppercase text-xs">
          <button @click="activeTab = 'TOKO'" :class="activeTab === 'TOKO' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-shop h-5 w-5"></i><span>Toko & Bank</span>
          </button>
          <button @click="activeTab = 'KASIR'" :class="activeTab === 'KASIR' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-people-fill h-5 w-5"></i><span>Management Kasir</span>
          </button>
          <button @click="activeTab = 'LAPORAN'" :class="activeTab === 'LAPORAN' ? 'bg-blue-600 shadow-lg' : 'hover:bg-gray-800'" class="whitespace-nowrap w-auto md:w-full text-left p-3 rounded transition flex items-center gap-3">
             <i class="bi bi-file-earmark-bar-graph-fill h-5 w-5"></i><span>Laporan Transaksi</span>
          </button>
       </nav>

       <div class="p-4 border-t border-gray-700 mt-auto">
          <button @click="$router.push('/')" class="w-full bg-red-600 py-2 rounded font-bold hover:bg-red-700 transition flex justify-center items-center gap-2 text-sm">
             <i class="bi bi-arrow-left-circle"></i> Back to POS
          </button>
       </div>
    </div>

    <div class="flex-1 p-4 md:p-10 overflow-y-auto w-full">
       
       <div v-if="activeTab === 'TOKO'" class="max-w-4xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
          <h3 class="text-xl md:text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2 uppercase tracking-wider">
             <i class="bi bi-shop-window"></i> Pengaturan Toko
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
             <div class="col-span-1 text-black">
                <label class="block font-bold text-gray-700 mb-2 uppercase text-xs">Logo Toko</label>
                <div class="border-2 border-dashed border-gray-300 rounded-lg p-6 flex flex-col items-center justify-center bg-gray-50 hover:bg-gray-100 transition relative min-h-[160px] overflow-hidden">
                   <img v-if="previewUrl || toko.logoUrl" :src="previewUrl || getImageUrl(toko.logoUrl)" class="h-32 object-contain mb-4"/>
                   <div v-else class="flex flex-col items-center">
                      <i class="bi bi-cloud-arrow-up text-4xl text-gray-400 mb-2"></i>
                   </div>
                   <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"/>
                   <p class="text-sm text-gray-500 font-bold uppercase text-[10px]">Klik untuk ganti logo</p>
                </div>
             </div>

             <div class="col-span-1 space-y-4">
                <div>
                   <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">Nama Toko</label>
                   <input v-model="toko.namaToko" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold text-black"/>
                </div>
                <div>
                   <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">No Telp / WhatsApp</label>
                   <input v-model="toko.noTelp" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none font-bold text-black"/>
                </div>
             </div>

             <div class="col-span-1 md:col-span-2 text-black">
                <label class="block font-bold text-[10px] text-gray-500 uppercase tracking-widest">Alamat Lengkap</label>
                <textarea v-model="toko.alamatToko" class="w-full border p-2 rounded h-24 focus:ring-2 focus:ring-blue-500 outline-none resize-none font-bold"></textarea>
             </div>
          </div>
          
          <div class="mt-10 border-t pt-6 text-black">
             <div class="flex justify-between items-center mb-4">
                <h4 class="font-bold text-gray-700 uppercase flex items-center gap-2 tracking-widest">
                   <i class="bi bi-credit-card"></i> Rekening Pembayaran
                </h4>
                <button @click="addRekening" class="bg-green-600 text-white px-4 py-1 rounded text-xs font-bold hover:bg-green-700 shadow transition">
                   + Tambah Rekening
                </button>
             </div>
             <div class="space-y-3">
                <div v-for="(bank, index) in toko.daftarRekening" :key="index" class="flex flex-wrap md:flex-nowrap gap-2 bg-gray-50 p-4 rounded-xl border border-gray-200 relative shadow-sm">
                   <div class="w-full md:w-1/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Nama Bank</label>
                       <input v-model="bank.namaBank" placeholder="BCA / BRI / Mandiri" class="w-full border p-2 rounded text-xs font-bold"/>
                   </div>
                   <div class="w-full md:w-2/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Nomor Rekening</label>
                       <input v-model="bank.noRekening" placeholder="XXXX-XXXX-XXXX" class="w-full border p-2 rounded text-xs font-bold font-mono"/>
                   </div>
                   <div class="w-full md:w-1/4">
                       <label class="text-[9px] font-bold text-gray-400 uppercase">Atas Nama</label>
                       <input v-model="bank.atasNama" placeholder="Nama Pemilik" class="w-full border p-2 rounded text-xs font-bold"/>
                   </div>
                   <button @click="removeRekening(index)" class="text-red-500 hover:text-red-700 self-end p-2 mb-0.5">
                      <i class="bi bi-trash-fill"></i>
                   </button>
                </div>
             </div>
          </div>

          <div class="flex justify-end mt-10">
             <button @click="saveToko" class="bg-blue-600 text-white px-10 py-3 rounded-xl font-bold hover:bg-blue-700 shadow-xl flex items-center gap-2 transition transform active:scale-95 uppercase tracking-widest">
                <i class="bi bi-save2"></i> Simpan Pengaturan
             </button>
          </div>
       </div>

       <div v-if="activeTab === 'KASIR'" class="max-w-4xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
          <h3 class="text-xl md:text-2xl font-bold mb-6 border-b pb-4 text-gray-800 flex items-center gap-2 uppercase tracking-wider">
             <i class="bi bi-people-fill"></i> Management Kasir
          </h3>
          
          <div class="bg-gray-50 p-4 rounded border mb-8">
             <h4 class="font-bold text-sm text-gray-600 uppercase mb-3 tracking-widest">Tambah Kasir Baru</h4>
             <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <input v-model="newKasir.username" placeholder="Username" class="border p-2 rounded text-sm font-bold text-black"/>
                <input v-model="newKasir.password" type="password" placeholder="Password" class="border p-2 rounded text-sm font-bold text-black"/>
                <button @click="addKasir" class="bg-blue-600 text-white font-bold rounded py-2 hover:bg-blue-700 transition uppercase text-xs shadow">Tambah Kasir</button>
             </div>
          </div>

          <div class="overflow-x-auto rounded border">
             <table class="w-full text-sm text-left">
                <thead class="bg-gray-800 text-white uppercase text-[10px] tracking-wider">
                   <tr>
                      <th class="p-3">Username</th>
                      <th class="p-3">Role</th>
                      <th class="p-3 text-center">Aksi</th>
                   </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                   <tr v-for="u in users" :key="u.id" class="text-black font-bold">
                      <td class="p-3">{{ u.username }}</td>
                      <td class="p-3">
                        <span class="bg-blue-100 text-blue-800 px-2 py-0.5 rounded text-[10px] uppercase font-black">{{ u.role }}</span>
                      </td>
                      <td class="p-3 text-center">
                         <button v-if="u.role !== 'ADMIN'" @click="deleteUser(u.id)" class="text-red-500 hover:text-red-700 p-1">
                            <i class="bi bi-trash-fill"></i>
                         </button>
                         <span v-else class="text-gray-300 text-xs italic">System</span>
                      </td>
                   </tr>
                </tbody>
             </table>
          </div>
       </div>

       <div v-if="activeTab === 'LAPORAN'" class="max-w-6xl mx-auto bg-white p-6 md:p-8 rounded-lg shadow-lg">
          <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 border-b pb-4 gap-4">
             <h3 class="text-xl md:text-2xl font-bold text-gray-800 flex items-center gap-2 uppercase tracking-wider">
                <i class="bi bi-journal-text"></i> Laporan Transaksi
             </h3>
             <button @click="exportPDF" class="bg-red-600 text-white px-6 py-2 rounded-lg shadow font-bold flex items-center gap-2 transition hover:bg-red-700 uppercase text-xs tracking-widest">
                <i class="bi bi-file-earmark-pdf-fill"></i> Export PDF
             </button>
          </div>

          <div class="bg-gray-50 p-4 rounded-xl border mb-6 grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
             <div>
                <label class="text-[10px] font-bold text-gray-400 uppercase block mb-1 tracking-widest">Dari Tanggal</label>
                <input type="date" v-model="filterDate.start" class="w-full border p-2 rounded-lg text-black font-bold outline-none focus:ring-2 focus:ring-blue-500"/>
             </div>
             <div>
                <label class="text-[10px] font-bold text-gray-400 uppercase block mb-1 tracking-widest">Sampai Tanggal</label>
                <input type="date" v-model="filterDate.end" class="w-full border p-2 rounded-lg text-black font-bold outline-none focus:ring-2 focus:ring-blue-500"/>
             </div>
             <div>
                <label class="text-[10px] font-bold text-gray-400 uppercase block mb-1 tracking-widest">Urutkan</label>
                <select v-model="sortBy" class="w-full border p-2 rounded-lg bg-white text-black font-bold outline-none">
                   <option value="newest">Terbaru</option>
                   <option value="oldest">Terlama</option>
                </select>
             </div>
             <div class="bg-blue-600 text-white p-3 rounded-xl shadow-lg border border-blue-400">
                <p class="text-[9px] font-bold uppercase opacity-80 tracking-widest">Total Pendapatan</p>
                <p class="text-lg font-black font-mono">Rp {{ formatRupiah(totalPendapatan) }}</p>
             </div>
          </div>

          <div class="overflow-x-auto rounded-xl border text-black">
             <table class="w-full text-xs border-collapse">
                <thead class="bg-gray-800 text-white uppercase font-bold tracking-wider text-[10px]">
                   <tr>
                      <th class="p-3 text-left">Tanggal</th>
                      <th class="p-3 text-left">No Nota</th>
                      <th class="p-3 text-left">Tipe</th>
                      <th class="p-3 text-left">Customer</th>
                      <th class="p-3 text-right">Total Tagihan</th>
                   </tr>
                </thead>
                <tbody class="font-bold">
                   <tr v-for="nota in filteredLaporan" :key="nota.id" class="border-b hover:bg-blue-50 transition-colors">
                      <td class="p-3">{{ new Date(nota.tanggal).toLocaleDateString('id-ID') }}</td>
                      <td class="p-3 font-mono text-blue-600 uppercase">{{ nota.kodeNota || nota.id }}</td>
                      <td class="p-3">
                        <span :class="nota.tipe === 'SERVICE' ? 'bg-orange-100 text-orange-700' : 'bg-blue-100 text-blue-700'" class="px-2 py-0.5 rounded text-[9px] font-black uppercase">{{ nota.tipe }}</span>
                      </td>
                      <td class="p-3 uppercase">{{ nota.customerNama }}</td>
                      <td class="p-3 text-right font-black text-gray-800">Rp {{ formatRupiah(nota.total) }}</td>
                   </tr>
                   <tr v-if="filteredLaporan.length === 0">
                      <td colspan="5" class="p-12 text-center text-gray-400 italic font-bold text-sm">Data transaksi tidak ditemukan.</td>
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
import api, { getImageUrl } from '../api';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const activeTab = ref('TOKO');
const users = ref([]);
const laporan = ref([]);
const newKasir = ref({ username: "", password: "" });
const filterDate = ref({ start: "", end: "" });
const sortBy = ref("newest");

const toko = ref({ namaToko: "", alamatToko: "", noTelp: "", logoUrl: null, daftarRekening: [] });
const selectedFile = ref(null);
const previewUrl = ref(null);

onMounted(() => { 
  loadToko(); 
  loadUsers();
  loadLaporan();
});

// --- LOGIKA PENGATURAN TOKO ---
const loadToko = async () => {
  try {
    const res = await api.get('/api/admin/toko');
    toko.value = res.data;
    if (!toko.value.daftarRekening) toko.value.daftarRekening = [];
  } catch(e) { console.error("Gagal load toko"); }
};

const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if(file) {
    if(file.size > 2 * 1024 * 1024) return alert("Maksimal file 2MB!");
    selectedFile.value = file;
    previewUrl.value = URL.createObjectURL(file);
  }
};

const saveToko = async () => {
  try {
    const formData = new FormData();
    const storeData = { ...toko.value };
    delete storeData.logoUrl; // Hapus URL lama dari JSON agar tidak konflik
    
    formData.append('toko', JSON.stringify(storeData));
    if (selectedFile.value) formData.append('logo', selectedFile.value);

    await api.post('/api/admin/toko', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    alert("Pengaturan Berhasil Disimpan!");
    window.location.reload(); 
  } catch(e) { 
    alert("Gagal Simpan: " + (e.response?.data?.message || e.message)); 
  }
};

const addRekening = () => { toko.value.daftarRekening.push({ namaBank: "", noRekening: "", atasNama: "" }); };
const removeRekening = (index) => { toko.value.daftarRekening.splice(index, 1); };

// --- LOGIKA MANAGEMENT KASIR ---
const loadUsers = async () => {
  try {
    const res = await api.get('/api/admin/users');
    users.value = res.data;
  } catch(e) { console.error("Gagal load users"); }
};

const addKasir = async () => {
  if(!newKasir.value.username || !newKasir.value.password) return alert("Isi Username & Password!");
  try {
    await api.post('/api/admin/users', { ...newKasir.value, role: 'KASIR' });
    alert("Kasir Baru Berhasil Ditambahkan!");
    newKasir.value = { username: "", password: "" };
    loadUsers();
  } catch(e) { alert("Gagal tambah kasir"); }
};

const deleteUser = async (id) => {
  if(confirm("Yakin ingin menghapus akun kasir ini?")) {
    try {
      await api.delete(`/api/admin/users/${id}`);
      loadUsers();
    } catch(e) { alert("Gagal hapus kasir"); }
  }
};

// --- LOGIKA LAPORAN TRANSAKSI ---
const loadLaporan = async () => {
  try {
    const res = await api.get('/api/nota');
    if(Array.isArray(res.data)) laporan.value = res.data;
  } catch(e) { console.error("Gagal load laporan"); }
};

const filteredLaporan = computed(() => {
    let data = [...laporan.value];
    if (filterDate.value.start) data = data.filter(i => new Date(i.tanggal) >= new Date(filterDate.value.start));
    if (filterDate.value.end) {
        const end = new Date(filterDate.value.end);
        end.setHours(23, 59, 59, 999);
        data = data.filter(i => new Date(i.tanggal) <= end);
    }
    data.sort((a, b) => sortBy.value === 'newest' ? new Date(b.tanggal) - new Date(a.tanggal) : new Date(a.tanggal) - new Date(b.tanggal));
    return data;
});

const totalPendapatan = computed(() => filteredLaporan.value.reduce((sum, item) => sum + (item.total || 0), 0));

const exportPDF = () => {
    const doc = new jsPDF();
    doc.setFontSize(18);
    doc.text("Laporan Pendapatan Kasir", 14, 15);
    doc.setFontSize(10);
    doc.text(`Dicetak pada: ${new Date().toLocaleString('id-ID')}`, 14, 22);

    const tableRows = filteredLaporan.value.map(nota => [
        new Date(nota.tanggal).toLocaleDateString('id-ID'),
        nota.kodeNota || nota.id,
        nota.tipe,
        nota.customerNama,
        `Rp ${formatRupiah(nota.total)}`
    ]);

    autoTable(doc, {
        head: [["Tanggal", "No Nota", "Tipe", "Customer", "Total"]],
        body: tableRows,
        startY: 30,
        theme: 'grid',
        headStyles: { fillColor: [41, 128, 185], fontStyle: 'bold' },
        foot: [[ "", "", "", "GRAND TOTAL", `Rp ${formatRupiah(totalPendapatan.value)}` ]],
        footStyles: { fillColor: [241, 241, 241], textColor: [0, 0, 0], fontStyle: 'bold' }
    });

    doc.save(`Laporan_POS_${new Date().toISOString().slice(0,10)}.pdf`);
};

const formatRupiah = (v) => new Intl.NumberFormat('id-ID').format(v || 0);
</script>

<style scoped>
/* Styling tambahan untuk sidebar aktif agar lebih tegas */
button.bg-blue-600 {
  border-left: 4px solid white;
}
</style>