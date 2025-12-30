<template>
  <div class="container mx-auto p-6 max-w-7xl min-h-screen">
    <div class="flex flex-col md:flex-row justify-between items-center mb-10">
      <div>
        <h1 class="text-4xl font-black text-gray-800 tracking-tight">Admin Panel</h1>
        <p class="text-gray-500 mt-2">Pusat kontrol manajemen DWIN Computer.</p>
      </div>
      <div class="mt-4 md:mt-0 bg-blue-50 px-4 py-2 rounded-lg border border-blue-100 text-blue-800 font-bold">
        <i class="bi bi-person-badge mr-2"></i> Mode Administrator
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">

      <router-link to="/laporan" class="group relative bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 overflow-hidden cursor-pointer">
        <div class="absolute right-0 top-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity transform group-hover:scale-110 duration-500">
           <i class="bi bi-graph-up-arrow text-9xl text-blue-600"></i>
        </div>
        <div class="relative z-10">
          <div class="w-14 h-14 bg-gradient-to-br from-blue-500 to-blue-700 rounded-xl flex items-center justify-center text-white shadow-lg mb-6 group-hover:scale-110 transition-transform">
            <i class="bi bi-file-earmark-bar-graph-fill text-2xl"></i>
          </div>
          <h3 class="text-2xl font-black text-gray-800 mb-2">Laporan Penjualan</h3>
          <p class="text-gray-500 text-sm leading-relaxed mb-6">
            Lihat grafik omzet, rekapitulasi nota, laba bersih, dan filter transaksi per tanggal.
          </p>
          <div class="flex items-center text-blue-600 font-bold tracking-wide text-sm">
            BUKA LAPORAN <i class="bi bi-arrow-right ml-2 group-hover:translate-x-2 transition-transform"></i>
          </div>
        </div>
      </router-link>

      <router-link to="/inventory" class="group relative bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 overflow-hidden cursor-pointer">
        <div class="absolute right-0 top-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity transform group-hover:scale-110 duration-500">
           <i class="bi bi-box-seam text-9xl text-emerald-600"></i>
        </div>
        <div class="relative z-10">
          <div class="w-14 h-14 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-xl flex items-center justify-center text-white shadow-lg mb-6 group-hover:scale-110 transition-transform">
            <i class="bi bi-hdd-rack-fill text-2xl"></i>
          </div>
          <h3 class="text-2xl font-black text-gray-800 mb-2">Manajemen Stok</h3>
          <p class="text-gray-500 text-sm leading-relaxed mb-6">
            Tambah barang baru, update stok fisik, edit harga modal/jual, dan hapus item.
          </p>
          <div class="flex items-center text-emerald-600 font-bold tracking-wide text-sm">
            KELOLA BARANG <i class="bi bi-arrow-right ml-2 group-hover:translate-x-2 transition-transform"></i>
          </div>
        </div>
      </router-link>

      <router-link to="/" class="group relative bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 overflow-hidden cursor-pointer">
         <div class="absolute right-0 top-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity transform group-hover:scale-110 duration-500">
           <i class="bi bi-cart text-9xl text-purple-600"></i>
        </div>
        <div class="relative z-10">
          <div class="w-14 h-14 bg-gradient-to-br from-purple-500 to-purple-700 rounded-xl flex items-center justify-center text-white shadow-lg mb-6 group-hover:scale-110 transition-transform">
            <i class="bi bi-calculator-fill text-2xl"></i>
          </div>
          <h3 class="text-2xl font-black text-gray-800 mb-2">Menu Kasir</h3>
          <p class="text-gray-500 text-sm leading-relaxed mb-6">
            Kembali ke halaman input transaksi untuk melayani pelanggan.
          </p>
          <div class="flex items-center text-purple-600 font-bold tracking-wide text-sm">
            BUKA KASIR <i class="bi bi-arrow-right ml-2 group-hover:translate-x-2 transition-transform"></i>
          </div>
        </div>
      </router-link>

       <router-link to="/riwayat" class="group relative bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 overflow-hidden cursor-pointer">
         <div class="absolute right-0 top-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity transform group-hover:scale-110 duration-500">
           <i class="bi bi-clock-history text-9xl text-amber-600"></i>
        </div>
        <div class="relative z-10">
          <div class="w-14 h-14 bg-gradient-to-br from-amber-500 to-amber-700 rounded-xl flex items-center justify-center text-white shadow-lg mb-6 group-hover:scale-110 transition-transform">
            <i class="bi bi-journal-text text-2xl"></i>
          </div>
          <h3 class="text-2xl font-black text-gray-800 mb-2">Riwayat Transaksi</h3>
          <p class="text-gray-500 text-sm leading-relaxed mb-6">
            Lihat daftar riwayat transaksi kasir tanpa detail analitik keuangan.
          </p>
          <div class="flex items-center text-amber-600 font-bold tracking-wide text-sm">
            CEK RIWAYAT <i class="bi bi-arrow-right ml-2 group-hover:translate-x-2 transition-transform"></i>
          </div>
        </div>
      </router-link>
      <router-link to="/admin/users" class="group relative bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 overflow-hidden cursor-pointer">
         <div class="absolute right-0 top-0 p-4 opacity-5 group-hover:opacity-10 transition-opacity transform group-hover:scale-110 duration-500">
           <i class="bi bi-people-fill text-9xl text-slate-600"></i>
        </div>
        <div class="relative z-10">
          <div class="w-14 h-14 bg-gradient-to-br from-slate-600 to-slate-800 rounded-xl flex items-center justify-center text-white shadow-lg mb-6 group-hover:scale-110 transition-transform">
            <i class="bi bi-person-gear text-2xl"></i>
          </div>
          <h3 class="text-2xl font-black text-gray-800 mb-2">Manajemen User</h3>
          <p class="text-gray-500 text-sm leading-relaxed mb-6">
            Kelola akun kasir dan admin. Tambah karyawan baru, reset password, atau hapus akses.
          </p>
          <div class="flex items-center text-slate-600 font-bold tracking-wide text-sm">
            KELOLA USER <i class="bi bi-arrow-right ml-2 group-hover:translate-x-2 transition-transform"></i>
          </div>
        </div>
      </router-link>

    </div>
  </div>
</template>

<script setup>
</script>