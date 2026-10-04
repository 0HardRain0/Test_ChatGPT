# React로 날씨 체크하는 페이지

심플하고 보통 구글에서 검색하며 나오는 날씨 확인하는 페이지와 비슷하게 작성됨

Openweather에서 API Key로 액세스한 후 받아오는 정보를 불러오는 페이지.

이를 기반으로 GUI를 추가하면 보다 나은 페이지가 가능

그리고 React 생성 및 어떻게 실행하는 지에 관한 지문도 나와있음.

## API Key 설정

API Key는 코드에 직접 넣지 않고 환경 변수로 읽어온다.

1. `weather-check/.env.example`을 복사하여 `weather-check/.env` 파일 생성
2. `REACT_APP_OPENWEATHER_API_KEY=` 뒤에 본인의 OpenWeatherMap API Key 입력
3. `npm start`로 실행 (`.env`는 `.gitignore`에 포함되어 커밋되지 않음)
