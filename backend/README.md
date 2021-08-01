### 规则约束
1. 一个用户对应一个角色  
现有角色如下:
- 超级管理员(SuperAdmin):
- 管理员(Admin):
- 普通用户(Normal):
2. 使用Token做访问凭证
3. 使用Spring Data Jpa做数据访问
4. 使用MySql做数据存储
5. 统一返回格式 { code:xxx, message: xxx, data: xxx}
6. Controller,Service,Dao内的方法按照CRUD的顺序排列
7. 登录时验证是否被禁用, 禁用的不返回token
8. RequestBody使用map接收,数据自己写swagger,自己检验参数
### 接口步骤

