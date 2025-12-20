import { createRouter, createWebHistory } from "vue-router"

import Login from "@/components/Login.vue"
import Register from "@/components/Register.vue"
import Dashboard from "@/components/Dashboard.vue"
import { useAuthStore } from "@/stores/useAuthStore"

const routes = [
    { path: "/login", name: "Login", component: Login },
    { path: "/register", name: "Register", component: Register },
    {
        path: "/",
        name: "Dashboard",
        component: Dashboard,
        meta: { requiresAuth: true },
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach(async (to, from) => {
    const auth = useAuthStore()

    if (!auth.isAuthenticated) {
        // try to initialize so we can check routes
        await auth.init()
    }

    if (auth.isAuthenticated && ["Login", "Register"].includes(to.name)) {
        return { name: "Dashboard" }
    }

    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        try {
            await auth.refreshToken()
        } catch (err) {
            auth.setAccessToken(null)
            return { name: "Login" }
        }
    }
})

export default router
