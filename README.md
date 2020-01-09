# 지자체 협약 지원 API 개발
## 개발 프레임워크
* Language : Java SE 8
* Framework: Spring Boot 2.2.2
* Database: H2database

## 문제 해결 방법
### 데이터
- 지자체 정보 엔티티는 지자체코드를 두어 테이블을 분리하세요.

##### MAS_INFO (지자체 지원 정보 엔티티)
|Column|Type|Description|
|---|----|----|
|id|Long|ID|
|regionCd|String|지자체 코드|
|target|String|지원대상|
|usage|String|용도|
|limited|String|지원한도|
|rate|String|이차보전|
|institute|String|추천기관|
|mgmt|String|관리점|
|reception|String|취급점|
|createdDate|LocalDateTime|생성일자|
|modifiedDate|LocalDateTime|수정일자|

##### MUNICIPAL_INFO (지원 지자체(기관))
|Column|Type|Description|
|---|----|----|
|region|String|지자체명|
|regionCd|String|지자체 코드|


### API 기능 명세
|Method|URL|Description|
|---|----|----|
|POST|/api/municipal|데이터파일에서 DB저장 API|
|GET|/api/municipal-all|지원하는 지자체 목록 검색 API|
|GET|/api/municipal|지원하는 지자체명을 입력 받아 해당 지자체의 지원정보를 출력하는 API|
|PUT|/api/municipal|지원하는 지자체 정보 수정 기능 API|
|GET|/api/municipal/limit-{cnt}|지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비율이 적은 순서)하여 특정 개수만 출력하는 API|
|GET|/api/municipal/rate-min-top|이차보전 비율이 가장 작은 추천 기관명을 출력하는 API|
|GET|/api/municipal/recommend|특정 기사를 분석하여 본 사용자는 어떤 지자체에서 금융지원을 받는게 가장 좋을지 지자체명을 추천하는 API|
|POST|/api/signup|계정생성 API|
|POST|/api/signin|로그인 API|
|POST|/api/refresh|토큰 재발급 API|

### 기본 문제
- 데이터 파일에서 각 레코드를 데이터베이스에 저장하는 API 개발
>- POST /api/municipal 호출한다. 
>- CSV 파일을 읽어 들여 DB에 지자체 정보 엔티티는 지자체코드를 두어 테이블을 분리하여 저장한다.  
>- 저장이 완료되면 저장된 지원하는 지자체 목록을 검색하여 출력한다. 
```
[
	{
		“region”: “강릉시”,
		“target”: “강릉시 소재 중소기업으로서 강릉시장이 추천한 자”,
		“usage”: “운전”,
		“limit” : “추천금액 이내”,
		“rate”: “3.00%”,
		“institute”: “강릉시”,
		“mgmt”: “강릉지점”,
		“reception”: “강릉시 소재 영업점”},
	…
	{ 
		“region”: “횡성군”,
		“target”: “횡성군 소재 중소기업으로서 횡성군수의 융자 추천을 받은 자”,
		“usage”: “운전”,
		“limit” : “1 억원 이내”,
		“rate”: “4.00%”,
		“institute”: “횡성군”,
		“mgmt”: “원주지점”,
		“reception”: “횡성군이 지정한 영업점” }
]
```

- 지원하는 지자체 목록 검색 API 개발
>- GET /api/municipal-all 호출한다.
>- 지원하는 지자체 목록을 검색하여 출력한다.
```
[
	{
		“region”: “강릉시”,
		“target”: “강릉시 소재 중소기업으로서 강릉시장이 추천한 자”,
		“usage”: “운전”,
		“limit” : “추천금액 이내”,
		“rate”: “3.00%”,
		“institute”: “강릉시”,
		“mgmt”: “강릉지점”,
		“reception”: “강릉시 소재 영업점”},
	…
	{ 
		“region”: “횡성군”,
		“target”: “횡성군 소재 중소기업으로서 횡성군수의 융자 추천을 받은 자”,
		“usage”: “운전”,
		“limit” : “1 억원 이내”,
		“rate”: “4.00%”,
		“institute”: “횡성군”,
		“mgmt”: “원주지점”,
		“reception”: “횡성군이 지정한 영업점” }
]
```
- 지원하는 지자체명을 입력 받아 해당 지자체의 지원정보를 출력하는 API
>- GET /api/municipal 다음과 같이 입력 값을 Body에 포함하여 호출한다.
```
{
	“region”: “강릉시”
}
```
>- 입력 된 지자체명을 통하여 지자체 코드를 확인 후 지차체 정보를 검색하여 출력한다.
```
{
	“region”: “강릉시”,
	“target”: “강릉시 소재 중소기업으로서 강릉시장이 추천한 자”,
	“usage”: “운전”,
	“limit” : “추천금액 이내”,
	“rate”: "3%”,
	“institute”: “강릉시”,
	“mgmt”: “강릉지점”,
	“reception”: “강릉시 소재 영업점"
}
```
- 지원하는 지자체 정보 수정 기능 API 개발
>- PUT /api/municipal 다음과 같이 입력 값을 Body에 포함하여 호출한다.
```
{
	“region”: “강릉시”,
	“target”: “강릉시 소재 중소기업으로서 강릉시장이 추천한 자”,
	“usage”: “운전”,
	“limit” : “추천금액 이내”,
	“rate”: "4%”,
	“institute”: “강릉시”,
	“mgmt”: “강릉지점”,
	“reception”: “강릉시 소재 영업점"
}
```
>- 입력 된 지자체명을 통하여 지자체 코드를 확인 후 지자체 정보를 수정한 뒤 수정된 지자체 정보를 출력한다.
```
{
	“region”: “강릉시”,
	“target”: “강릉시 소재 중소기업으로서 강릉시장이 추천한 자”,
	“usage”: “운전”,
	“limit” : “추천금액 이내”,
	“rate”: "4%”,
	“institute”: “강릉시”,
	“mgmt”: “강릉지점”,
	“reception”: “강릉시 소재 영업점"
}
```
- 지원한도 컬럼에서 지원금액으로 내림차순 정렬(지원금액이 동일하면 이차보전 평균 비율이 적은 순서)하여 특정 개수만 출력하는 API
>- GET /api/municipal/limit-{cnt} 에서 {cnt}에 특정 숫자를 넣고 호출한다.
>- PathVariable 변수 cnt를 확인하여 특정 갯수 만큼 출력한다.
>- 지원금액은 "추천금액 이내"는 지원 금액을 특정할 수 없었기 때문에 0으로 처리하였고, 이차보전비율의 경우 "대출이자 전액"의 비율을 0으로 처리 한다.
>- GET /api/municipal/limit-5 를 호출 하는경우 다음과 같이 출력된다.
```
{
	경기도, 제주도, 국토교통부, 인천광역시, 안양시
}
```
- 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명을 출력하는 API
>- GET /api/municipal/rate-min-top 을 호출한다. 
>- 이차보전비율의 경우 "대출이자 전액"의 비율을 0으로 처리 한다.
>- 다음과 같이 출력된다. 
```
{
	안산시
}
```

### 선택 문제
- 특정 기사를 분석하여 본 사용자는 어떤 지자체에서 금융지원을 받는게 가장 좋을지 지자체명을 추천하는 API
>- __미구현__ - 요소별로 입력 문자열에 맞는 비교 검색 알고리즘을 사용
>- 입력된 지역 및 중소기업은행 지자체 협약 지원 정보 데이터의 지자체의 좌표(위도, 경도) 임의로 추가 한다.
>- 입력된 지역의 좌표와 조건에 맞는 지자체 목록을 로드하여 좌표간 거리 계산으로 거리 도출 후 오름차순으로 정렬하여 가장 가까운 첫번째 지자체 상세 정보를 표출 한다.

### 추가 제약사항(옵션) 
#### API 인증을 위해 JWT(Json Web Token)를 이용해서 Token 기반 API 인증 기능을 개발하고 각 API 호출 시에 HTTP Header 에 발급받은 토큰을 가지고 호출하세요.
- signup 계정생성 API
>- POST /api/signup 다음과 같이 입력 값을 Body에 포함하여 호출한다.
```
{
    "username": "admin",
    "password": "kakaopay"
}
```
>- 사용자 ID, PW를 받아 내부 DB에 저장하고 토큰을 생성하여 출력한다. 
```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU3ODUwNzMwMywiaWF0IjoxNTc4NTAwMTAzfQ.ZIR1A-jAooV84QLVaMIJu18HLcVWUFy_aORUq3S-7Tl20-dnoklki4Ln_esEjRT-ISTpSMlGjLHkxS50h_17EQ"
}
```

- signin 로그인 API
>- POST /api/signin 다음과 같이 입력 값을 Body에 포함하여 호출한다.
```
{
    "username": "admin",
    "password": "kakaopay"
}
```
>- 사용자 ID, PW를 받아 인증 후 토큰을 발급하여 출력한다.
```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU3ODUwNzMyNCwiaWF0IjoxNTc4NTAwMTI0fQ.JwG_2hSD2eJXNN_GfkSDYkyQFZXejxYBwMDPUg2FE-QltJa8qY341GCGSSqAzodJ-Ah60RHo1Xim8xDi__p6Tg"
}
```

- refresh 토큰 재발급 API
>- POST /api/refresh 다음과 같이 입력 값을 Header에 포함하여 호출한다.
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU3ODUwNzMyNCwiaWF0IjoxNTc4NTAwMTI0fQ.JwG_2hSD2eJXNN_GfkSDYkyQFZXejxYBwMDPUg2FE-QltJa8qY341GCGSSqAzodJ-Ah60RHo1Xim8xDi__p6Tg
```
>- 토큰을 확인하여 토큰을 재발급하여 출력한다.
```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU3ODUwNzM1MywiaWF0IjoxNTc4NTAwMTUzfQ.AAbYYB3vGv6J8Ad42H7xz6vA645N0a49CTE8wKAkk0QWyJsZQj8SbZtDOhn2j7PRWOj37G6-qcArgZJq7yrbkA"
}
```
 
## 빌드 및 실행 방법
* 이클립스에서 GIT으로 프로젝트를 Import를 한다.
* 프로젝트 우클릭 Gradle -> Refresh Gradle Project를 한다.
* Boot Dashboad 에서 해당 프로젝트를 Start를 한다.  
