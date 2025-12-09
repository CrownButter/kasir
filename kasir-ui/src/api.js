import axios from 'axios';

const api = axios.create({
    baseURL: '', 
    headers: {
        'Content-Type': 'application/json'
    }
});

// 1. REQUEST INTERCEPTOR
// Sisipkan 'accessToken' (bukan sekadar 'token') ke Header setiap request
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken'); // Ganti key jadi 'accessToken'
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 2. RESPONSE INTERCEPTOR (Fitur Baru: Auto Refresh)
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Jika error 401 (Unauthorized) dan belum pernah mencoba refresh sebelumnya
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // Ambil refresh token dari storage
                const refreshToken = localStorage.getItem('refreshToken');

                if (!refreshToken) {
                    throw new Error("No refresh token");
                }

                // Panggil endpoint refresh token di backend
                // Perhatikan: Kita pakai axios biasa (bukan instance 'api') untuk menghindari loop
                const res = await axios.post('http://localhost:8080/api/auth/refresh-token', {
                    refreshToken: refreshToken
                });

                // Simpan Access Token yang BARU
                const newAccessToken = res.data.accessToken;
                localStorage.setItem('accessToken', newAccessToken);
                // Jika backend mengirim refresh token baru, simpan juga (opsional tergantung backend)
                if (res.data.refreshToken) {
                    localStorage.setItem('refreshToken', res.data.refreshToken);
                }

                // Update header request yang gagal tadi dengan token baru
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

                // Ulangi request awal
                return api(originalRequest);

            } catch (refreshError) {
                // Jika Refresh Token juga expired atau tidak valid -> Paksa Logout
                console.error("Sesi habis, silakan login ulang.");
                localStorage.clear();
                window.location.href = "/login"; // Redirect ke halaman login
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default api;