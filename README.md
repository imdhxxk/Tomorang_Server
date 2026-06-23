# Tomorang Backend

> 개인 관광 가이드 플랫폼 — **발견자(여행자)** 와 **안내자(가이드)** 를 연결하는 투어 예약·채팅 서비스의 백엔드 API 서버

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=springboot&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-DC382D)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)

---

## 📖 소개

Tomorang은 여행자가 현지 가이드의 투어 상품을 찾아 **예약**하고, 가이드와 **실시간 채팅**으로 소통하며, 투어 후 **리뷰**를 남길 수 있는 플랫폼입니다. 이 저장소는 REST API + WebSocket(STOMP) 기반 백엔드입니다.

- **발견자(DISCOVERER/TRAVELER)**: 투어 검색·찜·예약 신청·리뷰 작성
- **안내자(GUIDE)**: 투어 게시물 등록·예약 수락/거절·채팅 응대
- 회원은 역할(GUIDE ↔ DISCOVERER)을 전환할 수 있습니다.

## 🛠 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| Language / Runtime | Java 17 |
| Framework | Spring Boot 3.3.5 (Web, Security, WebSocket, Thymeleaf) |
| Persistence | MyBatis 3.0.3, MySQL 8.0 |
| Auth | Spring Security + JWT (jjwt 0.11.5) |
| Realtime | WebSocket + STOMP |
| Storage | AWS S3 (spring-cloud-aws 2.4.4) — 프로필/게시물/리뷰 이미지 |
| API Docs | springdoc-openapi (Swagger UI) 2.5.0 |
| 번역 | DeepL API (채팅 메시지 번역) |
| Build | Gradle |

## ✨ 주요 기능

| 도메인 | 설명 |
|--------|------|
| 회원 | 회원가입·로그인(JWT)·프로필 조회/수정·탈퇴·역할 전환·인기 안내자 목록 |
| 게시물 | 투어 등록·수정·삭제·목록/검색(키워드·도시·국가·가이드)·상세(이미지·본문블록·태그·일정·시간슬롯) |
| 예약 | 발견자 예약 신청(PENDING) → 가이드 수락(CONFIRMED)/거절(REJECTED), 정원·동시성 관리 |
| 채팅 | 채팅방 생성/조회, 메시지 전송·히스토리·읽음 처리 (실시간: WebSocket STOMP) |
| 리뷰 | 별점(1~5)·내용·이미지 리뷰 작성, 좋아요, 게시물 평점 자동 갱신 |
| 찜 | 게시물 찜 추가/취소 (발견자 전용) |
| 알림 | 예약 요청/확정/거절·리뷰 작성 시 자동 생성, 조회·읽음 처리 |
| 신고 | 게시물 신고 (중복 방지, 신고 시 자동 숨김 연동) |
| 숨김 사용자 | 특정 사용자 숨김/해제 및 목록 |
| 번역 | DeepL 기반 채팅 메시지 번역 |

## 📚 API 문서

서버 실행 후 Swagger UI에서 전체 명세 확인:

```
http://localhost:8081/swagger-ui/index.html      # 로컬
https://tomorang.mirim-it-show.site/swagger-ui/index.html   # 배포
```

- OpenAPI JSON: `/api-docs`
- 인증이 필요한 API는 우측 상단 **Authorize**에 JWT 토큰 입력 (`Bearer` 접두어 생략)

## 🗂 프로젝트 구조

```
src/main/java/kr/hs/after/Tomorang/
├── Controller/     # REST 엔드포인트 (member, post, reservation, chat, review, ...)
├── Service/        # 비즈니스 로직 (도메인별, post 하위 패키지 포함)
├── DAO/            # MyBatis 매퍼 인터페이스
├── DTO/            # 요청/응답 객체
├── Security/       # SecurityConfig (BCrypt, CORS 등)
├── jwt/            # JWT 발급·검증·블랙리스트
├── Config/         # OpenAPI 등 설정
└── model/          # 도메인 모델

src/main/resources/
├── mybatis/mapper/ # SQL 매퍼 XML
└── application.properties
```

## 🚀 시작하기

### 사전 요구사항

- JDK 17
- MySQL 8.0 (스키마 `Tomorang` 생성 필요)
- AWS S3 버킷 (이미지 업로드용), DeepL API Key (번역 기능)

### 환경 변수

`application.properties`는 환경 변수로 주입받습니다. (괄호 안은 기본값)

| 변수 | 설명 |
|------|------|
| `PORT` | 서버 포트 (기본 `8081`) |
| `MYSQL_HOST` / `MYSQL_PORT` | DB 호스트 (`localhost`) / 포트 (`3306`) |
| `MYSQL_DATABASE` | DB 이름 (`Tomorang`) |
| `MYSQL_USER` / `MYSQL_PASSWORD` | DB 계정 / 비밀번호 |
| `AWS_S3_BUCKET` / `AWS_REGION` | S3 버킷 / 리전 (`ap-northeast-2`) |
| `AWS_ACCESS_KEY` / `AWS_SECRET_KEY` | AWS 자격 증명 |
| `JWT_SECRET` | JWT 서명 키 (HS384, 충분히 긴 문자열) |
| `DEEPL_API_KEY` | DeepL 번역 API 키 |

> ⚠️ 실제 자격 증명은 저장소에 커밋하지 말고 환경 변수로 주입하세요.

### 빌드 & 실행

```bash
# 빌드 (실행 가능한 jar 생성)
./gradlew bootJar

# 실행
java -jar build/libs/Tomorang-0.0.1-SNAPSHOT.jar

# 또는 개발 모드
./gradlew bootRun
```

환경 변수 예시 (로컬):

```bash
export MYSQL_USER=root MYSQL_PASSWORD=yourpw
export JWT_SECRET=your-long-secret-key
export AWS_ACCESS_KEY=... AWS_SECRET_KEY=... DEEPL_API_KEY=...
java -jar build/libs/Tomorang-0.0.1-SNAPSHOT.jar
```

## 🔌 주요 엔드포인트 요약

| 메서드 | 경로 | 설명 |
|--------|------|------|
| `POST` | `/api/signup` | 회원가입 (비번 ≥ 8자, 이메일 형식, 닉네임 ≤ 8자 검증) |
| `POST` | `/api/login` | 로그인 → JWT 발급 |
| `GET`  | `/api/mypage` | 내 프로필 + 찜 목록 |
| `GET` / `POST` | `/api/post` | 게시물 목록·검색 / 등록 |
| `GET` / `PUT` / `DELETE` | `/api/post/{id}` | 게시물 상세 / 수정 / 삭제 |
| `POST` | `/api/reservation` | 예약 신청 (발견자) |
| `PATCH` | `/api/reservation/{id}/accept` · `/reject` | 예약 수락 / 거절 (가이드) |
| `GET` | `/api/reservation/my` | 내 예약 목록 |
| `POST` / `GET` | `/api/review` · `/api/review/post/{postId}` | 리뷰 작성 / 게시물 리뷰 목록 |
| `POST` / `DELETE` | `/api/wishlist/{postId}` | 찜 추가 / 취소 |
| `GET` | `/api/notifications` | 내 알림 목록 |
| `GET` | `/api/guides/popular` | 인기 안내자 목록 |
| `POST` | `/api/chat/message` · `WS /ws` | 채팅 (REST / WebSocket STOMP) |

전체 목록과 요청/응답 스키마는 Swagger UI 참고.

## 🗄 데이터베이스

주요 테이블:

```
member, member_language
posts, post_images, post_contents, post_tags, post_schedules, time_slots
reservations
reviews, review_images, review_likes
wishlists
notifications
hidden_user
reports
chat_rooms, chat_messages
```

- 비밀번호는 **BCrypt**(Spring Security `BCryptPasswordEncoder`, cost 10)로 해싱 저장됩니다.

## 🚢 배포

- **빌드**: `./gradlew bootJar`
- **운영**: AWS EC2(Ubuntu)에서 `systemd` 서비스로 jar 실행, Nginx 리버스 프록시
- CI/CD는 별도 미설정 (jar 교체 후 서비스 재시작 방식)

---

<sub>개인 관광 가이드 플랫폼 Tomorang · Spring Boot 백엔드</sub>
