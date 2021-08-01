-- 必须有一个超级管理员
insert into user(user_id, name, email, password, role, status)
value (1, 'root', 'zjy_mc@163.com', 'e10adc3949ba59abbe56e057f20f883e', 'SUPER_ADMIN', 'ENABLE')
on DUPLICATE KEY update status='ENABLE';
