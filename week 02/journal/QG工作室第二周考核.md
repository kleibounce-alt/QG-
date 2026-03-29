# QG工作室第二周考核

---

---

## ==DAY 1==

​		*上了一整天课，把下午英语上机给旷了，每次这种课都在玩手机，没什么意义。*



- #### 初看SpringBootWeb

​		给了个例子：浏览器发起请求http://localhost:3306/hello，我需要在创建springboot项目进行接收请求并返回响应。

​		做法主要是定义一个HelloController类，在里面写响应方法。在类上面写注解@RestController，方法上写注解@RequestMapping接受(“/hello”)。

​		目前来看，springbootweb就是能创建一个带http服务能力的java应用。



- #### http协议

​		**概念：**规定了浏览器和服务器之间数据传输的规则。

​		**特点：**一次请求对应一次响应，每次 *请求-响应* 都是独立的。



- #### http请求协议

  - **请求行：** 请求数据第一行(请求方式 + 资源路径 + 协议)
  - **请求头： **Host、Accept、……
  - **请求体：**POST的请求参数在请求体中，大小无限制。而GET的请求参数在请求行，大小有限制。在浏览器输入账号和密码时，GET请求时数据会出现在浏览器上方网址处，POST请求则无法直接看到数据。



- #### http响应格式

  - **响应行：** 响应数据第一行(协议 + 状态码 + 描述)

    - 状态码：

      |           1xx            |     2xx      |       3xx        |    4xx     |    5xx     |
      | :----------------------: | :----------: | :--------------: | :--------: | :--------: |
      | 表示已接收，等待继续请求 | 成功接收处理 | 重定向到其他地方 | 客户端错误 | 服务器错误 |

      

  - **响应头：** Content-Type、Content-Length、……

  - **响应体：** 存放响应数据



- #### **http协议解析**

​		将数据传入，用输入流读取一行一行的数据进行手动解析，代码量可能会很多。



---

---

## ==DAY 2==

​		*上午没什么重要课，下午自由，中午去打羽毛球打得疲惫，午觉一睡就从两点睡到四点，以后闹钟响了绝对爬起来。*



- #### 认识RESTful API

  - **RE：** 资源的表现形式
    - 如同一个资源可以用xml返回客户端，也可用JSON格式返回。
  - **S：** 状态
    - 有状态：登录一次，下次不用登录 ；无状态：请求-响应都是单独的。
  - **T：** 传输
    - GET：从服务器获取资源到客户端 ； POST：把客户端数据更新到服务器

​		<u>总的来说，REST是一种软件架构风格，用统一的接口，以无状态的方式，实现客户端与服务器的数据传递和变更状态。</u>



- #### 构造RESTful API

  - **客户端请求：**
    - 确定资源：*URI* -> 用名词表示资源，不用动词。具体某个数据，就加上ID，支持嵌套。
    - 选择动作：GET(查询资源)、POST(创造资源)、PUT(完整更新资源)、PATCH(部分更新资源)、DELETE(删除资源) … …
    - 添加查询条件：分页、过滤、排序 … …
    - 无状态：需要传Token，将每次的完整信息传入
  - **服务器响应：**
    - 统一响应格式：一般用JSON
    - 返回合适HTTP状态码：1xx、2xx开头之类的



- #### 接触dto

  - **数据封装更安全：**
    - 比如在StudentService的接口里，需要传入用户或表的数据的参数时，传入的是Request，不会明晃晃地暴露要传密码还是其他私密的东西。
  - **修改更方便：**
    - 只需要修改Request里面的东西即可。

​		

- #### 接触注解

​		写ServiceImpl时，需要用到一些注解，这些注解是来源于Spring Framework，springboot继承了framework。我的理解是，比如autowired，就是spring帮你创建一个对象bean然后管理，想用方法就直接调用无需创建对象再调用。@Autowired + 定义的变量，spring就会把原本管理的对象装进这个变量里。



---

---

## ==DAY 3==

​		*好忙，从早到晚五节课，只好把政治课和离散课旷掉了。*

​		

- #### JWT入门——JwtUtils配置

  - **一 :** 

  - ```java
    @Component
    public class JwtUtils {}
    ```

    - 注解@Componet能将JwtUtils注册成一个bean，以后通过@Autowired依赖注入使用

  

  - **二 ：**

  - ```java
    	@Value("${jwt.secret}")
        private String secret;
        @Value("${jwt.expiration}")
        private Long expiration;
    ```

    - @Value可从spring中取值，这个secret和expiration是在yaml文件里写好的

  - ```yaml
    jwt:
      secret: 0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef   # 至少64个字符
      expiration: 86400000   # 24小时，单位毫秒
    ```

    - secret和加密有关，expiration和签名过期有关

  

  - **三 ：**

  - ```java
    	/**
         * 获取签名密钥（HS512）
         */
        private SecretKey getSigningKey() {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            // HS512 要求密钥长度至少 64 字节，如果不足会抛出异常
            return Keys.hmacShaKeyFor(keyBytes);
        }
    ```

    - 将secret字符串按UTF-8编码转成字节数组
    - 返回加密后生成的密钥

  

  - **四 ：**

  - ```java
    	/**
         * 生成 JWT Token
         * @param subject 用户标识（如学生ID）
         * @return JWT 字符串
         */
        public String generateToken(String subject) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);
    
            return Jwts.builder()
                    .subject(subject)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey(), Jwts.SIG.HS512)
                    .compact();
        }
    ```

    - now为当前时间也为签发时间
    - expiryDate为过期日期(精确到毫秒)
    - 结合密钥生成Token

  

  - **五 :**

  - ```java
        /**
         * 从 Token 中获取用户标识（subject）
         * @param token JWT 字符串
         * @return subject
         */
        public String getSubjectFromToken(String token) {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        }
    ```

    - 从token中获取用户标识

  

  - **六 :**

  - ```java
    	/**
         * 验证 Token 是否有效（签名正确且未过期）
         * @param token JWT 字符串
         * @return true=有效，false=无效
         */
        public boolean validateToken(String token) {
            try {
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                // 签名错误、过期、格式错误等均视为无效
                return false;
            }
        }
    ```

    - 验证Token



---

---

## ==DAY 4==

​		*上午两节水课，下午无课，旷一节， 看看下午打不打球*

​		

- #### 初步完成StudentService

  ​		将service分为两个部分，一个接口，一个实现。其中接口传入的数据都是request，真数据在dto封装好了。目前还没有添加上传图片的功能，后续补上。

- #### 一些细节

  - **注解 :**
    - @Service ：说明该类为业务类，可以用一些旗下的注解
    - @Slf4j ：日志管理，输出用log.info()
    - @Transactional : 事物管理，遇到报错直接全部终止行为。
    - @Autowired ：依赖注入，把对象工厂中的对象放到变量里
    - @Override ：重写，说明为接口或抽象父类方法的重写

  - **报错处理 ：**
    - 设置全局exception，在前后端皆可用
    - 对于特殊报错，可直接throw new IllegalArgumentException()，返回对应错误信息

  - **密码加密**




- #### 初步完成AdminService



---

---

## ==DAY 5==

​		*上午就一节数学，下午心理旷掉，图书馆清洁的时候，回宿舍打打比赛。*



- #### 全局报错

  - 加上这两个注解，在service进行throw的时候能捕捉到错误，并把对应的信息传到前端展示

  - ```
    @ExceptionHandler
    ```

  - ```
    @RestControllerAdvice
    ```



- #### RESTful API设计规范

  | 注册/登录 | 修改密码 | 查看报修单 | 取消报修单 |
  | :-------: | :------: | :--------: | :--------: |
  |   POST    |   PUT    |    GET     |   DELETE   |



- #### Controller层

  - PostMapping：数据在请求体中，通过解析Json获取数据
  - Get,Put,Delete…：传入url时，用{}框起来传入的数据，用@PathVariable接收数据
  - jwt拦截器拦截了token并解析，在controller把数据用requestattribute获取



​		至此已经把基本后台的东西完成了。

​		

- #### 梳理结构

  - **common**
    - result : 统一响应
  - **service**
    - impl : 业务代码
    - service : 业务接口
  - **mapper**
    - 操作数据库的框架
  - **dto**
    - 数据传输，安全性高，方便数据的改动
  - **entity**
    - 实体类
  - **controller**
    - 后台接收前端的接口，将前台发送的token与json打包的result解析并传给service
  - **utils**
    - JWT : 用于形成token
  - **config**
    - 配置类，例如加密配置，注册拦截器
  - **exception**
    - 全局异常处理



---

---

## ==DAY 6==

​		*下午出去玩，玩了一整天*



---

---

## ==DAY 7==

​		*中午去图书馆，下午上水课。已经把前端用vue3用ai写完，修bug从下午一点到下午五点都在修理bug，最后终于是跑通代码了。*

​		

- 
