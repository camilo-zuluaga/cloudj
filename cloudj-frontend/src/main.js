import "./assets/main.css"

import { createApp } from "vue"
import { createPinia } from "pinia"
import App from "./App.vue"
import router from "@/router/index.js"

import "vuetify/styles"
import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"

const app = createApp(App)
const pinia = createPinia()
const vuetify = createVuetify({
    components,
    directives,
})

app.use(router).use(pinia).use(vuetify).mount("#app")
