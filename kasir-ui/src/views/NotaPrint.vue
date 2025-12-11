<template>
  <div v-if="nota" class="min-h-screen bg-gray-500 flex items-center justify-center p-4">
    
    <div id="printable-area" class="bg-white p-6 w-full max-w-5xl shadow-2xl relative font-sans text-sm text-black">
      
      <div class="flex justify-between items-start border-b-2 border-black pb-2 mb-2">
        <div class="w-1/4 flex flex-col">
           <img v-if="toko.logoBase64" :src="toko.logoBase64" class="h-16 object-contain self-start grayscale"/>
           <h2 v-else class="text-2xl font-bold text-black uppercase">{{ toko.namaToko || 'NAMA TOKO' }}</h2>
           <div class="mt-1 text-black font-bold text-xs">
               NO: {{ nota.kodeNota || nota.id }}
           </div>
        </div>
        
        <div class="w-2/4 text-center px-2">
           <p class="text-black font-bold text-base leading-tight uppercase">{{ toko.alamatToko || 'Alamat Toko Belum Diisi' }}</p>
           <p class="text-black font-bold mt-1">Telp: {{ toko.noTelp || '-' }}</p>
        </div>

        <div class="w-1/4 text-xs font-bold text-black pl-4">
           <div class="grid grid-cols-[70px_1fr] gap-y-1">
               <span class="text-right pr-2">Tgl :</span>
               <span>{{ formatDate(nota.tanggal) }}</span>
               <span class="text-right pr-2">Nama :</span>
               <span class="uppercase">{{ nota.customerNama }}</span>
               <span class="text-right pr-2">Telp :</span>
               <span>{{ nota.customerTelp }}</span>
           </div>
        </div>
      </div>

      <div class="text-right text-black italic text-xs mb-1 font-bold uppercase">
          NOTA {{ nota.tipe === 'SERVICE' ? 'SERVICE' : 'PENJUALAN' }}
      </div>

      <table class="w-full border-collapse mb-2 text-[11px] font-mono">
         <thead>
           <tr class="text-black border-t border-b border-black border-dashed">
             <th class="py-1 border-r border-black border-dashed w-6 text-center">No</th>
             
             <th class="py-1 border-r border-black border-dashed text-left px-2 w-[20%]">Nama/Jenis Barang</th>
             
             <th class="py-1 border-r border-black border-dashed text-left px-2 w-[20%]">Status/Kerusakan</th>
             
             <th class="py-1 border-r border-black border-dashed w-8 text-center">QTY</th>
             
             <th class="py-1 border-r border-black border-dashed text-left px-2 w-[25%]">Solusi</th>
             
             <th class="py-1 px-0 w-[25%]">
                 <div class="border-b border-black border-dashed pb-1 text-center font-bold">Harga</div>
                 <div class="flex pt-1">
                     <div class="w-1/2 text-center border-r border-black border-dashed">Satuan</div>
                     <div class="w-1/2 text-center">Jumlah</div>
                 </div>
             </th>
           </tr>
         </thead>
         
         <tbody class="text-black font-semibold">
           <tr v-for="(item, i) in (nota.items || nota.snapshots)" :key="i" class="border-b border-black border-dashed">
             
             <td class="py-1 border-r border-black border-dashed text-center valign-top">
                 {{ i + 1 }}
             </td>
             
             <td class="py-1 border-r border-black border-dashed px-2 valign-top font-bold">
                 <span v-if="nota.tipe === 'SERVICE'">
                     {{ shouldShowUnit(i, (nota.items || nota.snapshots)) ? extract(item.catatan, 'Unit') : '' }}
                 </span>
                 <span v-else>
                     {{ item.namaBarang }} </span>
             </td>

             <td class="py-1 border-r border-black border-dashed px-2 valign-top">
                 <span v-if="nota.tipe === 'SERVICE'">
                     {{ shouldShowUnit(i, (nota.items || nota.snapshots)) ? extract(item.catatan, 'Status') : '' }}
                 </span>
                 <span v-else>-</span>
             </td>

             <td class="py-1 border-r border-black border-dashed text-center valign-top">{{ item.jumlah }}</td>

             <td class="py-1 border-r border-black border-dashed px-2 valign-top">
                 <div v-if="nota.tipe === 'SERVICE'">
                     {{ item.namaBarang }}
                     <div v-if="extract(item.catatan, 'Tindakan')" class="text-[9px] italic">
                         ({{ extract(item.catatan, 'Tindakan') }})
                     </div>
                 </div>
                 <div v-else>
                     {{ item.catatan || '-' }}
                 </div>
             </td>
             
             <td class="py-1 px-0 valign-top">
                 <div class="flex">
                     <div class="w-1/2 text-right px-2 border-r border-black border-dashed">
                         Rp {{ formatNumber(item.hargaSatuan) }}
                     </div>
                     <div class="w-1/2 text-right px-2">
                         Rp {{ formatNumber(item.totalHarga || (item.hargaSatuan * item.jumlah)) }}
                     </div>
                 </div>
             </td>
           </tr>
         </tbody>
      </table>

      <div class="flex mb-4">
          <div class="w-2/3 pr-4 text-xs font-bold space-y-1">
              <div>Teknisi: {{ toko.kasirNama || nota.kasirNama || 'Admin' }}</div>
              
              <div v-if="toko.daftarRekening && toko.daftarRekening.length > 0" class="mt-2">
                  <div v-for="(bank, index) in toko.daftarRekening" :key="index" class="uppercase">
                      TRANSFER: {{ bank.namaBank }} - {{ bank.noRekening }}
                  </div>
                  <div class="uppercase">A.N : {{ toko.daftarRekening[0].atasNama }}</div>
              </div>
          </div>

          <div class="w-1/3">
              <div class="flex justify-between border-b border-black border-dashed font-bold text-sm pb-1">
                  <span>Total</span>
                  <span>Rp {{ formatNumber(nota.total) }}</span>
              </div>
              <div class="flex justify-between border-b border-black border-dashed font-bold text-sm py-1">
                  <span>DP</span>
                  <span>
                      {{ nota.dp > 0 ? 'Rp ' + formatNumber(nota.dp) : '-' }}
                  </span>
              </div>
              <div class="flex justify-between font-bold text-sm py-1">
                  <span>Sisa</span>
                  <span>Rp {{ formatNumber(nota.sisa) }}</span>
              </div>
          </div>
      </div>

      <div class="flex items-start justify-between mt-2 break-inside-avoid">
          <div class="text-center w-1/4 relative mt-4">
              <p class="font-bold mb-12 text-xs">Penerima</p>
              <div class="flex justify-center px-2">
                  <span class="font-bold border-b border-black border-dashed pb-1 text-xs uppercase px-2 w-full block">
                      ( {{ nota.customerNama }} )
                  </span>
              </div>
          </div>

          <div class="w-2/4 px-4">
              <div class="border border-black border-dashed p-2 text-[10px] text-center leading-tight font-bold">
                  <p class="mb-1 text-xs">"PERHATIAN"</p>
                  <p>Harap tunjukkan Nota ini saat pengambilan.</p>
                  <p>Garansi sesuai kerusakan yang sama (Non-Sparepart).</p>
                  <p>Kami tidak bertanggung jawab atas kehilangan data.</p>
                  <p>Barang tidak diambil > 1 bulan diluar tanggung jawab kami.</p>
              </div>
          </div>

          <div class="text-center w-1/4 relative mt-4">
              <p class="font-bold mb-12 text-xs">Hormat Kami</p>
              <div class="flex justify-center px-2">
                  <span class="font-bold border-b border-black border-dashed pb-1 text-xs uppercase px-2 w-full block">
                      ( {{ toko.kasirNama || nota.kasirNama || 'Admin' }} )
                  </span>
              </div>
          </div>
      </div>

      <div class="flex justify-end gap-4 border-t border-gray-300 pt-6 mt-8 no-print">
          <button @click="$emit('close')" class="bg-gray-500 text-white px-6 py-2 rounded font-bold hover:bg-gray-600 flex items-center gap-2">
              <i class="bi bi-x-circle"></i> Tutup
          </button>
          <button @click="printNow" class="bg-blue-600 text-white px-8 py-2 rounded font-bold hover:bg-blue-700 flex items-center gap-2 shadow-lg">
              <i class="bi bi-printer-fill"></i> CETAK
          </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';

const props = defineProps(['notaId']);
const emit = defineEmits(['close']);
const nota = ref(null);
const toko = ref({});

onMounted(async () => {
   try {
     const resToko = await api.get('/api/admin/toko');
     toko.value = resToko.data;

     if(!props.notaId) return;
     const res = await api.get(`/api/nota/${props.notaId}`);
     nota.value = res.data;
   } catch(e) {
     alert("Gagal memuat data!");
     emit('close');
   }
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n || 0);

const formatDate = (d) => {
    if(!d) return '-';
    return new Date(d).toLocaleDateString('id-ID', {day: 'numeric', month:'short', year:'numeric'});
};

// --- LOGIC PARSING ---

// Ambil value dari format string "Key: Value | Key2: Value2"
const extract = (text, key) => {
    if(!text) return '';
    const regex = new RegExp(`${key}:\\s*([^|]+)`, 'i');
    const match = text.match(regex);
    return match ? match[1].trim() : '';
};

// Logic agar Nama Unit & Kerusakan hanya muncul di baris pertama untuk grup unit yang sama
// (Persis seperti contoh gambar legacy)
const shouldShowUnit = (index, items) => {
    if (index === 0) return true;
    
    const currentUnit = extract(items[index].catatan, 'Unit');
    const prevUnit = extract(items[index - 1].catatan, 'Unit');
    
    // Tampilkan jika Unit baris ini berbeda dengan baris sebelumnya
    return currentUnit !== prevUnit;
};

const printNow = () => window.print();
</script>

<style>
/* CSS KHUSUS PRINT EPSON LX-310 (DOT MATRIX) */
@media print {
    @page { size: 9.5in 11in; margin: 0; }
    html, body { margin: 0 !important; padding: 0 !important; background: white !important; }
    body * { visibility: hidden; } 
    
    #printable-area, #printable-area * {
        visibility: visible;
        color: black !important;
    }
    
    #printable-area {
        position: absolute;
        left: 0;
        top: 0;
        width: 8.5in !important;
        margin-left: 0.1in !important;
        margin-top: 0.1in !important;
        padding: 0 !important;
        font-family: 'Courier New', Courier, monospace !important;
        font-size: 10pt !important;
        line-height: 1.1 !important;
        background-color: white !important;
        box-shadow: none !important;
        border: none !important;
    }

    .border-b-2, .border-t-2, .border, .border-r, .border-b, .border-t {
        border-width: 1px !important;
        border-color: black !important;
        border-style: dashed !important;
    }

    .valign-top { vertical-align: top; }
    .no-print { display: none !important; }
}
</style>