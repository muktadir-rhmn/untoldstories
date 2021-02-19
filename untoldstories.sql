create user untoldstories identified by '150250';
create database untoldstories;
grant all on untoldstories.* to untoldstories;
use untoldstories;

create table comments
(
    id      bigint auto_increment
        primary key,
    storyID bigint       not null,
    userID  bigint       not null,
    body    text         not null,
    mTime   int unsigned not null,
    cTime   int unsigned not null
);

create index comments_storyID_index
    on comments (storyID);

create index comments_userID_index
    on comments (userID);

create table replies
(
    id        bigint auto_increment
        primary key,
    userID    bigint not null,
    storyID   bigint not null,
    commentID bigint not null,
    body      text   not null,
    mTime     bigint not null,
    cTime     bigint not null
);

create index replies_commentID_index
    on replies (commentID);

create index replies_storyID_index
    on replies (storyID);

create index replies_userID_index
    on replies (userID);

create table stories
(
    id      bigint auto_increment
        primary key,
    userID  bigint        not null,
    body    text          not null,
    privacy tinyint       not null,
    nViews  int default 0 not null,
    mTime   int unsigned  not null,
    cTime   int unsigned  not null
);

create index stories_userID_index
    on stories (userID);

create table users
(
    id       bigint auto_increment
        primary key,
    userName varchar(100) not null,
    password varchar(100) not null,
    mTime    bigint       not null,
    cTime    bigint       not null,
    constraint users_userName_index
        unique (userName)
);

create table usersFollowStories
(
    id      bigint       not null
        primary key,
    userID  bigint       not null,
    storyID bigint       not null,
    cTime   int unsigned not null
);

create index userFollowsStory_userID_StoryID_index
    on usersFollowStories (userID, storyID);

create table usersReactToComments
(
    id        bigint auto_increment
        primary key,
    userID    bigint       not null,
    storyID   bigint       not null,
    commentID bigint       not null,
    reaction  tinyint      not null,
    cTime     int unsigned not null
);

create index usersReactToComments_commentID_userID_index
    on usersReactToComments (commentID, userID);

create index usersReactToComments_storyID_index
    on usersReactToComments (storyID);

create table usersReactToReplies
(
    id        bigint auto_increment
        primary key,
    commentID bigint       not null,
    replyID   bigint       not null,
    userID    bigint       not null,
    reaction  tinyint      not null,
    cTime     int unsigned not null
);

create index usersReactToReplies_commentID_index
    on usersReactToReplies (commentID);

create index usersReactToReplies_replyID_userID_index
    on usersReactToReplies (replyID, userID);

create table usersReactToStories
(
    id       bigint auto_increment
        primary key,
    userID   bigint       not null,
    storyID  bigint       not null,
    reaction tinyint      not null,
    cTime    int unsigned not null
);

create index usersReactToStories_storyID_userID_index
    on usersReactToStories (storyID, userID);

