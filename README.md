<img width="3144" height="904" alt="bannercloudj" src="https://github.com/user-attachments/assets/fb6506df-7fb5-4c39-9091-fd16c0da4cf4" />

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

## Demo
https://github.com/user-attachments/assets/1cc2b5c6-836a-40b4-82dd-c1d9953016b8

## Run the Project
### Prerequisites
- Docker and Docker Compose installed
- AWS S3 bucket with CORS configured

### 1. Clone the Repository
```bash
git clone https://github.com/camilo-zuluaga/cloudj. git
cd cloudj
```

### 2. Configure Environment Variables
Copy the example environment file and fill in your values:
```bash
cp .env.example .env
```

### 3. Configure S3 CORS
In your S3 bucket, add the following CORS configuration:
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": [
            "http://localhost:3000"
        ],
        "ExposeHeaders": ["ETag", "x-amz-id-2", "x-amz-request-id"]
    }
]
```

### 4. Build and Run
```bash
docker compose up --build
```

This will start three containers:
- **postgres** - PostgreSQL database on port 5432
- **backend** - Spring Boot API on port 8080
- **frontend** - Vue.js app served by Nginx on port 3000

### 5. Access the Application
Open your browser and navigate to: 

```
http://localhost:3000
```

## Motivation
This project was created as a learning experience to explore and integrate a modern frontend framework (Vue.js), a Spring Boot backend, and a cloud object storage service like Amazon S3.
Its primary goal is to experiment with concepts such as JWT-based authentication, pre-signed URL uploads, and multipart file handling, rather than to serve as a production-ready system which would involve much more.
