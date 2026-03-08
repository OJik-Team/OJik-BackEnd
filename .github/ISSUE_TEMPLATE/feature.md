---
name: 기능 개발
about: 새 기능 구현 이슈 템플릿
title: "[Feature] "
labels: ["feat"]
assignees: []
---

## 배경
- 왜 이 기능이 필요한지 작성

## 목표
- 구현해야 하는 핵심 목표 작성

## 작업 범위
- [ ] API 레이어(`api/controller`, `api/adapter`) 반영
- [ ] Domain 레이어(`domain/entity`, `domain/port`, `domain/service`) 반영
- [ ] Infrastructure 레이어(`infrastructure/repository`, `infrastructure/client`, `infrastructure/adapter`) 반영
- [ ] 테스트 작성/수정

## 완료 조건(DoD)
- [ ] 기능 요구사항 충족
- [ ] 예외/실패 케이스 처리
- [ ] 테스트 통과
- [ ] 의존성 방향 `api -> domain <- infrastructure` 준수

## 브랜치 네이밍
- 예정 브랜치: `OJik/feature-<issue-number>-<short-topic>`

## PR 연결
- 머지할 PR 본문에 `Closes #<issue-number>` 기입
