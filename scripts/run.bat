@echo off

REM Set the base URL of the microservice
set BASE_URL=http://localhost:8080/v1/api


REM Add new user activity for custom session window processor
set USER_ACTIVITY=user/activity
echo Adding a new user activity...
curl -X POST %BASE_URL%/%USER_ACTIVITY% -H "Content-Type: application/json" -d "{\"userId\": \"1\",\"activity\": \"new activity added\"}"
echo.


REM Add new IOT status for custom sliding window processor
set IOT_STATUS=iot/status
echo Adding a new IOT status...
curl -X POST %BASE_URL%/%IOT_STATUS% -H "Content-Type: application/json" -d "{\"key\":\"1\",\"status\": 42.0}"
echo.
