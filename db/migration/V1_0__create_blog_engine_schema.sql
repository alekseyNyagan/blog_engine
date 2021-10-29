create table if not exists users (
    id int not null auto_increment primary key,
    is_moderator tinyint not null,
    reg_time datetime not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    code varchar(255),
    photo text
);

create table if not exists posts (
    id int not null auto_increment primary key,
    is_active tinyint not null,
    moderation_status enum('NEW', 'ACCEPTED', 'DECLINED') not null ,
    moderator_id int,
    user_id int not null,
    time datetime not null,
    title varchar(255) not null,
    text text not null,
    view_count int not null,
    constraint posts_users_fk foreign key (user_id) references users(id)
);

create table if not exists post_votes (
    id int not null auto_increment primary key,
    user_id int not null,
    post_id int not null,
    time datetime not null,
    value tinyint not null,
    constraint post_votes_users_fk foreign key (user_id) references users(id),
    constraint post_votes_posts_fk foreign key (post_id) references posts(id)
);

create table if not exists tags (
    id int not null auto_increment primary key,
    name varchar(255) not null
);

create table if not exists tag2post (
    id int not null auto_increment primary key,
    post_id int not null,
    tag_id int not null,
    constraint tag2post_posts_fk foreign key (post_id) references posts(id),
    constraint tag2post_tags_fk foreign key (tag_id) references tags(id)
);

create table if not exists post_comments (
    id int not null auto_increment primary key,
    parent_id int,
    post_id int not null,
    user_id int not null,
    time datetime not null,
    text text not null,
    constraint post_comments_posts_fk foreign key (post_id) references posts(id),
    constraint post_comments_users_fk foreign key (user_id) references users(id)
);

create table if not exists captcha_codes (
    id int not null auto_increment primary key,
    time datetime not null,
    code tinytext not null,
    secret_code tinytext not null
);

create table if not exists global_settings (
    id int not null auto_increment primary key,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null
);
