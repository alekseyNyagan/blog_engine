INSERT INTO tags (name)
VALUES ('tag1'),
       ('tag2'),
       ('tag3'),
       ('tag4'),
       ('tag5');

INSERT INTO users (is_moderator, reg_time, name, email, password, code, photo)
VALUES (1, '2024-04-05 08:59:00', 'user1', 'user1@example.com', 'password1', 'code1', 'photo1'),
       (0, '2024-04-05 08:59:00', 'user2', 'user2@example.com', 'password2', 'code2', 'photo2'),
       (1, '2024-04-05 08:59:00', 'user3', 'user3@example.com', 'password3', 'code3', 'photo3'),
       (0, '2024-04-05 08:59:00', 'user4', 'user4@example.com', 'password4', 'code4', 'photo4'),
       (1, '2024-04-05 08:59:00', 'user5', 'user5@example.com', 'password5', 'code5', 'photo5');

INSERT INTO posts (is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES (1, 'ACCEPTED', 1, 1, '2024-04-05 08:59:00', 'title1', 'text1', 10),
       (1, 'ACCEPTED', 2, 2, '2024-04-05 08:59:00', 'title2', 'text2', 20),
       (1, 'ACCEPTED', 3, 3, '2024-04-05 08:59:00', 'title3', 'text3', 30),
       (1, 'ACCEPTED', 4, 4, '2024-04-05 08:59:00', 'title4', 'text4', 40),
       (1, 'ACCEPTED', 5, 5, '2024-04-05 08:59:00', 'title5', 'text5', 50),
       (1, 'ACCEPTED', 2, 1, '2024-04-05 08:59:00', 'title6', 'text6', 60),
       (1, 'ACCEPTED', 3, 2, '2024-04-05 08:59:00', 'title7', 'text7', 70),
       (1, 'ACCEPTED', 4, 3, '2024-04-05 08:59:00', 'title8', 'text8', 80),
       (1, 'ACCEPTED', 5, 4, '2024-04-05 08:59:00', 'title9', 'text9', 90),
       (1, 'ACCEPTED', 1, 5, '2024-04-05 08:59:00', 'title10', 'text10', 100);

INSERT INTO post_comments (parent_id, post_id, user_id, time, text)
VALUES (NULL, 1, 1, '2024-04-05 08:59:00', 'comment1'),
       (1, 1, 2, '2024-04-05 08:59:00', 'comment2'),
       (NULL, 2, 3, '2024-04-05 08:59:00', 'comment3'),
       (3, 2, 4, '2024-04-05 08:59:00', 'comment4'),
       (NULL, 3, 5, '2024-04-05 08:59:00', 'comment5');

INSERT INTO post_votes (user_id, post_id, time, value)
VALUES (1, 2, '2024-04-05 08:59:00', 1),
       (2, 1, '2024-04-05 08:59:00', 0),
       (3, 2, '2024-04-05 08:59:00', 1),
       (4, 2, '2024-04-05 08:59:00', 0),
       (5, 3, '2024-04-05 08:59:00', 1);

INSERT INTO tag2post (post_id, tag_id)
VALUES (2, 1),
       (1, 2),
       (2, 2),
       (2, 3),
       (3, 3);