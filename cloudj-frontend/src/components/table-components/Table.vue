<template>
    <table class="rwd-table">
        <tbody>
            <tr>
                <th>Filename</th>
                <th class="th-type">Type</th>
                <th class="th-size">Size</th>
                <th class="th-date">Uploaded At</th>
                <th class="th-actions"></th>
            </tr>
            <slot></slot>
        </tbody>
    </table>
</template>

<style scoped>
.rwd-table {
    table-layout: fixed;
    border-top: 1px solid #373737;
    font-size: 16px;
    color: white;
    width: 100%;
    border-collapse: collapse;
}

.rwd-table th:first-child {
    border-left: none;
}

.rwd-table th:last-child {
    border-right: none;
}

.rwd-table th {
    border: 1px solid #373737;
    text-align: left;
    padding: 15px;
}

.th-size {
    width: 15%;
}

.th-type {
    width: 15%;
}

.th-date {
    width: 25%;
}

.th-actions {
    width: 15%;
}
</style>
