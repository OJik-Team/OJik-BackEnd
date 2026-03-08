---
name: 작업/개선
about: 리팩토링, 설정, 문서, 유지보수 이슈 템플릿
title: "[Chore] "
labels: ["chore"]
assignees: []
---

## 작업 배경
- 현재 문제 또는 개선 필요 사항 작성

## 작업 내용
- [ ] 변경 대상 레이어 식별(`api / domain / infrastructure`)
- [ ] 영향 범위 확인(의존성 방향, 패키지 경계)
- [ ] 필요한 코드/문서 반영

## 검증 방법
- [ ] 로컬 실행/테스트 확인
- [ ] 회귀 영향 점검
- [ ] 아키텍처 규칙 위반 없음(`domain`의 외부 기술 의존 금지)

## 브랜치 네이밍
- 예정 브랜치: `OJik/chore-<issue-number>-<short-topic>`

## PR 연결
- 머지할 PR 본문에 `Closes #<issue-number>` 기입
