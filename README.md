https://github.com/user-attachments/assets/acfb7ab1-0773-4287-a15a-c9377877ef81

# Cloudj ☁️
CloudJ is a cloud storage web application inspired by platforms like Google Drive and MediaFire. It allows users to securely upload, store, and manage files using Amazon S3 as object storage.
The system supports both single-part and multipart uploads through pre-signed URLs, enabling efficient and reliable handling of large files directly from the client.

## Features
- JWT-based authentication and authorization
- Secure file storage using Amazon S3 object storage
- Single-part and multipart uploads via pre-signed URLs
- Efficient large file handling with chunking
- User-scoped file access and permissions
- PostgreSQL-backed persistence for users and file metadata
- RESTful backend API built with Spring Boot
- Modern frontend built with Vue.js
- Abort uploads
- Sharing file with presigned urls

 ## Motivation
This project was created as a learning experience to explore and integrate a modern frontend framework (Vue.js), a Spring Boot backend, and a cloud object storage service like Amazon S3.
Its primary goal is to experiment with concepts such as JWT-based authentication, pre-signed URL uploads, and multipart file handling, rather than to serve as a production-ready system which would involve much more.
