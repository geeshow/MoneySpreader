# MoneySpreader

## 목차

## 개발 환경
- Language: Java 11
- Framework: Spring Boot 2.3.7.RELEASE
- Database: H2
- Dependencies: jpa / hateoas / mapstruct / lombok / restdocs

## 핵심 문제해결 전략
### 가. 핵심 요구사항 해결 방법
##### 1. 다수의 서버 다수의 인스턴스에 대한 데이터 정합성
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

## API 가이드

### 카카오페이 뿌리기 REST API Guide

| Name | Description
| --- | --- |
|`+X-USER-ID+` | 사용자 ID |
|`+X-ROOM-ID+` | 대화방 ID |


TODO
출금처리 위치 변경(정규화 해야함)
Exception 계층 추가(HTTP ERROR STATUS 별로 작업)