<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { toast } from "vue-sonner"

import { useAuthStore } from "@/stores/useAuthStore"
import { useFormActions } from "@/composables/useFormActions"

const { login } = useFormActions()
const router = useRouter()
const auth = useAuthStore()

const username = ref("")
const password = ref("")

async function loginHandler() {
    try {
        const data = await login(username.value, password.value)
        auth.login(data)
        await router.push("/")
    } catch (err) {
        toast.error("Username or password is incorrect.")
    }
}
</script>

<template>
    <div class="login-wrapper">
        <div class="login-container">
            <h1>Login</h1>
            <div class="input-group">
                <label for="username">[username]</label>
                <input v-model="username" type="text" id="username" placeholder="john_doe" />
            </div>

            <div>
                <label for="password">[password]</label>
                <input
                    v-model="password"
                    type="password"
                    id="password"
                    placeholder="**************"
                />
            </div>

            <button @click="loginHandler">[Sign In]</button>

            <div class="footer">Don't have an account? <a href="/register">Sign up</a></div>
        </div>
    </div>
</template>

<style scoped>
.login-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}

.login-container {
    width: 100%;
    max-width: 400px;
    border: 1px solid #515151;
    padding: 40px 30px;
    position: relative;
}

h1 {
    color: white;
    margin-bottom: 30px;
    font-size: 28px;
    font-weight: 600;
    text-align: center;
}

.input-group {
    margin-bottom: 20px;
    position: relative;
}

label {
    font-size: 17px;
    display: block;
    margin-bottom: 8px;
    font-weight: 400;
}

input {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid #515151;
    color: white;
    font-size: 16px;
    background-color: #0d0d0d;
    outline: none;
}

input::placeholder {
    color: #515151;
    opacity: 1;
}

button {
    font-size: 18px;
    width: 100%;
    padding: 12px;
    color: black;
    font-weight: 400;
    cursor: pointer;
    margin-top: 30px;
    background-color: white;
    border: 0;
}

.footer {
    text-align: center;
    margin-top: 20px;
    color: white;
}

.footer a {
    color: white;
}
</style>
