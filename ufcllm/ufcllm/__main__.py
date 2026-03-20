import uvicorn
from ufcllm.settings import settings

def main() -> None:
    """Entrypoint of the application."""
    import platform
    # 윈도우이거나 리로드 모드인 경우 uvicorn 사용
    if settings.reload or platform.system() == "Windows":
        uvicorn.run(
            "ufcllm.web.application:get_app",
            workers=settings.workers_count,
            host=settings.host,
            port=settings.port,
            reload=settings.reload,
            log_level=settings.log_level.value.lower(),
            factory=True,
        )
    else:
        # Linux/Unix 환경에서만 gunicorn 사용
        from ufcllm.gunicorn_runner import GunicornApplication
        GunicornApplication(
            "ufcllm.web.application:get_app",
            host=settings.host,
            port=settings.port,
            workers=settings.workers_count,
            factory=True,
            accesslog="-",
            loglevel=settings.log_level.value.lower(),
            access_log_format='%r "-" %s "-" %Tf',
        ).run()


if __name__ == "__main__":
    main()
