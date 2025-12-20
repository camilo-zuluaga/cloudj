export const usernameRules = [
    (value) => !!value || "Required.",
    (value) => (value || "").length >= 4 || "Min 4 characters",
    (value) => (value || "").length <= 15 || "Max 15 characters",
    (value) => {
        const pattern = /^[a-zA-Z0-9_]{4,22}$/
        return pattern.test(value) || "Invalid username."
    },
]

export const emailRules = [
    (value) => !!value || "Required.",
    (value) => (value || "").length <= 40 || "Max 40 characters",
    (value) => {
        // RFC5322 Regex for email validation
        const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(value) || "Invalid e-mail."
    },
]

export const passwordRules = [
    (value) => !!value || "Required.",
    (value) => (value || "").length >= 8 || "Min 8 characters",
    (value) => (value || "").length <= 32 || "Max 32 characters",
    (value) => {
        const pattern = /^(?=.*?[0-9])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^0-9A-Za-z]).+$/
        return pattern.test(value) || "Invalid password."
    },
]
