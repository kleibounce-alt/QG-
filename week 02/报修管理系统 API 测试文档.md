# 报修管理系统 API 文档

> 生成时间：2026-03-30  
> 测试工具：Apifox  
> 基础URL：`http://localhost:8080`

---

## 基础信息

| 项目 | 内容 |
|:---|:---|
| 数据格式 | JSON |
| 认证方式 | Bearer Token |
| 响应格式 | `{code, message, data}` |

---

## 认证模块

### 1.1 学生注册

- **URL**: `POST /api/auth/student/register`
- **描述**: 学生首次注册，学号作为账号

**请求体:**
```json
{
  "id": "3125004516",
  "password": "112302"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "3125004516",
    "role": "ROLE_STUDENT"
  }
}
```

------

### 1.2 管理员注册

- **URL**: `POST /api/auth/admin/register`
- **描述**: 管理员账号注册

**请求体:**

JSON

复制

```json
{
  "id": "0025000000",
  "password": "000000"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "0025000000",
    "role": "ROLE_ADMIN"
  }
}
```

------

### 1.3 学生登录

- **URL**: `POST /api/auth/student/login`

**请求体:**

JSON

复制

```json
{
  "id": "3125004516",
  "password": "112302"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMTI1MDA0NTE2Iiwicm9sZSI6IlJPTEVfU1RVREVOVCJ9..."
}
```

------

### 1.4 管理员登录

- **URL**: `POST /api/auth/admin/login`

**请求体:**

JSON

复制

```json
{
  "id": "0025000000",
  "password": "000000"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDI1MDAwMDAwIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NzQ4NDg2NzUsImV4cCI6MTc3NDkzNTA3NX0..."
}
```

------

## 管理员模块

> 请求头：`Authorization: Bearer {adminToken}`

### 2.1 获取所有报修单

- **URL**: `GET /api/admin/reports`

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 3,
      "studentId": 3125004516,
      "deviceType": "空调",
      "description": "灯不亮了",
      "status": "已完成",
      "time": "2026-03-29 16:57:27"
    }
  ]
}
```

------

### 2.2 更新报修单状态

- **URL**: `PUT /api/admin/reports/{id}`
- **描述**: 修改指定报修单的处理状态

**请求体:**

JSON

复制

```json
{
  "status": "处理中"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 6,
    "studentId": 3125004500,
    "deviceType": "水管",
    "description": "爆炸了突然",
    "status": "已完成",
    "time": "2026-03-29 19:14:03"
  }
}
```

------

### 2.3 删除报修单

- **URL**: `DELETE /api/admin/reports/{id}`

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

------

### 2.4 修改管理员密码

- **URL**: `PUT /api/admin/password`

**请求体:**

JSON

复制

```json
{
  "oldPassword": "000000",
  "newPassword": "123456"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

------

## 学生模块

> 请求头：`Authorization: Bearer {studentToken}`

### 3.1 绑定/修改宿舍

- **URL**: `PUT /api/student/dormitory`

**请求体:**

JSON

复制

```json
{
  "dormitory": "A1栋",
  "roomId": "301"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

------

### 3.2 获取我的报修单

- **URL**: `GET /api/student/reports`

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 10,
      "studentId": 3125004516,
      "deviceType": "空调",
      "description": "灯不亮了",
      "status": "未完成",
      "time": "2026-03-30 14:20:00"
    },
    {
      "id": 11,
      "studentId": 3125004516,
      "deviceType": "电脑",
      "description": "无法开机",
      "status": "处理中",
      "time": "2026-03-29 10:15:30"
    }
  ]
}
```

------

### 3.3 提交报修单

- **URL**: `POST /api/student/reports`

**请求体:**

JSON

复制

```json
{
  "deviceType": "水管",
  "description": "水龙头漏水"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "id": 12,
    "status": "未完成",
    "time": "2026-03-30 14:25:00"
  }
}
```

------

### 3.4 取消报修单

- **URL**: `DELETE /api/student/reports/{reportId}`

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

**失败响应 (400):**

JSON

复制

```json
{
  "code": 400,
  "message": "只能取消未完成的报修单"
}
```

------

### 3.5 修改学生密码

- **URL**: `PUT /api/student/password`

**请求体:**

JSON

复制

```json
{
  "oldPassword": "112302",
  "newPassword": "654321"
}
```

**成功响应 (200):**

JSON

复制

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

------

## 状态码说明

表格





| 状态码 | 含义       | 场景                                     |
| :----- | :--------- | :--------------------------------------- |
| 200    | 成功       | 业务正常完成                             |
| 400    | 请求错误   | 参数错误、学号已存在、无法取消已完成订单 |
| 401    | 未授权     | Token 无效或过期                         |
| 403    | 禁止访问   | 无权限操作                               |
| 404    | 资源不存在 | 报修单ID不存在                           |
| 500    | 服务器错误 | 后端异常                                 |

------

## 前端调用示例

TypeScript

复制

```typescript
// api/auth.ts
export const registerStudent = (data: { id: string; password: string }) => 
  request.post('/api/auth/student/register', data)

export const registerAdmin = (data: { id: string; password: string }) => 
  request.post('/api/auth/admin/register', data)

export const loginStudent = (data: { id: string; password: string }) => 
  request.post('/api/auth/student/login', data)

export const loginAdmin = (data: { id: string; password: string }) => 
  request.post('/api/auth/admin/login', data)


// api/admin.ts
export const getAllReports = () => 
  request.get('/api/admin/reports')

export const updateReportStatus = (id: number, status: string) =>
  request.put(`/api/admin/reports/${id}`, { status })

export const deleteReport = (id: number) =>
  request.delete(`/api/admin/reports/${id}`)

export const changeAdminPassword = (data: { oldPassword: string; newPassword: string }) =>
  request.put('/api/admin/password', data)


// api/student.ts
export const bindDormitory = (data: { dormitory: string; roomId: string }) =>
  request.put('/api/student/dormitory', data)

export const getStudentReports = () => 
  request.get('/api/student/reports')

export const createReport = (data: { deviceType: string; description: string }) =>
  request.post('/api/student/reports', data)

export const cancelReport = (id: number) =>
  request.delete(`/api/student/reports/${id}`)

export const changeStudentPassword = (data: { oldPassword: string; newPassword: string }) =>
  request.put('/api/student/password', data)
```

------

## Apifox 环境配置

yaml

复制

```yaml
环境名称: 本地开发
变量:
  baseUrl: http://localhost:8080
  
  # 管理员 Token（测试用）
  adminToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDI1MDAwMDAwIiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NzQ4NDg2NzUsImV4cCI6MTc3NDkzNTA3NX0.PiRpCOV39rzdBwP7UaSX0YsXyj4xNTpN7tJPLD3BZKtZnMrbFQ8XapFJmWhiduyXVw2K31oqnuYi8Oo7Rxz4pQ
  
  # 学生 Token（测试用）
  studentToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMTI1MDA0NTE2Iiwicm9sZSI6IlJPTEVfU1RVREVOVCJ9...
```