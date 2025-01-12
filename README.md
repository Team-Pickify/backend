# 📍 Team-Pickify 개발 컨벤션

## ⭐️ git issue Convention
- **이슈 생성 시 진행하고자 하는 task에 맞춰서 Get started**
- **이슈 title: [타입] + 실제 수행할 task 목록**
  <pre><code> ex) [Feat] API 응답 base code 구현
  </code></pre>
- **이슈 description: 진행한 task 자유롭게 요약**
- Development의 Create a branch로 브랜치 생성 <br>
  - **[타입]/#이슈번호**
  <pre><code> ex) feature/#5
  </code></pre>

---

## ⭐️ PR Convention

🟢 각 브랜치에서 main으로 PR 올리기 <br>
🟢 PR 생성 후 팀원 2명 이상에게 코드 리뷰 받아야 merge 가능 <br>
🟢 PR 내용은 아래의 템플릿을 사용한다

### #️⃣ 연관된 이슈

> ex) #이슈번호, #이슈번호

### 📝 작업 내용

> 이번 PR에서 작업한 내용을 간략히 설명해주세요(이미지 첨부 가능)

### 📸 스크린샷 (선택)

>

### 💬 리뷰 요구사항(선택)

> 리뷰어가 특별히 봐주었으면 하는 부분이 있다면 작성해주세요
>
> ex) 메서드 XXX의 이름을 더 잘 짓고 싶은데 혹시 좋은 명칭이 있을까요?

---

## ⭐️ Code Convention

✅ 들여쓰기는 4칸 <br>
✅ indent depth(들여쓰기)는 최대한 2까지 맞추도록(반복문 남용 금지) <br>
✅ 클래스명은 PascalCase, 메서드와 변수는 camelCase<br>
✅ 주석 작성 시 "클래스와 메서드"에는 Javadoc 스타일 사용<br>
<pre><code>💡Example
/**
   * 사용자 정보를 ID로 조회합니다.
   *
   * @param userId 조회할 사용자의 ID
   * @return 사용자 정보
   */
</code></pre>
✅ 코드 내 주석은 필요 시 추가, 불필요한 주석은 제거 <br>

---

## ⭐️ Spring Boot 특화 코드 스타일

### 1. 애노테이션 사용
   - 클래스 레벨의 애노테이션 순서는 다음과 같이 작성한다
     ```
      1) @Controller, @Service, @Repository 등 주요 역할 지정
      2) @RequestMapping, @GetMapping, @PostMapping 등 HTTP 관련 애노테이션
      3) 기타(예: @Transactional, @Cacheable)
      ```
### 2. Bean 주입 방식
   - Spring에서는 생성자 주입을 기본으로 사용하며, @Autowired 필드 주입은 지양

### 3. 컨트롤러 메서드
   - HTTP 응답 코드는 명확히 설정하고, 반환 타입으로는 DTO 사용

### 4. DTO와 Entity 분리
   - DTO는 외부에 노출되는 데이터를 정의하며, Entity는 내부 데이터베이스와 매핑
   - Entity를 직접 컨트롤러에서 반환하지 말고, 서비스 계층에서 변환