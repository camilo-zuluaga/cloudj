import axios from "axios"

export function useFormActions() {
    async function login(username, password) {
        try {
            const response = await axios.post(
                "/api/auth/login",
                { username, password },
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                },
            )
            return response.data
        } catch (err) {
            throw new Error(err)
        }
    }

    async function register(username, email, password) {
        try {
            const response = await axios.post(
                "/api/auth/register",
                { username, email, password },
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                },
            )
            return response.data
        } catch (err) {
            throw new Error(err)
        }
    }

    return {
        login,
        register,
    }
}
