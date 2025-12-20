<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"

import { toast } from "vue-sonner"
import { useFormActions } from "@/composables/useFormActions"
import { VForm, VTextField } from "vuetify/components"
import { usernameRules, emailRules, passwordRules } from "@/utils/validators"

const router = useRouter()
const { register } = useFormActions()

const username = ref("")
const email = ref("")
const password = ref("")
const isRegistering = ref(false)
const formRef = ref(null)

async function registerForm() {
    const { valid } = await formRef.value.validate()

    if (!valid) {
        return
    }

    isRegistering.value = true

    try {
        await register(username.value, email.value, password.value)
        await router.push("/")
    } catch (err) {
        toast.error("Username or e-mail already exists.")
    } finally {
        isRegistering.value = false
    }
}
</script>

<template>
    <div class="login-wrapper">
        <v-form
            ref="formRef"
            class="login-container custom-error-theme"
            @submit.prevent="registerForm"
        >
            <h1>Register</h1>

            <div class="input-group">
                <label>[username]</label>
                <v-text-field
                    :rules="usernameRules"
                    v-model="username"
                    variant="plain"
                    placeholder="john_doe"
                    class="field-input"
                />
            </div>

            <div class="input-group">
                <label>[email]</label>
                <v-text-field
                    :rules="emailRules"
                    v-model="email"
                    variant="plain"
                    placeholder="your@email.com"
                    class="field-input"
                />
            </div>

            <div class="input-group">
                <label>[password]</label>
                <v-text-field
                    :rules="passwordRules"
                    v-model="password"
                    type="password"
                    variant="plain"
                    placeholder="**************"
                    class="field-input"
                />
            </div>

            <button type="submit" class="submit-button">
                <span v-if="!isRegistering">[Register]</span>
                <span v-else class="loader"></span>
            </button>
        </v-form>
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

.login-container :deep(.v-messages__message) {
    color: #db4646;
}

h1 {
    color: white;
    margin-bottom: 30px;
    font-size: 28px;
    font-weight: 600;
    text-align: center;
}

.input-group {
    margin-bottom: 5px;
    position: relative;
}

label {
    font-size: 17px;
    display: block;
    margin-bottom: 8px;
    font-weight: 400;
}

.field-input :deep(.v-field__input) {
    color: white;
    font-size: 16px;
    padding: 12px 15px;
}

.field-input :deep(.v-field) {
    background-color: #0d0d0d;
    border: 1px solid #515151;
}

input::placeholder {
    color: #515151;
    opacity: 1;
}

.submit-button {
    display: flex;
    justify-content: center;
    background-color: white;
    font-size: 18px;
    width: 100%;
    padding: 12px;
    color: black;
    font-weight: 400;
    cursor: pointer;
    margin-top: 30px;
    border: 0;
}

.loader {
    width: 28px;
    height: 28px;
    border: 2px solid black;
    border-bottom-color: transparent;
    border-radius: 50%;
    display: inline-block;
    box-sizing: border-box;
    animation: rotation 1s linear infinite;
}

@keyframes rotation {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}
</style>
