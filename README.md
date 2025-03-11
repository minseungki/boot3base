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

## 패키지 구조 설명
```
┌── data
│   └── ddl.sql (회원 / 게시판 샘플 ddl)
├── src
│   └── java
│       ├── com
│       │   └── example
│       │      └── demo
│       │          ├── advice (ExceptionHandler / AOP 모듈)
│       │          ├── annotation (Swagger 관련 custom annotation)
│       │          ├── config (공통 설정)
│       │          ├── controller
│       │          ├── dto
│       │          │  └── 업무명
│       │          │     └── enumeration (해당 업무관련 enum)
│       │          │     └── ~~Reuqest.java (요청 객체)
│       │          │     └── ~~Response.java (응답 객체)
│       │          ├── exception (custom Exception)
│       │          ├── filter
│       │          ├── http (http client)
│       │          ├── mapper (MyBatis Mapper Interface)
│       │          ├── security
│       │          ├── service
│       │          └── util (공통 util)
│       └── resource
│           └── mapper (mybatis xml 파일)
│           └── application.yml
│           └── application-dev.yml
│           └── application-local.yml
│           └── application-prod.yml
│           └── log4jdbc.log4j2.properties
│           └── logback-dev.yml
│           └── logback-local.yml
│           └── logback-prod.yml
│           └── lucy-xss-sax.xml (xss 설정파일)
│           └── lucy-xss-servlet-filter-rule.xml (xss 설정파일)
└── build.gradle
```

## JSON 응답 처리

### 응답 객체 정의
| 객체명     | 필수여부 | 타입                | 설명            |
|---------|------|-------------------|---------------|
| code    | Y    | String            | 에러코드          |
| message | Y    | String            | 에러메시지         |
| data    | N    | JSON / JSON Array | 결과데이터 / 에러 상세 |

### 성공 응답 객체

#### GET List
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다.",
    "data" : []
}
```

#### GET Page
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다.",
    "data" : [{}, {}]
}
```

#### GET Detail
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다.",
    "data" : {}
}
```

#### POST
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다."
}
```

#### PUT
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다."
}
```

#### DELETE
HTTP/1.1 200 OK
```json
{
    "code" : "SUC200_001",
    "message" : "처리가 완료되었습니다."
}
```

### 오류 응답 객체

#### 미 정의 오류
HTTP/1.1 400 Bad Request
```json
{
    "code" : "ERR400_001",
    "message" : "오류가 발생했습니다. 관리자에게 문의 하세요."
}
```
#### form validation 오류
HTTP/1.1 400 Bad Request
```json
{
    "code" : "ERR400_002",
    "message" : "유효성 검사에서 오류가 발생하였습니다.",
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
#### 토큰 만료 오류
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401_001",
    "message" : "토큰이 만료되었습니다."
}
```

#### 토큰 위변조 오류
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401_002",
    "message" : "토큰이 위변조되었습니다."
}
```

#### 토큰 누락 오류
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401_003",
    "message" : "토큰은 필수값입니다."
}
```

#### 그외 토큰 오류
HTTP/1.1 401 Unauthorized
```json
{
    "code" : "ERR401_999",
    "message" : "토큰 관련 오류가 발생했습니다. 관리자에게 문의 하세요."
}
```

#### 권한 부족 오류
HTTP/1.1 403 Forbidden
```json
{
    "code" : "ERR403_001",
    "message" : "접근이 거부되었습니다."
}
```

#### 상세 API 조회 실패 오류
HTTP/1.1 404 NOT_FOUND
```json
{
    "code" : "ERR404_001",
    "message" : "DATA NOT FOUND"
}
```

#### 서버 오류
HTTP/1.1 500 INTERNAL_SERVER_ERROR
```json
{
    "code" : "ERR404_001",
    "message" : "DATA NOT FOUND"
}
```

#### 시스템 점검 오류
HTTP/1.1 503 SERVICE_UNAVAILABLE
```json
{
    "code" : "ERR503_001",
    "message" : "시스템 점검 오류"
}
```

## 인증 방식
- 방식 : Bearer 방식
- 클라이언트는 API 요청 시, 로그인 API를 통해 발급 받은 JWT를 Authorization 헤더에 Bearer 방식으로 포함하여 서버에 전달합니다.

```
GET /api/bbs
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
- Swagger의 경우 개발 편의를 위해 Bearer는 생략합니다.

## 기능별 설명

### 캐싱
- Redis Cache
    - [조건]
        - Front 목록 : @Cacheable로 데이터 캐싱 (기본적으로 Service Method에서 처리함)
        - Admin 등록/수정/삭제 : @CacheEvic으로 데이터 삭제 (기본적으로 Mapper Interface에서 처리함)
    - [캐싱 방법]
        - @Cacheable 사용 방법
            - @Cacheable(value = RedisCacheKey.BBS, cacheManager = "cacheManager1Days", keyGenerator = "redisCacheKeyGenerator")
            - value : redis key (현재 RedisCacheKeyGenerator로 파라미터 내용도 key로 생성됨(중복 방지))
            - cacheManager : TTL 설정

        - @CacheEvict 사용 방법
            - @CacheEvict(value = RedisCacheKey.BBS_DEL, allEntries = true, cacheManager = "cacheManager")

    - [캐싱 시간 변경 방법]
        - @Cacheable(value = RedisCacheKey.BBS, cacheManager = "cacheManager10minutes", keyGenerator = "redisCacheKeyGenerator")
            - 위의 cacheManager10minutes 의 값을 RedisRepositoryConfig.cacheManager* 로 시작하는 함수명에서 해당하는 것을 작성하면 됨
            - 현재 선택 가능 한 목록(필요시 함수로 추가 가능)
                - cacheManager1seconds
                - cacheManager5seconds
                - cacheManager10seconds
                - cacheManager30seconds
                - cacheManager1minutes
                - cacheManager5minutes
                - cacheManager10minutes
                - cacheManager30minutes
                - cacheManager1Hour
                - cacheManager5Hour
                - cacheManager10Hour
                - cacheManager30Hour
                - cacheManager1Days
                - cacheManager5Days
                - cacheManager10Days
                - cacheManager30Days


### GlobalInterceptor.java
- 스웨거 접속 차단 기능 : 요청 URI가 Swagger-UI이고, 요청 Client IP가 해당 아이피가 아니면 접속불가
```
String ip = "127.0.0.1|0:0:0:0:0:0:0:1";

if ("/swagger-ui.html".equals(request.getRequestURI()) && !(ip.contains(RestUtil.getClientIp()))){
    throw new Exception("접속 금지");
}
```
- 앱버전 체크, 시스템 점검 등 공통 기능 처리

### 에러 처리

- 공통 에러 (ErrorCode.java)
```java
public enum ErrorCode {

    ERROR("ERR400_001", "오류가 발생했습니다. 관리자에게 문의 하세요.", "미 정의 에러"),
    INVALID_PARAM("ERR400_002", "유효성 검사에서 에러가 발생하였습니다.", "파라미터 에러"),

    ERR401_001("ERR401_001","토큰이 만료되었습니다.", "토큰 만료 에러"),
    ERR401_002("ERR401_002","토큰이 위변조되었습니다.", "토큰 위변조 에러"),
    ERR401_003("ERR401_003","토큰은 필수값입니다.", "토큰 누락 에러"),
    ERR401_999("ERR401_999","토큰 관련 오류가 발생했습니다. 관리자에게 문의 하세요.", "그외 토큰 에러"),

    ERR403_001("ERR403_001","접근이 거부되었습니다.", "권한 부족 에러"),
    ERR404_001("ERR404_001","DATA NOT FOUND", "No Data 에러"),

    SERVER_ERROR("ERR500_001", "장애가 발생했습니다. 관리자에게 문의 하세요.", "서버 내부 오류"),
    SYSTEM_INSPECTION("ERR503_001", "시스템 점검 상태 입니다.", "시스템 점검 오류"),
    ;

    private final String code;
    private final String message;
    private final String desc;

}
```

- 서비스 에러 처리
```java
@Getter
@AllArgsConstructor
private enum LoginERRCd implements CustomErrorCode {
    ERR_LOGIN_001("일치하는 회원 정보가 없는 경우"),
    ERR_LOGIN_002("리프레시 토큰이 없는 경우"),
    ;
    private final String desc;
}

@Transactional
public LoginResponse login(LoginRequest req) {
    // 회원 조회
    LoginSelectResponse member = mapper.selectLogin(req);

    if (ObjectUtils.isEmpty(member) || !sha256Util.encrypt(req.getPassword()).equals(member.getPassword())) {
        // 아이디가 없거나 비밀번호 오류
        throw new CustomException(LoginERRCd.ERR_LOGIN_001);
    } else {
        // 정상인 경우
        return responseLogin(member, true);
    }
}
```
- Exception Handling
  - exception 발생시 AdviceController.java class에서 ExceptionHandling 처리
```
public class Service {
    public void method() {
        throw new CustomException(...);
    }
}

@ExceptionHandler(value = {CustomException.class})
public ResponseEntity<?> customException(final CustomException e) {
    log.error("CustomException : {}",e);
    return RestUtil.error(e.getCode(), e.getMessage());
}
```

- 에러 로깅
  - request 정보, printStackTrace 정보 로깅 (RestUtil.java)
  - pram, body 가져올 때 개인 정보 ***마스킹 처리

```java
public static void printStackTraceInfo(final Exception error) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    log.error("====================================================================");
    log.error("Exception \t\t: " + error.getClass().getSimpleName());
    log.error("Request URI \t\t: " + request.getRequestURI());
    log.error("Request Method \t: " + request.getMethod());

    String param = RequestUtil.removeMapInKey(request);
    if (StringUtils.hasText(param)) {
        log.error("Param \t: " + param);
    }

    String body = RequestUtil.removeJsonObjectInKey(request);
    if (StringUtils.hasText(body)) {
        log.error("Body \t: " + body);
    }

    if (!ObjectUtils.isEmpty(error.getCause())) {
        log.error("Cause \t\t\t: " + error.getCause());
    }

    if (StringUtils.hasText(error.getMessage())) {
        log.error("Message \t\t\t: " + error.getMessage());
    }

    String accessToken = request.getHeader(HEADER_AUTH);
    if (StringUtils.hasText(HEADER_AUTH) && StringUtils.hasText(accessToken)) {
        log.error("Access Token \t: " + accessToken);
    }

    String refreshToken = request.getHeader(HEADER_AUTH_REFRESH);
    if (StringUtils.hasText(HEADER_AUTH_REFRESH) && StringUtils.hasText(refreshToken)) {
        log.error("Refresh Token \t: " + refreshToken);
    }

    for (StackTraceElement element : error.getStackTrace()) {
        String target = element.toString();
        if (target.contains("com.example.demo") && !target.contains("<generated>")) {
            log.error("at " + element);
        }
    }
    log.error("====================================================================");
}
```

### LogAdvice.java
- request 정보 로깅
- pram, body 가져올 때 개인 정보 ***마스킹 처리
```java
@Around("com.example.demo.advice.LogAdvice.onRequest()")
public Object requestLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    long start = System.currentTimeMillis();
    try {
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    } finally {
        //헬스체크, 파일 저장 예외
        if (!"/hc".equals(request.getRequestURI()) && !isFilterWhiteList(request.getServletPath())) {

            long end = System.currentTimeMillis();

            String param = RequestUtil.removeMapInKey(request);
            String body = RequestUtil.removeJsonObjectInKey(request);

            log.debug("Request: {} {}: {} {} ({}ms)", request.getMethod(), request.getRequestURL(), param, body, end - start);
        }
    }
}
```

### JWT (JwtUtil.java)
- 토큰 정보 (info) Aes256방식 암호화
```java
public String generateToken(JwtAuthenticationVo jwtAuthenticationVo) {
    jwtAuthenticationVo.setProfile(ProfileType.valueOf(profile));

    if (JwtType.access.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
        jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_EXPIRATION * 1000 * 60));
    } else if (JwtType.refresh.getDesc().equals(jwtAuthenticationVo.getJwtType().getDesc())) {
        jwtAuthenticationVo.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRATION * 1000 * 60));
    }

    Map<String, Object> claims = new HashMap<>() {{
        put(CLAIMS_KEY, aes256Util.encrypt(RestUtil.classToJsonStr(jwtAuthenticationVo)));
    }};

    return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(aes256Util.encrypt(jwtAuthenticationVo.getPlatformType().getDesc()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(jwtAuthenticationVo.getExpiration())
            .signWith(getSignInKey(), SignatureAlgorithm.HS512)
            .compact();
}
```
- 토큰 정보 (JwtAuthenticationVo)
```java
public class JwtAuthenticationVo {

    private Long seq;
    private String id;
    private PlatformType platformType; // front, admin
    private JwtType jwtType; // access token, refresh token
    private ProfileType profile; // dev, prod
    private Date expiration;
    private Role role;

}
```

### Swagger (SpringDocConfig.java)
[스웨거 가이드 (클라이언트)](./doc/client.md)

[스웨거 가이드 (백엔드)](./doc/backend.md)