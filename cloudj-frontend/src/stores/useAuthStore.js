import { defineStore } from "pinia"
import axios from "axios"

export const useAuthStore = defineStore("auth", {
    state: () => ({ accessToken: null, username: null }),

    getters: {
        isAuthenticated: (state) => !!state.accessToken,
    },

    actions: {
        async init() {
            try {
                await this.refreshToken()
            } catch (error) {
                this.accessToken = null
            }
        },

        setAccessToken(token) {
            this.accessToken = token
        },

        login(data) {
            this.setAccessToken(data.accessToken)
            this.username = data.username
        },

        async logout() {
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
                throw new Error(error)
            }
        },

        async refreshToken() {
            try {
                const response = await axios.post("/api/auth/refresh-token", null, {
                    withCredentials: true,
                })

                this.setAccessToken(response.data.accessToken)
                this.username = response.data.username
            } catch (error) {
                throw new Error(error)
            }
        },
    },
})
