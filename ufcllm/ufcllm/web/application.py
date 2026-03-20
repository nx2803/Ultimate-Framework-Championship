from fastapi import FastAPI
from fastapi.responses import UJSONResponse, RedirectResponse

from ufcllm.log import configure_logging
from ufcllm.web.api.router import api_router
from ufcllm.web.lifespan import lifespan_setup


def get_app() -> FastAPI:
    """
    Get FastAPI application.

    This is the main constructor of an application.

    :return: application.
    """
    configure_logging()
    app = FastAPI(
        title="ufcllm",
        lifespan=lifespan_setup,
        docs_url="/api/docs",
        redoc_url="/api/redoc",
        openapi_url="/api/openapi.json",
        default_response_class=UJSONResponse,
    )

    # Main router for the API.
    app.include_router(router=api_router, prefix="/api")

    @app.get("/", include_in_schema=False)
    def root_redirect() -> RedirectResponse:
        """
        Redirect to API documentation.
        """
        return RedirectResponse(url="/api/docs")

    return app


