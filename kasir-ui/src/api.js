import axios from 'axios';

// 1. KONFIGURASI URL (Docker-Ready)
// Jika di Docker/Produksi, set variabel ini di file .env (VITE_API_BASE_URL)
// Jika tidak ada, otomatis pakai localhost:8080
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://demokasir.dwincomputer.com';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * HELPER: Mendapatkan URL Gambar Lengkap
 * Digunakan untuk menampilkan gambar dari folder /uploads backend
 * @param {string} path - Contoh: '/api/images/item_xyz.jpg'
 */
export const getImageUrl = (path) => {
    if (!path) return null;
    // Jika path sudah berupa URL lengkap (http...), kembalikan langsung
    if (path.startsWith('http')) return path;
    // Jika path tidak diawali dengan /, tambahkan /
    const cleanPath = path.startsWith('/') ? path : `/${path}`;
    return `${BASE_URL}${cleanPath}`;
};

// 2. REQUEST INTERCEPTOR
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 3. RESPONSE INTERCEPTOR (Auto Refresh Token)
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');

                if (!refreshToken) {
                    throw new Error("No refresh token available");
                }

                // Gunakan axios mentah dengan BASE_URL agar tidak loop interceptor
                const res = await axios.post(`${BASE_URL}/api/auth/refresh-token`, {
                    refreshToken: refreshToken
                });

                const newAccessToken = res.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);

                if (res.data.refreshToken) {
                    localStorage.setItem('refreshToken', res.data.refreshToken);
                }

                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return api(originalRequest);

            } catch (refreshError) {
                console.error("Sesi telah habis, silakan login ulang.");
                localStorage.clear();
                window.location.href = "/login";
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default api;