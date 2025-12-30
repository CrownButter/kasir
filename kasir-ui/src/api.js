import axios from 'axios';

// 1. KONFIGURASI URL
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * Helper untuk mendapatkan URL Gambar dari backend
 * Menangani path yang disimpan di DB seperti "/api/images/item_abc.png"
 */
export const getImageUrl = (path) => {
    if (!path) return null;
    if (path.startsWith('http')) return path;
    const cleanPath = path.startsWith('/') ? path : `/${path}`;
    return `${BASE_URL}${cleanPath}`;
};

// 2. REQUEST INTERCEPTOR (Hanya satu block agar efisien)
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => Promise.reject(error));

// 3. RESPONSE INTERCEPTOR
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        const { response } = error;

        // A. HANDLING ERROR PESAN DARI BACKEND (GlobalExceptionHandler)
        // Menangkap pesan seperti "Username sudah digunakan" atau "Stok habis"
        if (response && response.data && response.data.message) {
            // Tampilkan alert/toast di sini agar tidak perlu menulis di setiap file .vue
            console.error("Backend Error:", response.data.message);
            
            // Jangan tampilkan alert jika ini error 401 yang akan di-refresh di bawah
            if (response.status !== 401) {
                alert(response.data.message);
            }
        }

        // B. CEK JIKA INI REQUEST LOGIN
        // Jika gagal login (karena password salah/akun diblokir), langsung lempar error
        if (originalRequest.url.includes('/api/auth/login')) {
            if (response && response.status === 401) {
                alert(response.data.message || "Username atau Password salah.");
            }
            return Promise.reject(error);
        }

        // C. LOGIC REFRESH TOKEN (Status 401 selain login)
        // Menangani sesi kadaluarsa atau token yang ditimpa login baru
        if (response && response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) throw new Error("No refresh token");

                // Request ke endpoint refresh tanpa instance 'api' agar tidak loop interceptor
                const res = await axios.post(`${BASE_URL}/api/auth/refresh-token`, {
                    refreshToken: refreshToken
                });

                const newAccessToken = res.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);

                // Ulangi request asli dengan token baru
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return api(originalRequest);

            } catch (refreshError) {
                // D. REDIRECT JIKA REFRESH GAGAL
                if (window.location.pathname !== '/login') {
                    localStorage.clear();
                    alert("Sesi Anda telah berakhir atau akun login di perangkat lain.");
                    window.location.href = "/login";
                }
                return Promise.reject(refreshError);
            }
        }

        // E. HANDLING 403 FORBIDDEN (Akses Admin Ditolak)
        if (response && response.status === 403) {
            console.warn("Akses data ditolak oleh server (403).");
        }

        return Promise.reject(error);
    }
);

export default api;