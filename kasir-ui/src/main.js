import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router' // Pastikan baris ini ada
import 'bootstrap-icons/font/bootstrap-icons.css'
const app = createApp(App)
app.use(router) // Pastikan baris ini ada
app.mount('#app')