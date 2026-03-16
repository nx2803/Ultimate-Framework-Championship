# 🥊 Ultimate Framework Championship (UFC)

Ultimate Framework Championship(UFC)은 전 세계 다양한 프로그래밍 프레임워크와 기술들의 GitHub 통계를 실시간으로 수집하고, 그 트렌드를 시각화하여 보여주는 오픈 테크 인사이트 플랫폼입니다.

---

## 🏗️ 프로젝트 구조

프로젝트는 크게 백엔드 API 서버와 프론트엔드 웹 애플리케이션으로 나뉩니다.

```text
UFC/
├── ufcback/        # Spring Boot 4.0.3 (Java 21) 기반 백엔드
└── ufcfront/       # Next.js (TypeScript) 기반 프론트엔드
```

---

## 🚀 주요 기술 스택

### Backend
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

- **핵심 인프라**: Java 21, Spring Boot 4.0.3, Spring Batch 6.0
- **데이터 & 캐시**: PostgreSQL (Supabase), Redis (Upstash)
- **배포**: Docker (Multi-stage Build), Render.com

### Frontend
![Next.js](https://img.shields.io/badge/Next.js-black?style=for-the-badge&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)

- **프레임워크**: Next.js (App Router), TypeScript
- **스타일링**: TailwindCSS, Vanilla CSS
- **배포**: Vercel

---

## ✨ 주요 기능

1. **GitHub 통계 자동 수집**: Spring Batch를 통해 주기적으로 주요 기술들의 Star, Fork, Repo 수를 수집합니다.
2. **기술별 트렌드 차트**: 수집된 데이터를 바탕으로 시간 흐름에 따른 인기 변화 및 시장 점유율(Market Share)을 차트로 제공합니다.
3. **카테고리별 비교**: Frontend, Backend, Database 등 카테고리 내 기술들을 한눈에 비교할 수 있는 기능을 제공합니다.
4. **실시간 검색 및 필터링**: 관심 있는 기술을 빠르게 찾아 차트로 시각화합니다.

---

## 🛠️ 시작하기 (Local Setup)

### 1. 환경 변수 설정
각 디렉토리의 `.env` 파일에 필요한 정보를 설정해야 합니다.

**백엔드 (`ufcback/.env`)**
```env
DB_URL=jdbc:postgresql://your-db-endpoint
DB_USERNAME=your-username
DB_PASSWORD=your-password
GITHUB_TOKEN=your-github-pat
REDIS_HOST=your-upstash-host
REDIS_PORT=your-upstash-port
REDIS_PASSWORD=your-upstash-password
```

**프론트엔드 (`ufcfront/src/lib/api.ts`)**
- 로컬 개발 시 `API_BASE_URL`을 `http://localhost:8080/api`로 설정합니다.

### 2. 실행 방법

**Backend**
```bash
cd ufcback
./gradlew bootRun
```

**Frontend**
```bash
cd ufcfront
npm install
npm run dev
```

---

## 🐳 Docker 배포

백엔드는 Docker 이미지로 빌드하여 배포할 수 있습니다.
```bash
cd ufcback
docker build -t ufc-backend .
docker run -p 8080:8080 --env-file .env ufc-backend
```

---

## 🔗 관련 주소

- **Backend (API)**: https://ultimate-framework-championship.onrender.com
- **Frontend (Web)**: https://ultimate-framework-championship.vercel.app
- **Swagger UI**: https://ultimate-framework-championship.onrender.com/swagger-ui.html

---

## 📄 License
This project is for educational and insightful purposes.
