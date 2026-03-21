# QG工作室第一周周记

---



### ==DAY 1==

- #### 尝试Github

​      一开始想本地建一个新文件夹week 01，其中夹着journal和project。然而在用Git上传的时候总是发现自己处于已完全更新的状态，甚是不解，GitHub也没显示week 01。后来得知GitHub上传文件有个特性，便是追踪不到空文件夹，起码在里面加个gitkeep文件，才能被追踪到。



- #### 初学MySQL

​      *MySQL的读法我在面试好像都读错了，当时没学。不知道这些基础内容能几天学会。*

#####          1. SQL语言分类

​      主要有五种：数据查询语言(DQL)、数据操作语言(DML)、数据定义语言(DDL)、数据控制语言(DCL)、事务控制语言(TCL)。<u>注意：DML针对表中的数据，DDL针对数据库对象</u>

#####          **2. SQL语言语法**

​      SQL语句不区分大小写(数据名要区分)，一般关键字用大写。SQL语句可以单行或多行书写，以分号结尾。

#####          3. SQL创建删除数据库的语法

① DDL语句创建数据库：

```mysql
CREATE DATABASE 数据库名 DEFAULT CHARACTER SET 字符编码;
# DATABASE 是单数
# DEFUALT CHARACTER SET 是指定默认编码类型关键字
# 字符编码一般是utf8
```

② 查看数据库：

```mysql
SHOW DATABASES;
# 此时DATABASES 是复数
```

③ DDL语句删除数据库：

```mysql
DROP DATABASE 数据库名；
```

④ 选择数据库：

```mysql
USE 数据库
```

#### DAY 1小结

​      今天从早上到晚，只有六点以后才能去图书馆学习。大概三个小时的学习时间里，很多都花在配置环境、学上传Github上了，算是大概弄好了。其实早八看到md文件的时候，我基本是一脸懵的。maven是什么？MyBatis是什么？我听都没听过。但在配置maven、mysql时，我有些模糊地感觉到各个的作用了。

​      比如mysql帮助我把本来储存在txt的数据，储存到了数据库里，在通过表来分别开数据与数据。maven还不太清楚，只在idea创建了一个maven项目。然后剩下的基本使用Java实现一个业务需求，大概是这样。明天时间较为充足，搞懂maven吧。

​      对了，群里的xihale在之前acm群里看见过，如今没想到已经如此地强了。我也想写个属于自己的网站，但笔试的登网站流程我都不会，不过若能持之以恒，还是能学会的。



---

---

### ==DAY 2== 

​      *上午满课，踢了一节足球，本来是因为没抢到羽毛球才去的足球，但现在感觉还不错。中午和舍友四排打go，遂下午晚上猛学。*



- #### 继续学学MySQL

​    *实际上，好像不用学太深入，但还是多学学好。*

#####     1. 整数类型

```
   注：m为显示长度，必须与zerofill配合。比如一个整数为100，而m设为4，则会输出0100。若m设为2，会直接输出100。
```

| tinyint(m) | smallint(m) | mediumint(m) | int(m)  | bigint(m) |
| :--------: | :---------: | :----------: | :-----: | :-------: |
|  1个字节   |   2个字节   |   3个字节    | 4个字节 |  8个字节  |

#####      **2. 浮点类型**

```
    注：m代表总个数，d为小数位。但我查询了下资料，发现这个用法是非正式的，以后还是直接用float和double即可。
```

|   float(m, d)   |   double(m, d)   |
| :-------------: | :--------------: |
| 8位精度， 4字节 | 16位精度， 8字节 |

#####       3. 字符类型

```
    注：无论char还是varchar都需要给长度，text则不需要。char输入不足n个的时候会自动补空格，而varchar不会补空格占字节，但当输入长度超过n时，都会报错。查询时间上，char最快，text最慢。能用varchar不用text。
```

|         char(n)         |        varchar(n)         |           text            |
| :---------------------: | :-----------------------: | :-----------------------: |
| 固定长度，最多255个字符 | 可变长度，最多65535个字符 | 可变长度，最多65535个字符 |

#####       4. DDL语句创建表

```mysql
CREATE TABLE employees(employee_id int, employee_name varchar(10), salary float);
# 基本语法：create table 表名(列名 类型，......);
```

##### 	  5. 查看表

```mysql
show tables;
```

##### 	  6. 删除表

```mysql
DROP TABLE 表名;
```

#####       7. 修改表名

```mysql
ALTER TABLE 旧表名 RENAME 新表名;
```

#####       8. 修改列名

```mysql
ALTER TABLE 表名 CHANGE COLUMN 旧列名 新列名 类型;
# 最好不要把类型改了，可能会出错
```

##### 	  9. 修改列类型

```mysql
ALTER TABLE 表名 MODIFY 列名 新类型;
```

#####       10. 添加新列

```mysql
ALTER TABLE 表名 ADD COLUMN 新列名 类型;
```

#####       11. 删除指定列

```mysql
ALTER TABLE 表名 DROP COLUMN 列名;
```



- #### 初看maven

​      *希望能解决昨天的疑惑。*

#####   **1.maven的作用：**

​		① 项目构建

​		② 依赖管理

​		③ 统一开发结构

#####    2.仓库：

​		①  **作用**：用于储存资源，含各种jar包

​		②  **分类**：

- 本地仓库：自己电脑上存储资源的仓库，可通过连接远程仓库获取资源
- 远程仓库：不是自己电脑的仓库，可为本地仓库提供资源。
  - 中央仓库：Maven团队自己开发维护的，存储全球百分之99的jar包
  - 私服： 部门或公司范围内存储资源的仓库，资源基本也是从中央仓库获取（局域网速度，很快）

​    **3.坐标：**

​		中央仓库有许多的资源，每一个资源需要特定的坐标来方便查询。需要某个jar包时，去maven官网获取其坐标，再放到IDEA里的pom.xml自动加载到本地仓库。



- #### MySQL的DML语言

​     *先跳过DDL语言的约束语法了，初期感觉DML是重头戏*

##### 		1. 添加数据

```mysql
INSERT INTO 表名(列名1，列名2，......) VALUES(值1，值2，......);
# 这是选择插入
# 完全插入就不用写列名
# 当主键是自动增长，可用default、null占位
```



#### DAY 2 小结

​		今天学的又多又少。学得多是因为看了很多集课，学得少是因为全都是在学语法和概念，还没落实到项目中。学习状态比较差，是因为昨天晚上睡太晚了。



---

---

## ==DAY 3==

​		*晚上要庆祝舍友生日，故中午到图书馆学一会。* 



- #### 初看Mybatis

​		我自己的理解就是，JDBC太麻烦了，不仅需要在dao写将MySQL数据封装到java对象，还要写实现类。而Mybatis仅仅需要写个mapper文件去实现一些操作即可。



#### DAY 3 小结

​		今天共三节课，中午打了会go然后就去图书馆了。下午上完高数就立马和舍友去聚餐，庆祝生日，一直玩到晚上，半夜还吃了蛋糕，挺开心的，遗憾就是几乎什么都没学，不过开心就好。



---

---

## ==DAY 4==

​		*今天下午都没课，可以好好弥补昨天没学的知识了。*



- #### Mybatis-plus相关配置

​		搜了一下又问了一下导师，我直接将现成代码写入了resourses底下的mybatis-config.xml里，然后在utils里添加了MybatisPlusSessionFactory，相当于SQLsessionfactory。



- #### MybatisSessionFactory

1. 类静态变量：sqlSessionFactory

  2. 公有静态方法：getSqlSessionFactory()

  3. 私有静态方法：createSqlSessionFactory()

     

- #### 三个Mapper

​		有三个实体类分别为：Student，Admin，report。由于Mybatis-plus有BaseMapper，所以在各自的Mapper里面只需要@Mapper并继承BaseMapper<>即可。



---

---

## ==DAY 5==

​		*一睁眼已经十点钟了，下午准备和舍友去天河玩。*



- #### 写写业务代码

​		*main类已经写完了，包含一些主菜单的登录注册与退出，此时先写学生注册的界面。写一下实现过程中的细节。*

1. ​     遇到“重复输入直至正确”的类型时，基本模板为：while(true) -> if (sc.hasNext) ->else { if (sc.hasNextLont) }  (以要输入Long为例)。这个sc对象创建可以放在while外面也可以放在里面，放在外面记得使用sc.nextLine()清除缓冲区。
2. ​     在输入有前缀固定的时候，用正则表达式。先把输入的类型转成String，在用matches(“^…$”)规定输入。
3. ​     mybatis执行数据库操作的标准写法：创建一个工厂SqlSessionFactory，来创建SqlSession进行一次与数据库的连接。再用try-with-resourses语法创建一个自动提交SQL语句的session。最后用session.getMapper为mapper里面的任意一个接口生成一个代理对象，以便直接调用接口的方法。
4. 用Mybatis-plus封装好的QueryWrapper<>和selectCount进行查询相同id的个数，count>0则无法注册



- #### 补充登录细节

​		*这里先写登录学生，不会写传输加密，遂不管了。*

1. 
