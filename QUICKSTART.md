# Quick Start Guide - 5 Minutes

Get your GCP Spring Boot application up and running in 5 minutes!

## Prerequisites

✅ Java 17+  
✅ Maven 3.6+  
✅ GCP Account with billing enabled  
✅ Google Cloud SDK installed

## Step 1: GCP Setup (2 minutes)

```bash
# Set project
gcloud config set project YOUR_PROJECT_ID

# Enable services
gcloud services enable firestore.googleapis.com storage-api.googleapis.com

# Create service account
gcloud iam service-accounts create springboot-app --display-name="Spring Boot"

# Grant permissions
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:springboot-app@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/editor"

# Download credentials
gcloud iam service-accounts keys create ~/gcp-key.json \
  --iam-account=springboot-app@YOUR_PROJECT_ID.iam.gserviceaccount.com

# Create bucket
gsutil mb gs://springboot-app-bucket-YOUR_PROJECT_ID
```

## Step 2: Environment Setup (1 minute)

```bash
export GOOGLE_APPLICATION_CREDENTIALS=~/gcp-key.json
export GCP_PROJECT_ID=YOUR_PROJECT_ID
export GCP_STORAGE_BUCKET=springboot-app-bucket-YOUR_PROJECT_ID
```

## Step 3: Run Application (2 minutes)

```bash
# Clone and build
git clone https://github.com/sureshs59/gcp-springboot-app.git
cd gcp-springboot-app
mvn clean install

# Run
mvn spring-boot:run
```

Application starts at: **http://localhost:8080**

## Test It!

```bash
# Health check
curl http://localhost:8080/api/health

# Create document
curl -X POST http://localhost:8080/api/documents \
  -F "title=Test" \
  -F "description=Quick test" \
  -F "file=@README.md"

# List all documents
curl http://localhost:8080/api/documents
```

✅ **Done!** Your application is running with Firestore and Cloud Storage!

For detailed documentation, see [README.md](README.md)
