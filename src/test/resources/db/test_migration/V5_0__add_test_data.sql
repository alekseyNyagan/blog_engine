INSERT INTO tags (name)
VALUES
    ('Technology'),
    ('Travel'),
    ('Food'),
    ('Fashion'),
    ('Sports');
INSERT INTO users (is_moderator, reg_time, name, email, password)
VALUES
    (1, '2024-03-29 08:59:00', 'Alice', 'alice@example.com', '$2a$12$NXN4TKfIUNrWtAsXN/1uwelK1hkTDgSB2dgUhZ0z1r5KEtHPrSGh2'),
    (0, '2024-03-29 08:59:00', 'Bob', 'bob@example.com', '$2a$12$V/GsVB.f/VzjVRxNPnTOFed871fHSGGyuscHtDu9Z2FQL71Ph/7Yu'),
    (1, '2024-03-29 08:59:00', 'Charlie', 'charlie@example.com', '$2a$12$ehdoUo95dJmUtMdxjSTBHukrrCuvehohVm4HQVQoCD5IRfQ2ZoT36');
INSERT INTO posts (is_active, moderation_status, user_id, time, title, text, view_count)
VALUES
    (1, 'ACCEPTED', 1, '2024-03-29 08:59:00', 'Introduction to Technology', 'This is a post about technology.', 100),
    (1, 'NEW', 2, '2024-03-29 08:59:00', 'Travel Adventures', 'Exploring new places and cultures.', 50),
    (1, 'ACCEPTED', 3, '2024-03-29 08:59:00', 'Delicious Recipes', 'Cooking up a storm in the kitchen.', 75);
INSERT INTO post_comments (post_id, user_id, time, text)
VALUES
    (1, 2, '2024-03-29 08:59:00', 'Great post!'),
    (2, 1, '2024-03-29 08:59:00', 'I love traveling!'),
    (3, 3, '2024-03-29 08:59:00', 'Yummy recipes!');
INSERT INTO post_votes (user_id, post_id, time, value)
VALUES
    (1, 1, '2024-03-29 08:59:00', 1),
    (2, 2, '2024-03-29 08:59:00', -1),
    (3, 3, '2024-03-29 08:59:00', 1);
INSERT INTO tag2post (post_id, tag_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (1, 4),
    (2, 5);