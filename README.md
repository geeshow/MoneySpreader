# MoneySpreader

## 목차

## 개발 환경
- Language: Java 11
- Framework: Spring Boot 2.3.7.RELEASE
- Database: H2
- Dependencies: jpa / hateoas / mapstruct / lombok / restdocs

## 핵심 문제해결 전략
### 가. 핵심 요구사항 해결 방법
##### 1. 다수의 서버, 다수의 인스턴스에 대한 데이터 정합성
  - Service Transactional Annotation 적용
  - JPA Version Lock 사용
##### 2. 토큰 유틸리티 개발
  - SecureRandom으로 예측 불가능한 난수 값 생성.
##### 3. 티켓(뿌리기용) 생성자 유틸리티 개발 
  - 주어진 금액을 주어진 건수로 랜덤하게 나누는 유틸리티.
  - 생성된 티켓들을 다시 한번 더 무작위 Sorting(Comparable 인터페이스 적용).
##### 4. 조회 및 받기 유효시간
  - 조회유효시간, 받기유효시간 컬럼을 추가
##### 5. 실패 응답 처리
  - RestControllerAdvice 적용
  - Exception을 HTTP status 단위로 계층적 분리 
     
### 나. 개발규칙 정의
##### 1. 상수 사용
  - 같은 상수를 반복 사용 자제.
  - Service, Controller에서만 사용.
  - 유틸리티성 패키지는 별도의 상수 사용.
##### 2. Controller 컴포넌트의 역할
  - 데이터 검증(DB 조회 검증은 하지 않는다.)
  - Service 컴포넌트 호출(필요시 Repository 조회)
  - 응답값 맵핑
##### 3. Service 컴포넌트의 역할
  - 비지니스 로직(청사진) / 컴포넌트 간의 연결고리 역할.
  - 입력값 검증(DB 조회 검증).
  - return 값은 해당되는 Domain을 넘긴다.
##### 4. Domain 컴포넌트의 역할 
  - 단위 업무 로직 구현.
  - 예외처리를 발생하지 않음.
##### 5. 그 외
  - null을 사용하지 않음(Optional 활용).
  - DTO Naming 규칙 : [요청/응답] + 메소드명 + DTO
  
 
## 기능별 플로어
### 1. 뿌리기
![뿌리기플로어](https://user-images.githubusercontent.com/20357042/103145026-c7478c00-4776-11eb-805f-572e5487b27f.png)
 
### 2. 받기
![받기플로어](https://user-images.githubusercontent.com/20357042/103145712-07603c00-4782-11eb-9994-2a4d41907298.png)

### 3. 조회
![조회플로어](https://user-images.githubusercontent.com/20357042/103145231-2d81de00-477a-11eb-8e53-43bc96b1d88e.png)

## 카카오페이 뿌리기 REST API Guide

### 공통 Header
| Name | Description
| --- | --- |
|X-USER-ID | 사용자 ID |
|X-ROOM-ID | 대화방 ID |

### 뿌리기 API
#### 요청
- method : POST
- url : /v1/spreder

|Path|Type|Description|
|---|---|---|
|amount|Number|뿌리기 금액|
|number|Number|받을 대상 맴버수|
- request sample
```
POST /v1/spreader HTTP/1.1
Content-Type: application/json;charset=UTF-8
X-USER-ID: 9
X-ROOM-ID: X-ROOM-ID-20
Accept: application/hal+json
Content-Length: 27
Host: docs.geeshow.com:8080

{"amount":20000,"number":3}
```
#### 응답
| Path | Type | Description |
| --- | --- | --- |
| token | String | 뿌리기 token |
- response sample
```
HTTP/1.1 201 Created
Location: http://docs.geeshow.com:8080/v1/spreader/yan/yan
Content-Type: application/hal+json
Content-Length: 370

{
  "token" : "yan",
  "_links" : {
    "self" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader"
    },
    "read" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader/yan"
    },
    "receipt" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader/receipt/roomId"
    },
    "profile" : {
      "href" : "/docs/index.html#spreader"
    }
  }
}
```
### 뿌리기 조회 API
#### 요청
- method : get
- url : /v1/spreder/{token}
- request sample
```
GET /v1/spreader/B3x HTTP/1.1
Content-Type: application/json;charset=UTF-8
X-ROOM-ID: X-ROOM-ID-20
X-USER-ID: 17
Accept: application/hal+json
Host: docs.geeshow.com:8080
```

#### 응답
|Path|Type|Description|
|---|---|---|
|spreadDatetime|String|뿌린 시각|
|spreadAmount|Number|뿌린 금액|
|receiptAmount|Number|현재까지 받은 금액|
|receipts[].userId|Number|받은 사용자 ID|
|receipts[].amount|Number|받은 금액|
- response sample
```
HTTP/1.1 200 OK
Content-Type: application/hal+json
Content-Length: 514

{
  "spreadDatetime" : "2020-12-26T16:29:11.166892",
  "spreadAmount" : 10000,
  "receiptAmount" : 620,
  "receipts" : [ {
    "userId" : 18,
    "amount" : 620
  } ],
  "_links" : {
    "self" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader/B3x"
    },
    "spreader" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader"
    },
    "receipt" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader/receipt/B3x"
    },
    "profile" : {
      "href" : "/docs/index.html#read"
    }
  }
}
```

### 받기 API
#### 요청
- method : put
- url : /v1/spreder/receipt/{token}
- request sample
```
PUT /v1/spreader/receipt/H2d HTTP/1.1
Content-Type: application/json;charset=UTF-8
X-USER-ID: 2
X-ROOM-ID: X-ROOM-ID-20
Accept: application/hal+json
Host: docs.geeshow.com:8080
```
#### 응답
|Path|Type|Description|
|---|---|---|
|amount|Number|받은 금액|
- response sample
```
HTTP/1.1 200 OK
Content-Type: application/hal+json
Content-Length: 291

{
  "amount" : 317,
  "_links" : {
    "self" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader/receipt/X-ROOM-ID-20"
    },
    "spreader" : {
      "href" : "http://docs.geeshow.com:8080/v1/spreader"
    },
    "profile" : {
      "href" : "/docs/index.html#receipt"
    }
  }
}
```
### 오류코드
|Path|Type|Description|
|---|---|---|
|timestamp|String|오류 발생 시간|
|status|String|ERROR HTTP STATUS|
|statusCode|Number|ERROR HTTP STATUS CODE|
|message|String|기본 오류 메시지|
|detailMessage|String|Stack trace|
|detailErrors|Array|오류 상세 메시지(해당 시)|
|detailErrors[].object|String|오류 발생 객체|
|detailErrors[].field|String|오류 필드|
|detailErrors[].rejectedValue|Number|오류 발생 값|
|detailErrors[].message|String|오류 메시지|
- response sample
```
HTTP/1.1 400 Bad Request
Content-Type: application/hal+json
Content-Length: 1090

{"timestamp":"2020-12-26T16:29:11.250093"
,"status":"BAD_REQUEST"
,"statusCode":400
,"message":"BODY에 필수 값이 잘못되었습니다."
,"detailMessage":"org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [2] in public org.springframework.http.ResponseEntity<com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto$ResponseSpreadDto> com.geeshow.kakaopay.MoneySpreader.controller.SpreaderController.spread(java.lang.String,int,com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto$RequestSpreadDto): [Field error in object 'requestSpreadDto' on field 'number': rejected value [0]; codes [Positive.requestSpreadDto.number,Positive.number,Positive.int,Positive]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [requestSpreadDto.number,number]; arguments []; default message [number]]; default message [뿌리기 인원 오류. 양수로 입력해야 합니다.]] "
,"detailErrors":[
    {"object":"requestSpreadDto"
    ,"field":"number"
    ,"rejectedValue":0
    ,"message":"뿌리기 인원 오류. 양수로 입력해야 합니다."}
]}
```
TODO
출금처리 위치 변경(정규화 해야함)
Exception 계층 추가(HTTP ERROR STATUS 별로 작업)