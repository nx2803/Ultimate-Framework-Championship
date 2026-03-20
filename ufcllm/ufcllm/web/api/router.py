from fastapi.routing import APIRouter

from ufcllm.web.api import analysis, echo, monitoring

api_router = APIRouter()
api_router.include_router(monitoring.router)
api_router.include_router(echo.router, prefix="/echo", tags=["echo"])
api_router.include_router(analysis.router, prefix="/analysis", tags=["analysis"])
