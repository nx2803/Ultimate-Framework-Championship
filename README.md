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

### Analysis (AI)
![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)
![FastAPI](https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi&logoColor=white)
![Gemini](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
- **Framework**: FastAPI (Python 3.12+)
- **LLM Engine**: Google Gemini 3 Flash / 2.0 Flash
- **Database**: PostgreSQL (Shared with Backend)

### DevOps
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Vercel](https://img.shields.io/badge/vercel-%23000000.svg?style=for-the-badge&logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white)
![Hugging Face](https://img.shields.io/badge/%F0%9F%A4%97%20Hugging%20Face-Spaces-yellow?style=for-the-badge)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Upstash](https://img.shields.io/badge/Upstash-00E9A3?style=for-the-badge&logo=upstash&logoColor=white)


---

## ✨ 주요 데이터 파이프라인 및 핵심 기술

**1. GitHub API 자동 수집 및 Batch Processing (`Spring Batch 6.x`)**
- **도입 배경**: 전 세계 수십 개의 프레임워크 지표를 매시간 누락 없이 안정적으로 긁어오기 위한 강력한 데이터 파이프라인이 필요했습니다.
- **해결 및 효과**: Spring Boot 4의 최신 `Spring Batch 6.x`를 도입, 레거시 저장소를 걷어내고 `Chunk Processing`을 통해 대량의 통계 데이터를 트랜잭션 단위로 안전하게 처리합니다.

**2. Java 21 Virtual Threads (가상 스레드) 최적화**
- **도입 배경**: 외부 API(GitHub)를 동기식으로 반복 호출할 때 발생하는 I/O 병목 현상과 무거운 운영체제 스레드 생성 비용을 해결해야 했습니다.
- **해결 및 효과**: 네이티브로 지원하는 **Virtual Threads**를 스케줄링에 결합했습니다. OS 스레드 고갈 걱정 없이 수십 개의 수집 I/O 작업들을 극도로 가볍게 비동기 병렬 처리하여 자원 효율성을 크게 최적화했습니다.

**3. 데이터 선형 보간 (Linear Interpolation) 및 복잡한 통계 쿼리 최적화**
- **도입 배경**: API 횟수 초과나 네트워크 장애로 특정 시간대 수집이 누락되면, 프론트엔드 차트가 왜곡되거나 지표가 0으로 표기되는 치명적인 문제가 있었습니다.
- **해결 및 효과**: 최신 `QueryDSL 5.1`을 사용하여 누락된 시간대의 데이터를 예측해 선형 보간(Linear Interpolation)하는 로직을 자바 코드 레벨에서 안전하고 우아하게 제어해 어떠한 장애 속에서도 차트의 시각적 무결성을 보장합니다.

**4. API 응답 캐싱 (Redis)**
- **도입 배경**: 접속자가 많아질수록 복잡한 시계열 통계 및 랭킹 쿼리가 DB에 직접 꽂혀 서버에 엄청난 데이터베이스 병목 부하를 유발했습니다.
- **해결 및 효과**: `Spring Cache`와 **Upstash Redis**를 연계해 읽기(Read)와 쓰기(Write)의 부담을 완벽히 분리했습니다. 동일한 데이터는 인메모리에서 즉각 반환되어 API 응답 속도가 획기적으로 개선되었습니다.

**5. 무중단 헬스체크 인프라 (`Spring Actuator` & `Security`)**
- **도입 배경**: Render.com 플랫폼 같은 클라우드 환경에서 컨테이너의 헬스 상태를 지속 모니터링하고 무중단 배포를 지원할 타겟이 필요했습니다.
- **해결 및 효과**: `/actuator/health` 엔드포인트를 열어두고 CORS 및 Spring Security 로직을 세분화하여 외부 노출 없이 안전한 시스템 모니터링 통신 환경을 확보했습니다.

**6. 인터랙티브 데이터 시각화**
- **도입 배경**: 딱딱한 단방향 통계 패널을 넘어, 사용자에게 시각적 쾌감과 몰입감을 주는 프리미엄 UI/UX 경험이 필요했습니다.
- **해결 및 효과**: `Framer Motion`과 초박형 도넛 차트를 결합해 부드러운 상태 보간을 구현했습니다. 차트나 리스트 아이템에 Hover 시 해당 지표가 중앙에 실시간 동기화되며 시각적 퀄리티를 한 차원 높였습니다.

**7. OpenAPI 3.0 (Swagger) 기반 API 자동 문서화**
- **도입 배경**: 협업 시 프론트엔드 개발자가 백엔드 API 스펙을 수동으로 파악해야 하는 비효율성과 소통 비용을 줄여야 했습니다.
- **해결 및 효과**: `springdoc-openapi`를 활용하여 별도의 추가 리소스 투입 없이 프론트엔드-백엔드 간 API 명세서를 자동화하고, 직관적인 Swagger UI 환경을 연동시켜 개발 생산성을 높였습니다.

**8. GitHub API 예외 처리 및 회복 탄력성 (Resilience)**
- **도입 배경**: `422 Unprocessable Content` 및 API 호출 제한 등 GitHub 외적인 통신 변수로 인한 배치의 강제 종료 및 실패가 잦았습니다.
- **해결 및 효과**: 구형 `RestTemplate` 대신 최신 **Fluent API 기반 `RestClient`**를 적극 도입해 매우 직관적이고 견고한 커스텀 에러 캡처 및 방어 로직을 구축하여 수집 성공률을 극대화했습니다.

**9. 시계열 데이터 정합성 보장 (Time-Series Data Normalization)**
- **도입 배경**: 서버 배치는 `Hour(시간)` 단위로 데이터를 적재하지만 프론트 패널은 `Minute(분)` 단위까지 조회해오면서 시점 불일치 데이터 누락이 발생했습니다.
- **해결 및 효과**: 백엔드 서비스 레이어에서 시간 절사(Truncation) 체계를 단일화하여, DB에 저장된 시간과 클라이언트의 조회 시간을 안전하게 동기화해 데이터 정합성을 완벽히 지켜냈습니다.

**10. Supabase PostgreSQL 기반 고가용성 데이터 저장소**
- **도입 배경**: 매시간 수십 개의 기술 스탯이 무한히 누적되는 시계열 구조 특성상, 안정적이고 빠른 쿼리 응답을 보장할 Database가 필수였습니다.
- **해결 및 효과**: 클라우드 네이티브 환경의 Supabase를 활용하여 방대한 데이터를 안전하게 관리하며, 빠른 통계 집계를 위해 `collected_at` 및 프라이머리 키 인덱스 최적화를 적극 적용했습니다.

**11. 동적 테마 색상 보정 (Adaptive Color Luminance)**
- **도입 배경**: 라이트/다크 모드에 상관없이 기술별 고유 브랜드 색상(예: 노란색 JS, 남색 Expo 등)을 그대로 반영하면 배경색과 대비되어 가독성이 심각하게 훼손되는 경우가 생겼습니다.
- **해결 및 효과**: 렌더링 시 각 HEX 색상의 밝기(Relative Luminance)를 수학적으로 계산합니다. 대비가 부족한 경우 HSL 채도를 유지한 채 명도만 자동으로 보정하는 `logoUtils` 알고리즘을 구현해 완벽한 환경 적응형 가독성을 보장합니다.

**12. AI 기반  실시간 해설 시스템**
- **도입 배경**: 단순한 수치 나열을 넘어, 기술 트렌드 변화의 맥락을 짚어주고 사용자에게 재미를 줄 수 있는 고차원적인 인사이트가 필요했습니다.
- **해결 및 효과**: `Gemini 3` 모델을 활용하여 해설 시스템을 구축했습니다. 매일 기술들의 지표를 분석해 스포츠 중계 톤의 한글 해설을 생성하고, 생동감 있게 전달합니다.

---

## 🗄️ 데이터베이스 스키마 (Database Structure)

본 프로젝트는 PostgreSQL(Supabase) 내 `ufc` 스키마를 사용하여 핵심 데이터를 분리·관리합니다.

```mermaid
erDiagram
    "ufc.techs" ||--o{ "ufc.github_tech_stats" : "has many"
    "ufc.techs" ||--o{ "ufc.tech_analysis" : "has insights"
    
    "ufc.techs" {
        string name PK "기술 명칭"
        string category "분류 (LANGUAGE, FRONTEND 등)"
        string logo_url "로고 이미지 식별자"
    }

    "ufc.github_tech_stats" {
        bigint id PK
        string tech_name FK
        int star_count
        int fork_count
        int repo_count
        timestamp collected_at
    }

    "ufc.tech_analysis" {
        bigint id PK
        string category "분석 카테고리 (Indexed)"
        text insight "AI 분석 결과 전문"
        timestamp created_at "생성 시점"
    }
```

- **`ufc.github_tech_stats`**: 매시간 Spring Batch에 의해 수집되는 기술별 통계 데이터가 누적되는 시계열 메인 테이블입니다.
- **`ufc.tech_analysis`**: AI 해설위원이 생성한 기술 카테고리별 분석 인사이트를 저장하는 테이블입니다.
- **`ufc.techs`**: 프론트엔드 환경에서 보여줄 각 기술의 분류 및 메타데이터를 관리합니다.

---

## 🛠️ 시작하기 (Local Setup)

### 1. 환경 변수 설정
`ufcback`, `ufcllm`, `ufcfront` 디렉토리에 각각 설정 파일을 생성합니다.

**`ufcback/.env`** (Spring Boot)
- `DB_URL`, `GITHUB_TOKEN`, `REDIS_HOST` 등 기존 설정

**`ufcllm/.env`** (FastAPI)
```env
UFCLLM_GEMINI_API_KEY=your_gemini_api_key
UFC_DB_URL=postgresql+asyncpg://user:pass@host:port/db
```

**`ufcfront/.env.local`** (Next.js)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 2. 데이터베이스 사전 준비
AI 분석 결과를 저장하기 위해 PostgreSQL에서 아래 SQL을 실행해야 합니다.
```sql
CREATE TABLE IF NOT EXISTS ufc.tech_analysis (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    insight TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 3. 프로젝트 실행

**Backend (Spring Boot)**
```bash
cd ufcback && ./gradlew bootRun
```

**AI Module (FastAPI)**
```bash
cd ufcllm
pip install poetry
poetry install
poetry run python -m ufcllm
```

**Frontend (Next.js)**
```bash
cd ufcfront && npm run dev
```

---

## 🔗 관련 링크

- **Backend (API)**: https://ultimate-framework-championship.onrender.com
- **AI Analysis (HF)**: [Hugging Face Spaces - UFC LLM](https://huggingface.co/spaces)
- **Frontend (Web)**: https://ultimate-framework-championship.vercel.app
- **Swagger UI**: https://ultimate-framework-championship.onrender.com/swagger-ui.html
