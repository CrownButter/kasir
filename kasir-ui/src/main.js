import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router' // Pastikan baris ini ada
import 'bootstrap-icons/font/bootstrap-icons.css'
import permissionPlugin from './plugins/permissions';
const app = createApp(App)
app.use(router) 
app.use(permissionPlugin);
app.mount('#app')
