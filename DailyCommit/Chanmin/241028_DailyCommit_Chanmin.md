
카카오 OAuth를 사용하여 사용자의 로그인과 인증을 처리하는 과정은 다음과 같은 단계로 진행됩니다. 이 플로우에서는 **Next.js 프론트엔드**와 **Spring Cloud Gateway, Auth 서비스**가 함께 동작하여 카카오 로그인 절차를 처리하게 됩니다.

### Kakao OAuth 플로우 단계별 설명

### 1. 사용자 로그인 요청 (Next.js → Kakao OAuth)

- 사용자가 로그인 버튼을 클릭하면 **Next.js 프론트엔드**에서 **카카오 OAuth 로그인 페이지**로 리다이렉트합니다.
- 이때 카카오로 요청할 URL에는 **client_id (앱 키)**, **redirect_uri (인증 완료 후 돌아올 URL)**, **response_type=code**를 포함합니다.
- 예시 URL:
    
    ```css
    css
    코드 복사
    https://kauth.kakao.com/oauth/authorize?client_id={REST_API_KEY}&redirect_uri={REDIRECT_URI}&response_type=code
    
    ```
    

### 2. 카카오 로그인 및 인증 코드 수신 (Kakao OAuth → Next.js)

- 사용자가 카카오 로그인 페이지에서 로그인하면 카카오는 **인증 코드 (authorization code)**를 `redirect_uri`에 포함하여 **Next.js 프론트엔드로 리다이렉트**합니다.
- Next.js 프론트엔드는 이 인증 코드를 받아서 **백엔드로 전달**합니다.

### 3. 인증 코드로 토큰 요청 (Next.js → Auth 서비스)

- Next.js에서 인증 코드를 받은 후, 이를 Spring Cloud Gateway를 통해 **Auth 서비스로 전달**합니다.
- Auth 서비스는 이 인증 코드를 사용해 **카카오의 토큰 엔드포인트에 요청**하여 **엑세스 토큰**과 **리프레시 토큰**을 발급받습니다.
- 예시 요청 (Auth 서비스 → 카카오):
    
    ```arduino
    arduino
    코드 복사
    POST https://kauth.kakao.com/oauth/token
    
    ```
    
- 요청 파라미터에는 `grant_type=authorization_code`, `client_id`, `redirect_uri`, `code` 등이 포함됩니다.

### 4. 카카오로부터 엑세스 토큰 수신 (Kakao OAuth → Auth 서비스)

- 카카오는 요청이 성공적으로 처리되면 **엑세스 토큰과 리프레시 토큰**을 Auth 서비스에 반환합니다.
- Auth 서비스는 엑세스 토큰을 통해 사용자의 카카오 프로필을 요청할 수 있습니다.

### 5. 엑세스 토큰으로 사용자 정보 요청 (Auth 서비스 → Kakao API)

- Auth 서비스는 엑세스 토큰을 사용해 **카카오 API로 사용자 정보를 요청**합니다.
- 예시 요청:
    
    ```bash
    bash
    코드 복사
    GET https://kapi.kakao.com/v2/user/me
    
    ```
    
- 이 요청의 응답으로 **사용자의 고유 ID, 닉네임, 이메일 등**이 반환됩니다.

### 6. 사용자 정보 저장 및 JWT 발급 (Auth 서비스)

- Auth 서비스는 카카오에서 받은 사용자 정보를 이용해 **회원 가입 여부를 확인**하고, 신규 사용자라면 `Member DB`에 해당 정보를 저장합니다.
- 이후 Auth 서비스는 **JWT (JSON Web Token)**을 발급하여 사용자 인증에 사용합니다.
- 이 JWT에는 사용자 ID와 인증 만료 시간 등의 정보가 포함되며, **이후 사용자 요청 시 토큰을 이용해 인증**할 수 있습니다.

### 7. Next.js로 JWT 전송 (Auth 서비스 → Next.js)

- Auth 서비스는 JWT를 발급한 후, 이를 Next.js 프론트엔드로 반환합니다.
- Next.js는 이 토큰을 **브라우저의 쿠키 또는 로컬 스토리지**에 저장하여, 이후 인증이 필요한 요청에 토큰을 사용합니다.

### 8. 사용자 인증 완료 후 리소스 접근 (Next.js → Spring Cloud Gateway → 각 서비스)

- 사용자가 로그인한 후, Next.js는 JWT를 포함해 요청을 보냅니다.
- Spring Cloud Gateway는 이 토큰을 받아서 **토큰 유효성 검사**를 수행하거나, **Auth 서비스로 유효성 검사를 위임**할 수 있습니다.
- 인증이 완료되면 요청이 해당 리소스 서비스(예: Member, Event 서비스 등)로 전달됩니다.

### 요약

- **1-2단계**: Next.js → 카카오 OAuth 로그인 페이지로 리다이렉트 → 인증 코드 수신
- **3-4단계**: Next.js → Auth 서비스 → 카카오로 인증 코드 전송 후 엑세스 토큰 수신
- **5-6단계**: Auth 서비스가 엑세스 토큰으로 사용자 정보를 받아서 DB에 저장하고 JWT 발급
- **7단계**: Auth 서비스가 JWT를 Next.js에 반환, Next.js는 토큰을 저장
- **8단계**: JWT를 포함한 요청이 Spring Cloud Gateway를 통해 리소스 서비스로 전달됨

이렇게 하면 사용자는 카카오 로그인을 통해 인증을 완료하고, 이후 모든 요청에 대해 JWT를 사용하여 인증을 유지하게 됩니다. 추가적으로, JWT는 **사용자 인증 상태를 확인하고 각 서비스 간에 일관된 인증을 제공**할 수 있게 해줍니다.

---

**Auth 서비스**와 **Spring Cloud Gateway**를 나눠서 사용하는 이유는 MSA 아키텍처에서 **역할 분리**와 **확장성**, **유지보수성**을 고려하기 때문입니다. 두 서비스를 분리함으로써 다음과 같은 장점을 얻을 수 있습니다.

### 1. **역할과 책임의 분리**

- **Spring Cloud Gateway**: 주로 **라우팅**과 **요청 필터링**에 중점을 두며, 트래픽을 관리하고 각 서비스로 요청을 분산합니다. Gateway는 인증 여부를 확인하지만, **사용자 인증과 토큰 발급/검증과 같은 구체적인 인증 작업을 수행하는 데는 특화되지 않음**이 일반적입니다.
- **Auth 서비스**: **사용자 인증, 토큰 발급 및 관리, 소셜 로그인 (예: 카카오 OAuth)**을 담당합니다. 여기에는 사용자 세션 관리, 리프레시 토큰 발급, 만료된 토큰 처리, OAuth 규격 준수 등의 추가 작업이 포함됩니다.

두 역할을 분리하면 각각의 서비스가 **특화된 기능을 제공**하게 되어 코드의 복잡도를 줄이고, 유지보수가 쉬워집니다.

### 3. **보안 측면**

- **보안 계층 강화**: Gateway는 단순히 요청을 필터링하고 인증을 확인하는 1차 방어선 역할을 하고, 실제 인증 로직과 토큰 관리는 Auth 서비스에서 수행합니다. 이렇게 하면 인증에 대한 세부 정보를 Gateway에 노출하지 않고, 인증 로직이 Gateway에서 분리되어 **보안이 강화**됩니다.
- **토큰 검증 로직 분리**: Spring Cloud Gateway에서는 단순한 토큰의 유효성 검증은 가능하지만, 리프레시 토큰 발급이나 세션 관리와 같은 복잡한 인증 관리를 하기에는 적합하지 않습니다. 이를 Auth 서비스에 두면 **보안 규칙을 더욱 정교하게 적용**할 수 있습니다.