# 🥊 Ultimate Framework Championship (UFC)

UFC는 전 세계 다양한 프로그래밍 프레임워크와 기술 트렌드를 실시간으로 수집하고, 그 지표를 시각화하여 제공하는 오픈 테크 인사이트 플랫폼입니다.

---

## 🚀 기술 스택

### Backend
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
- **Framework**: Java 21, Spring Boot 4.x
- **Data Pipeline**: Spring Batch 6.x
- **Database**: PostgreSQL (Supabase)
- **Cache**: Redis (Upstash)

### Frontend
![Next JS](https://img.shields.io/badge/Next-black?style=for-the-badge&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Framer](https://img.shields.io/badge/Framer-black?style=for-the-badge&logo=framer&logoColor=blue)
![Chart.js](https://img.shields.io/badge/chart.js-F5788D.svg?style=for-the-badge&logo=chart.js&logoColor=white)
- **Framework**: Next.js (App Router), React 18
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Visualization**: Chart.js, Framer Motion

### DevOps
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white)
![Upstash](https://img.shields.io/badge/Upstash-00E9A3?style=for-the-badge&logo=upstash&logoColor=white)

---

## ✨ 주요 데이터 파이프라인 및 핵심 기술

**1. GitHub API 자동 수집 및 스케줄링 (`Spring Batch`)**
- 매시간 GitHub REST API를 호출하여 기술별 Stars, Forks, Repositories 데이터를 수집합니다.
- 대량의 통계 데이터는 `Chunk Processing`을 통해 저장하고, 캐시 무효화는 `Tasklet`을 이용합니다.

**2. Java 21 Virtual Threads (가상 스레드) 최적화**
- API 요청 처리와 스케줄링 작업에 경량화된 Virtual Threads를 도입하여 고동시성 환경에서의 시스템 퍼포먼스와 자원 효율성을 크게 최적화했습니다.

**3. 데이터 선형 보간 (Linear Interpolation)**
- API Rate Limit 또는 기타 이슈로 누락된 시간대의 데이터는 백엔드 로직에서 이전/다음 값을 선형 보간하여 차트 왜곡을 방지합니다.

**4. API 응답 캐싱 (Redis)**
- 동일한 통계 데이터를 매번 DB에서 집계하지 않도록, `Spring Cache`와 Upstash Redis를 연계해 API 응답 속도를 최적화했습니다.

**5. 무중단 헬스체크 인프라 (`Spring Actuator` & `Security`)**
- Render.com(또는 등급 서버) 환경에서의 무중단 배포를 위해 `/actuator/health` 엔드포인트를 열어두고, 이를 Spring Security로 보호/허용 처리하는 세밀한 헬스체크 인프라를 구축했습니다.

**6. 인터랙티브 데이터 시각화**
- 카테고리 전환 시 Framer Motion을 사용하여 부드러운 상태 보간 및 애니메이션을 제공합니다.
- 차트 데이터 포인트나 랭킹 리스트 아이템에 Hover 시 해당 지표(`Market Share`, `Stars`, `Forks`)를 실시간으로 중앙에 동기화하여 표시합니다.

**7. OpenAPI 3.0 (Swagger) 기반 API 자동 문서화**
- `springdoc-openapi`를 활용하여 별도의 수동 작업 없이 프론트엔드-백엔드 간 API 스펙 명세서를 자동화하고, 직관적인 Swagger UI 환경을 연동했습니다.

**8. GitHub API 예외 처리 및 회복 탄력성 (Resilience)**
- `422 Unprocessable Content` 및 Rate Limit 초과 등 외부 API 통신 중 발생할 수 있는 오류에 대응하는 견고한 예외 처리 및 쿼리 필터링 방어 로직을 갖추었습니다.

**9. 시계열 데이터 정합성 보장 (Time-Series Data Normalization)**
- 수집 주기(Hour)와 조회 기준(Minute) 간의 시간차로 생길 수 있는 데이터 불일치 문제를 해결하기 위해 백엔드 서비스 레이어에서 시간 절사(Truncation) 체계를 단일화했습니다.

**10. CORS 및 Security API 엔드포인트 세분화**
- 프론트엔드의 접근은 허용하면서도, Health Check(`/actuator/health`)와 API 엔드포인트별 보안 정책(Spring Security)을 세밀하게 분리하여 클라우드 인프라 요건과 보안성을 함께 충족했습니다.

---

## 🛠️ 시작하기 (Local Setup)

### 1. 환경 변수 설정
`ufcback` 및 `ufcfront` 디렉토리에 각각 `.env`, `.env.local` 파일을 생성합니다.

**`ufcback/.env`**
```env
DB_URL=jdbc:postgresql://your-db-endpoint
DB_USERNAME=postgres
DB_PASSWORD=your-password
GITHUB_TOKEN=ghp_your_token
REDIS_HOST=your-redis-host
REDIS_PORT=your-redis-port
REDIS_PASSWORD=your-redis-password
```

**`ufcfront/.env.local`**
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 2. 프로젝트 실행

**Backend**
```bash
cd ufcback
./gradlew clean bootRun
```

**Frontend**
```bash
cd ufcfront
npm install
npm run dev
```

---

## 🔗 관련 링크

- **Backend (API)**: https://ultimate-framework-championship.onrender.com
- **Frontend (Web)**: https://ultimate-framework-championship.vercel.app
- **Swagger UI**: https://ultimate-framework-championship.onrender.com/swagger-ui.html
