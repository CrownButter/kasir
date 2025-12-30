export default {
  install: (app) => {
    app.config.globalProperties.$can = (permissionName) => {
      // 1. Ambil Role dan Permissions dari LocalStorage
      const role = localStorage.getItem('role');
      const permissionsStr = localStorage.getItem('permissions');
      
      // Jaga-jaga kalau kosong, anggap array kosong
      const permissions = permissionsStr ? JSON.parse(permissionsStr) : [];

      // 2. Logic Pengecekan
      // Jika ADMIN, selalu boleh (True)
      if (role === 'ADMIN') return true;

      // Jika bukan Admin, cek apakah punya izin tersebut
      return permissions.includes(permissionName);
    };
  }
};