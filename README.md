# 个人博客后端

这是后端，需要结合[前端](https://github.com/zkytech/zkytech-frontend)一起使用

## 相关技术

语言：Java

框架：Spring Boot

主要工具：
+ Spring MVC
+ Spring Data JPA
+ Spring Security
+ Lombok

## 使用方法

1. IDEA打开后创建`application.properties`，按照`example.properties`中给出的配置项进行配置，具体需要设置的项目已在`example.properties`中给出

2. 编辑`src/main/java/com.zkytech.zkytech/config/WebSecurityConfig`，将第78行的注释取消掉。

3. 启动运行

4. 编辑`src/main/java/com.zkytech.zkytech/config/WebSecurityConfig`，将第78行注释掉。


### 管理员账号注册方法

编辑`src/main/java/com.zkytech.zkytech/controller/AuthController`第101行，将`UserType.DEFAULT`修改为`UserType.ADMIN`

注册完成后再修改回来即可

