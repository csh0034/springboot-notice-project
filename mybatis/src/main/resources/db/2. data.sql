INSERT INTO COMT_USER VALUES ('user-01', 'user1', 'ROLE_USER', '$2a$10$lCJtLo8PdaFglHSa2Hg.m.XBC4fXU8FUxSpPeVQPejkiDcor8EV7.', '사용자1', 1);
INSERT INTO COMT_USER VALUES ('user-02', 'user2', 'ROLE_USER', '$2a$10$lCJtLo8PdaFglHSa2Hg.m.XBC4fXU8FUxSpPeVQPejkiDcor8EV7.', '사용자2', 1);
INSERT INTO COMT_USER VALUES ('user-03', 'user3', 'ROLE_USER', '$2a$10$lCJtLo8PdaFglHSa2Hg.m.XBC4fXU8FUxSpPeVQPejkiDcor8EV7.', '사용자3', 1);

INSERT INTO COMT_NOTICE VALUES ('notice-01', 'user1의 게시글1', '내용1', 0, null, 1, 'user-01', '2020-11-11 00:00:01', 'user-01', '2020-11-11 00:00:11');
INSERT INTO COMT_NOTICE VALUES ('notice-02', 'user2의 게시글2', '내용2', 0, null, 1, 'user-02', '2020-11-11 00:00:02', 'user-02', '2020-11-11 00:00:22');
INSERT INTO COMT_NOTICE VALUES ('notice-03', 'user3의 게시글3', '내용3', 0, null, 1, 'user-03', '2020-11-11 00:00:03', 'user-03', '2020-11-11 00:00:33');