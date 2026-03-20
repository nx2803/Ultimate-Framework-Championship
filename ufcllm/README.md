---
title: UFC LLM Analysis
emoji: 🥊
colorFrom: green
colorTo: gray
sdk: docker
pinned: false
app_port: 7860
short_description: Codig technology trend analysis using Gemini 3 & FastAPI
---

# 🥊 UFC LLM (Ultimate Framework Championship - Analysis Module)

이 프로젝트는 UFC(Ultimate Framework Championship) 플랫폼의 핵심 AI 분석 엔진입니다. 
GitHub의 기술트렌드 데이터를 바탕으로 **Google Gemini 3 Flash** 모델을 사용하여 스포츠 중계 스타일의 인사이트를 생성합니다.

## 🚀 주요 기능
- **AI 분석**: 전 세계 프레임워크/언어의 별점, 포크, 리포지토리 점유율 변화 분석.
- **스포츠 중계 컨셉**: 딱딱한 통계가 아닌 생동감 넘치는 '코딩 리그' 중계 해설 생성.
- **FastAPI 기반**: 고성능 비동기 API 서버로 신속한 인사이트 제공.

## 🛠️ 기술 스택
- **Language**: Python 3.12+
- **Framework**: FastAPI
- **LLM**: Google Gemini 3 Flash / 2.0 Flash
- **Dependency Management**: UV / Poetry
- **Infrastructure**: Docker (Hugging Face Spaces)

## ⚙️ 환경 변수 (Secrets)
Hugging Face Spaces의 **Settings > Variables and secrets**에서 다음 항목을 설정해야 합니다:

| Key | Description |
|-----|-------------|
| `UFCLLM_GEMINI_API_KEY` | Google AI Studio에서 발급받은 Gemini API 키 |
| `UFC_DB_URL` | 수집된 통계 및 분석 결과를 저장할 PostgreSQL DB URL (asyncpg용) |

## 📦 로컬 실행 가이드
로컬 환경에서 테스트하려면 `uv`를 사용하는 것이 가장 빠릅니다.

```bash
uv sync --locked
uv run -m ufcllm
```

Swagger 배포 문서는 `/api/docs`에서 확인할 수 있습니다.

## 🐳 Docker 개발
```bash
docker build -t ufc-llm .
docker run -p 7860:7860 --env-file .env ufc-llm
```

---
*본 모듈은 UFC 메인 백엔드(Spring Boot)의 스케줄러에 의해 매일 자동으로 호출되어 새로운 해설을 생성합니다.*
