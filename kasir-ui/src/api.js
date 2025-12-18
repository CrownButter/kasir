import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

export const getImageUrl = (path) => {
    if (!path) return null;
    if (path.startsWith('http')) return path;
    const cleanPath = path.startsWith('/') ? path : `/${path}`;
    return `${BASE_URL}${cleanPath}`;
};

// 1. REQUEST INTERCEPTOR (Tambah Token)
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => Promise.reject(error));

// 2. RESPONSE INTERCEPTOR (Single Entry - Best Practice)
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // A. CEK JIKA INI REQUEST LOGIN
        // Jika gagal login (401), JANGAN jalankan logic refresh/redirect.
        // Langsung lempar error agar ditangkap catch di Login.vue
        if (originalRequest.url.includes('/api/auth/login')) {
            return Promise.reject(error);
        }

        // B. LOGIC REFRESH TOKEN (Hanya untuk request selain login)
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) throw new Error("No refresh token");

                // Gunakan axios standar agar tidak masuk ke interceptor ini lagi (mencegah loop)
                const res = await axios.post(`${BASE_URL}/api/auth/refresh-token`, {
                    refreshToken: refreshToken
                });

                const newAccessToken = res.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);

                // Ulangi request yang gagal tadi dengan token baru
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return api(originalRequest);

            } catch (refreshError) {
                // C. REDIRECT HANYA JIKA TIDAK DI HALAMAN LOGIN
                if (window.location.pathname !== '/login') {
                    console.error("Sesi habis.");
                    localStorage.clear();
                    window.location.href = "/login";
                }
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;