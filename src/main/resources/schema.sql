drop table if exists link;
drop table if exists category_like;
drop table if exists category;
drop table if exists user;
drop table if exists profile_image;

create table user (
                      user_id bigint primary key auto_increment,

                      created_at datetime(6) not null default now(6),
                      last_modified_at datetime(6) not null default now(6),

                      username varchar(255) not null,
                      password varchar(255),

                      nickname varchar(255),
                      contact varchar(255),
                      refresh_token varchar(255),
                      role enum ('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST') not null,
                      social_id varchar(255),
                      social_type enum ('LOCAL', 'GOOGLE', 'NAVER', 'KAKAO') not null,

                      profile_image_id bigint
);

create table profile_image (
                               profile_image_id bigint primary key auto_increment,

                               created_at datetime(6) not null default now(6),
                               last_modified_at datetime(6) not null default now(6),

                               original_name varchar(255) not null,
                               uploaded_name varchar(255) not null
);

create table category (
                          category_id bigint primary key auto_increment,

                          created_at datetime(6) not null default now(6),
                          last_modified_at datetime(6) not null default now(6),
                          created_by varchar(255) not null,
                          last_modified_by varchar(255) not null,

                          category_name varchar(255) not null,
                          as_category_name varchar(255),
                          category_state enum ('PRIVATE', 'PUBLIC') not null,
                          view_count int not null default 0,

                          user_id bigint not null
);

create table category_like (
                               category_like_id bigint primary key auto_increment,

                               user_id bigint not null,
                               category_id bigint not null
);

create table link (
                      link_id bigint primary key auto_increment,

                      created_at datetime(6) not null default now(6),
                      last_modified_at datetime(6) not null default now(6),
                      created_by varchar(255) not null,
                      last_modified_by varchar(255) not null,

                      title varchar(255) not null,
                      summary varchar(255) not null,
                      url varchar(255) not null,
                      link_state bit not null default 0,

                      user_id bigint not null,
                      category_id bigint not null
);

alter table user
    add constraint UK_profile_image_id
        unique (profile_image_id);

alter table user
    add constraint FK_user__profile_image
        foreign key (profile_image_id)
            references profile_image(profile_image_id);

alter table category
    add constraint FK_category__user
        foreign key (user_id)
            references user(user_id);

alter table category_like
    add constraint FK_category_like__user
        foreign key (user_id)
            references user(user_id);

alter table category_like
    add constraint FK_category_like__category
        foreign key (category_id)
            references category(category_id);

alter table link
    add constraint FK_link__user
        foreign key (user_id)
            references user(user_id);

alter table link
    add constraint FK_link__category
        foreign key (category_id)
            references category(category_id);