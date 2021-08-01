create database if not exists lims;
use lims;

create table if not exists user(
    user_id int(11) primary key auto_increment comment '用户ID',
    name varchar(128) not null comment '姓名',
    email varchar(128) not null unique comment '邮箱',
    password varchar(128) not null comment '存的其实是密码摘要',
    avatar varchar(128) default 'http://127.0.0.1:8080/image/default.jpg' comment '用户头像url',
    role enum('SUPER_ADMIN', 'ADMIN', 'NORMAL') default 'NORMAL' comment '用户身份',
    status enum('ENABLE', 'DISABLE') default 'ENABLE' comment '用户状态'
);

create table if not exists laboratory (
    laboratory_id int primary key auto_increment comment '实验室ID',
    name varchar(128) comment '实验室名',
    location varchar(128) comment '实验室地点',
    contact varchar(128) comment '联系方式',
    user_id int comment '创建者'
);

create table if not exists device (
    device_id int primary key auto_increment comment '设备ID',
    image_url varchar(128) comment '设备图片url',
    name varchar(128) comment '设备名称',
    status enum('AVAILABLE', 'BORROWED', 'REPAIRING', 'DAMAGED') default 'Available' comment '设备状态',
    laboratory_id int comment '所属实验室'
);
create table if not exists device_property (
    device_property_id int primary key auto_increment comment '属性ID',
    name varchar(128) comment '属性名',
    value varchar(128) comment '属性值',
    device_id int comment '关联的设备ID'
);
create table if not exists device_appendix (
    device_appendix_id int primary key auto_increment comment '附件ID',
    name varchar(128) comment '附件名',
    device_id int comment '关联的设备ID'
);

create table if not exists device_apply (
     device_apply_id int primary key auto_increment comment '申请ID',
     borrow_reason varchar(128) comment '申请理由',
     promise_timestamp long comment '承诺归还时间',
     apply_timestamp long comment '使用者申请时间',
     grant_timestamp long comment '管理员授权时间',
     return_timestamp long comment '使用者归还时间',
     finish_timestamp long comment '管理员归档时间',
     status enum('APPLIED', 'GRANTED', 'RETURNED', 'FINISHED', 'DENIED', 'CANCELED') comment '申请状态',
     device_id int comment '申请的设备',
     user_id int comment '申请人'
);

create table if not exists device_record (
    device_record_id int primary key auto_increment comment '设备使用记录ID',
    record_timestamp long comment '记录时间',
    record_image_url varchar(128) comment '设备使用情况图片',
    description varchar(128) comment '说明',
    user_id int comment '使用者',
    device_id int comment '关联的设备'
    );

create table if not exists device_comment (
    device_comment_id int primary key auto_increment comment '设备评论ID',
    comment varchar(128) comment '评论内容',
    comment_timestamp long comment '评论时间',
    device_id int comment '评论的设备',
    user_id int comment '评论者'
);