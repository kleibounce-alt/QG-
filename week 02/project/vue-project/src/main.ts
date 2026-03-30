import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'

const pinia = createPinia()
// 1. 先创建 app 实例
const app = createApp(App)

// 2. 再使用插件
app.use(router)
app.use(ElementPlus)

// 3. 最后挂载
app.mount('#app')

app.use(pinia)