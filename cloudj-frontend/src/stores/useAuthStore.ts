import { defineStore } from "pinia"
import axios from "axios"
import type { LoginData } from "@/types/loginData"

interface AuthState {
    accessToken: string | null
    username: string | null
}

export const useAuthStore = defineStore("auth", {
    state: (): AuthState => ({
        accessToken: null,
        username: null
    }),

    getters: {
        isAuthenticated: (state): boolean => !!state.accessToken,
    },

    actions: {
        async init(): Promise<void> {
            try {
                await this.refreshToken()
            } catch (error) {
                this.accessToken = null
            }
        },

        setAccessToken(token: string | null): void {
            this.accessToken = token
        },

        login(data: LoginData): void {
            this.setAccessToken(data.accessToken)
            this.username = data.username
        },

        async logout(): Promise<void> {
            try {
                const response = await axios.post("/api/auth/logout", null, {
                    headers: {
                        Authorization: `Bearer ${this.accessToken}`,
                    },
                })

                if (response.status === 401) {
                    await this.refreshToken()
                    await this.logout()
                }

                this.setAccessToken(null)
            } catch (error) {
                throw new Error(error instanceof Error ? error.message : "Logout failed")
            }
        },

        async refreshToken(): Promise<void> {
            try {
                const response = await axios.post("/api/auth/refresh-token", null, {
                    withCredentials: true,
                })

                this.setAccessToken(response.data.accessToken)
                this.username = response.data.username
            } catch (error) {
                throw new Error(error instanceof Error ? error.message : "Refresh token failed")
            }
        },
    },
})
