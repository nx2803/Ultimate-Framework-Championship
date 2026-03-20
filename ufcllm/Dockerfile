# Python 3.12 슬림 이미지를 기반으로 사용
FROM python:3.12-slim

# 환경 변수 설정: 파이썬 출력 버퍼링 비활성화 및 바이트코드 생성 방지
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PYTHONPATH=/app \
    PORT=7860

# 작업 디렉토리 설정
WORKDIR /app

# 시스템 의존성 설치 (PostgreSQL 연결 등을 위해 필요한 경우)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Hugging Face Spaces 사용자 설정 (UID 1000)
# Spaces는 보안상의 이유로 루트 권한이 아닌 특정 사용자로 실행되어야 합니다.
RUN useradd -m -u 1000 user && chown -R user:user /app
USER user
ENV HOME=/home/user \
    PATH=/home/user/.local/bin:$PATH


# 프로젝트 파일 복사 (소유권 설정 포함)
COPY --chown=user:user . .

# 의존성 설치 (pip install . 를 통해 pyproject.toml 기반 설치)
RUN pip install --no-cache-dir --user .

# Spaces 기본 포트 노출
EXPOSE 7860

# 서버 실행 명령어
# --factory 옵션을 사용하여 get_app() 함수를 통해 FastAPI 인스턴스를 생성합니다.
CMD ["uvicorn", "ufcllm.web.application:get_app", "--host", "0.0.0.0", "--port", "7860", "--factory"]
