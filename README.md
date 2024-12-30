# 이 프로젝트는... Spring Boot 3.x Base 프로젝트 입니다.

## 프로젝트 목표
- Spring Boot 버전 업 (2.7.x -> 3.x.x)로 인한 기본 아키텍쳐 수정 및 Swagger API 의존성 수정
- Java 버전 업
- 멀티 모듈 형태의 프레임 워크 아키텍쳐
- -> Generator Update 

## 스펙
- 프레임 워크: Spring Boot(3.3.5), Spring MVC(6.1.14)
- 언어: Java(OpenJDK 17)
- 데이터 베이스: H2(in-memory)
- Persistence Framework: MyBatis(3.5.9), SQL Mapper (현재) -> 이후 JPA (ORM) 변경
- 인증 및 인가: Spring Security(6.4.4), JWT(Json Web Token)
- API 문서화: springdoc-openapi (2.1.0)
- 의존성 관리: Gradle
- 로깅: Logback(1.5.11)
- 예외 처리: @ControllerAdvice, @ExceptionHandler
- XSS 필터 : naver/lucy-xss-servlet-filter
- RESTful API 디자인: RESTful API 설계 원칙을 따름
- 서비스 계층: 비즈니스 로직 처리
- 데이터 전송: JSON 형식

## JSON 응답 처리

### 응답 객체 정의
| 객체명     | 필수여부     | 타입                | 설명            |
|---------|----------|-------------------|---------------|
| code    | Required | String            | 에러코드          |
| message | Required | String            | 에러메시지         |
| data    | Optional | JSON / JSON Array | 결과데이터 / 에러 상세 |

### 성공 응답 객체

#### GET List
HTTP/1.1 200 OK
```json
{
    "code" : "success",
    "message" : "처리가 완료되었습니다.",
    "data" : [{}, {}]
}
```

#### GET Detail
HTTP/1.1 200 OK
```json
{
    "code" : "success",
    "message" : "처리가 완료되었습니다.",
    "data" : {}
}
```

#### POST
HTTP/1.1 200 OK
```json
{
    "code" : "success",
    "message" : "처리가 완료되었습니다."
}
```

#### PUT
HTTP/1.1 200 OK
```json
{
    "code" : "success",
    "message" : "처리가 완료되었습니다."
}
```

#### DELETE
HTTP/1.1 200 OK
```json
{
    "code" : "success",
    "message" : "처리가 완료되었습니다."
}
```

### 에러 응답 객체

#### 미 정의 에러
HTTP/1.1 400 Bad Request
```json
{
    "code" : "ERR100",
    "message" : "오류가 발생했습니다. 관리자에게 문의 하세요."
}
```

#### 토큰 만료
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401",
    "message" : "정상적인 서비스 이용을 위해 로그인이 필요합니다."
}
```

#### 토큰 변조
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401",
    "message" : "정상적인 서비스 이용을 위해 로그인이 필요합니다."
}
```

#### form validation 에러
HTTP/1.1 400 Bad Request
```json
{
    "code" : "ERR301",
    "message" : "유효성 검사에서 에러가 발생하였습니다.",
    "data": {
	    "errors": [
		    {
			    "field": "title",
			    "message": "제목은 필수값입니다."
		    },
		    {
			    "field": "contents",
			    "message": "내용은 필수값입니다."
		    }
		]
	}
}
```

## 기본 적용 대상
- JWT 인증 토큰 기능 구현
- springdoc-openapi를 통한 Swagger 기능 구현
- 공통 에러 처리