# GCP Spring Boot Application

A production-ready Spring Boot application with full Google Cloud Platform (GCP) integration for managing documents with Firestore database and Cloud Storage file handling.

## 🚀 Features

- **Firestore Database** - NoSQL document storage with auto-generated IDs
- **Cloud Storage** - Secure file uploads with UUID naming and public URLs
- **Complete REST API** - Full CRUD operations with error handling
- **Comprehensive Logging** - Structured logging with SLF4J
- **CORS Support** - Cross-origin requests enabled
- **Health Monitoring** - Application status endpoint
- **Validation** - Input validation and error handling
- **Lombok** - Reduced boilerplate code

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- GCP Account with billing enabled
- Google Cloud SDK installed

## ⚡ Quick Start (5 Minutes)

See [QUICKSTART.md](QUICKSTART.md) for a 5-minute setup guide.

## 🔧 Detailed Setup Instructions

### Step 1: GCP Project Setup

```bash
# Set your GCP project
gcloud config set project YOUR_PROJECT_ID

# Enable required services
gcloud services enable firestore.googleapis.com storage-api.googleapis.com

# Create service account
gcloud iam service-accounts create springboot-app --display-name="Spring Boot App"

# Grant necessary permissions
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:springboot-app@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/editor"

# Create and download service account key
gcloud iam service-accounts keys create ~/gcp-key.json \
  --iam-account=springboot-app@YOUR_PROJECT_ID.iam.gserviceaccount.com

# Create Cloud Storage bucket
gsutil mb gs://springboot-app-bucket-YOUR_PROJECT_ID
```

### Step 2: Configure Environment Variables

```bash
export GOOGLE_APPLICATION_CREDENTIALS=~/gcp-key.json
export GCP_PROJECT_ID=YOUR_PROJECT_ID
export GCP_STORAGE_BUCKET=springboot-app-bucket-YOUR_PROJECT_ID
```

### Step 3: Build and Run

```bash
# Clone the repository
git clone https://github.com/sureshs59/gcp-springboot-app.git
cd gcp-springboot-app

# Build with Maven
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

## 📚 API Endpoints

### Health Check
```bash
GET /api/health
```
Returns application status and health information.

**Response:**
```json
{
  "status": "UP",
  "service": "GCP Spring Boot Application",
  "message": "Application is running successfully"
}
```

### Create Document
```bash
POST /api/documents
```
Create a new document with optional file upload.

**Request:**
```bash
curl -X POST http://localhost:8080/api/documents \
  -F "title=My Document" \
  -F "description=Document Description" \
  -F "file=@/path/to/file.pdf"
```

**Response:**
```json
{
  "success": true,
  "message": "Document created successfully",
  "statusCode": 201,
  "data": {
    "id": "auto-generated-id",
    "title": "My Document",
    "description": "Document Description",
    "fileUrl": "https://storage.googleapis.com/bucket/uuid_filename.pdf",
    "fileName": "file.pdf",
    "fileSize": 1024,
    "createdAt": "2026-05-18T10:30:00",
    "updatedAt": "2026-05-18T10:30:00"
  }
}
```

### Get All Documents
```bash
GET /api/documents
```
Retrieve all documents from Firestore.

**Request:**
```bash
curl http://localhost:8080/api/documents
```

**Response:**
```json
{
  "success": true,
  "message": "Documents retrieved successfully",
  "statusCode": 200,
  "data": [
    {
      "id": "document-id-1",
      "title": "Document 1",
      "description": "Description 1"
    }
  ]
}
```

### Get Document by ID
```bash
GET /api/documents/{id}
```
Retrieve a specific document by its ID.

**Request:**
```bash
curl http://localhost:8080/api/documents/document-id-1
```

### Update Document
```bash
PUT /api/documents/{id}
```
Update an existing document's title or description.

**Request:**
```bash
curl -X PUT http://localhost:8080/api/documents/document-id-1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "description": "Updated Description"
  }'
```

### Delete Document
```bash
DELETE /api/documents/{id}
```
Delete a document and its associated file.

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/documents/document-id-1
```

## 📁 Project Structure

```
gcp-springboot-app/
├── src/main/java/com/example/
│   ├── GcpSpringBootApplication.java
│   ├── config/
│   │   └── GcpConfig.java
│   ├── controller/
│   │   ├── DocumentController.java
│   │   └── HealthController.java
│   ├── service/
│   │   ├── DocumentService.java
│   │   └── StorageService.java
│   ├── repository/
│   │   └── DocumentRepository.java
│   ├── model/
│   │   └── Document.java
│   └── dto/
│       └── ApiResponse.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
├── README.md
└── QUICKSTART.md
```

## 🔒 Security Considerations

1. **Credentials Management**
   - Never commit `gcp-key.json` to version control
   - Use `.gitignore` to exclude credentials
   - Use environment variables in production

2. **CORS Configuration**
   - Currently enabled for all origins
   - Update for specific domains in production

3. **Storage Security**
   - Files are uploaded with UUID prefix
   - Configure bucket policies based on requirements

## 🚀 Deployment

### Deploy to Cloud Run

```bash
# Build Docker image
docker build -t gcp-springboot-app .

# Tag the image
docker tag gcp-springboot-app gcr.io/YOUR_PROJECT_ID/gcp-springboot-app

# Push to Container Registry
docker push gcr.io/YOUR_PROJECT_ID/gcp-springboot-app

# Deploy to Cloud Run
gcloud run deploy gcp-springboot-app \
  --image gcr.io/YOUR_PROJECT_ID/gcp-springboot-app \
  --platform managed \
  --region us-central1 \
  --set-env-vars GCP_PROJECT_ID=YOUR_PROJECT_ID,GCP_STORAGE_BUCKET=your-bucket-name
```

## 🔧 Troubleshooting

### Issue: "Permission denied" when accessing Firestore

**Solution:**
- Verify service account has correct permissions
- Run: `gcloud projects add-iam-policy-binding PROJECT_ID --member=serviceAccount:... --role=roles/editor`

### Issue: "Bucket not found" error

**Solution:**
- Ensure bucket name is set in environment variables
- Verify bucket exists in your GCP project
- Check credentials have access to the bucket

### Issue: File upload fails

**Solution:**
- Verify `GCP_STORAGE_BUCKET` environment variable is set
- Check bucket permissions allow uploads

### Issue: Firestore connection timeout

**Solution:**
- Verify `GOOGLE_APPLICATION_CREDENTIALS` is set correctly
- Ensure Firestore API is enabled
- Check network connectivity to GCP

## 📝 Configuration Reference

Key properties in `application.properties`:

```properties
spring.application.name=gcp-springboot-app
server.port=8080
spring.cloud.gcp.project-id=${GCP_PROJECT_ID}
gcp.storage.bucket-name=${GCP_STORAGE_BUCKET}
```

## 📚 Dependencies

- Spring Boot 3.1.5
- Spring Cloud GCP 4.8.1
- Google Cloud Firestore
- Google Cloud Storage
- Lombok
- Jackson

## 📞 Support

For issues or questions:
1. Check the [QUICKSTART.md](QUICKSTART.md) guide
2. Review the troubleshooting section above
3. Check GCP documentation: https://cloud.google.com/docs

## 🎯 Next Steps

1. Complete the GCP setup following the instructions above
2. Run the application locally
3. Test the API endpoints
4. Deploy to Cloud Run for production

---

**Repository:** https://github.com/sureshs59/gcp-springboot-app
