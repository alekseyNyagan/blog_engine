INSERT INTO tags (name)
VALUES
    ('Technology'),
    ('Travel'),
    ('Food'),
    ('Fashion'),
    ('Sports');
INSERT INTO users (is_moderator, reg_time, name, email, password)
VALUES
    (1, NOW(), 'Alice', 'alice@example.com', '$2a$12$NXN4TKfIUNrWtAsXN/1uwelK1hkTDgSB2dgUhZ0z1r5KEtHPrSGh2'),
    (0, NOW(), 'Bob', 'bob@example.com', '$2a$12$V/GsVB.f/VzjVRxNPnTOFed871fHSGGyuscHtDu9Z2FQL71Ph/7Yu'),
    (1, NOW(), 'Charlie', 'charlie@example.com', '$2a$12$ehdoUo95dJmUtMdxjSTBHukrrCuvehohVm4HQVQoCD5IRfQ2ZoT36');
INSERT INTO posts (is_active, moderation_status, user_id, time, title, text, view_count)
VALUES
    (1, 'ACCEPTED', 1, NOW(), 'Introduction to Technology', 'This is a post about technology.', 100),
    (1, 'NEW', 2, NOW(), 'Travel Adventures', 'Exploring new places and cultures.', 50),
    (1, 'ACCEPTED', 3, NOW(), 'Delicious Recipes', 'Cooking up a storm in the kitchen.', 75);
INSERT INTO post_comments (post_id, user_id, time, text)
VALUES
    (1, 2, NOW(), 'Great post!'),
    (2, 1, NOW(), 'I love traveling!'),
    (3, 3, NOW(), 'Yummy recipes!');
INSERT INTO post_votes (user_id, post_id, time, value)
VALUES
    (1, 1, NOW(), 1),
    (2, 2, NOW(), -1),
    (3, 3, NOW(), 1);
INSERT INTO tag2post (post_id, tag_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (1, 4),
    (2, 5);