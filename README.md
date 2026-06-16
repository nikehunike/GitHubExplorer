# GitHubExplorer

一个基于 **Jetpack Compose** + **Material 3** 构建的 GitHub 仓库浏览器，支持搜索仓库、查看详情、阅读 README、收藏仓库、浏览用户主页等功能。

## 功能

| 功能 | 说明 |
|------|------|
| 🏠 热门仓库 | 首页展示 GitHub 上 Star > 1000 的热门仓库 |
| 🔍 搜索仓库 | 输入关键词实时搜索，500ms 防抖 |
| 📋 仓库详情 | 查看描述、Stars/Forks/Watchers、Topics、License |
| 📖 README 渲染 | Base64 解码 + Markdown 富文本渲染 |
| ⭐ 收藏管理 | Room 本地持久化，详情页一键收藏/取消 |
| 👤 用户主页 | 查看用户信息、头像、仓库列表 |
| 🌓 暗黑模式 | Material 3 Dynamic Color + 手动亮暗切换 |

## 技术栈

| 类别 | 技术 | 用途 |
|------|------|------|
| **UI** | Jetpack Compose + Material 3 | 声明式 UI 框架 |
| **导航** | Navigation Compose (type-safe) | 路由管理 |
| **状态管理** | StateFlow + sealed UiState | MVVM 状态管理 |
| **依赖注入** | Hilt | 全链路依赖管理 |
| **网络** | Retrofit + OkHttp + kotlinx.serialization | HTTP 请求 & JSON 解析 |
| **日志** | OkHttp Logging Interceptor | 网络请求/响应日志 |
| **图片** | Coil 3 | Compose 原生图片加载 |
| **本地存储** | Room + Flow | 收藏数据持久化 & 自动通知 |
| **Markdown** | multiplatform-markdown-renderer | README 富文本渲染 |

## 架构

```
┌─────────────────────────────────────────────┐
│  UI Layer           │  Compose Screens       │
│                      │  Home / Search / Detail │
│                      │  Bookmark / User        │
├─────────────────────────────────────────────┤
│  ViewModel Layer     │  StateFlow<UiState>     │
│                      │  业务逻辑 & 状态管理      │
├─────────────────────────────────────────────┤
│  Data Layer          │  GithubRepository       │
│                      │  ├── GitHubApi (Remote) │
│                      │  └── BookmarkDao (Local)│
└─────────────────────────────────────────────┘
```

**数据流**：`Screen → ViewModel → Repository → API / DAO → StateFlow → 重组 UI`

## API

使用 [GitHub REST API v3](https://docs.github.com/en/rest)，公开免费，无需注册：

| 接口 | 说明 |
|------|------|
| `GET /search/repositories` | 搜索仓库 |
| `GET /repos/{owner}/{repo}` | 仓库详情 |
| `GET /repos/{owner}/{repo}/readme` | README 内容 |
| `GET /users/{username}` | 用户信息 |
| `GET /users/{username}/repos` | 用户仓库列表 |

> 未认证情况下每小时 60 次请求。可在 OkHttp 拦截器中添加 Personal Access Token 提升至 5000 次/小时。

## 项目结构

```
app/src/main/java/com/example/githubexplorer/
├── GithubExplorerApp.kt          # @HiltAndroidApp 入口
├── MainActivity.kt               # 单 Activity + Edge to Edge
│
├── di/                           # Hilt 依赖注入
│   ├── AppModule.kt
│   ├── NetworkModule.kt          # OkHttp + Retrofit
│   └── DatabaseModule.kt         # Room
│
├── data/
│   ├── remote/
│   │   ├── GitHubApi.kt          # Retrofit 接口定义
│   │   └── dto/                  # API 响应模型
│   ├── local/
│   │   ├── AppDatabase.kt        # Room Database
│   │   ├── BookmarkDao.kt
│   │   └── entity/
│   └── repository/
│       └── GithubRepository.kt   # 数据层统一入口
│
├── ui/
│   ├── navigation/               # 路由 + NavHost
│   ├── theme/                    # Material 3 主题
│   ├── home/                     # 首页 (热门仓库)
│   ├── search/                   # 搜索页
│   ├── detail/                   # 仓库详情页
│   ├── user/                     # 用户主页
│   ├── bookmark/                 # 我的收藏
│   └── component/                # 可复用组件
│       └── RepoListItem.kt
│
└── util/
    └── Constants.kt
```

## 构建与运行

```bash
# 1. 用 Android Studio 打开项目根目录
# 2. 等待 Gradle Sync 完成
# 3. 连接设备或启动模拟器
# 4. 点击 Run
```

**最低要求**：
- Android Studio Hedgehog (2023.1.1) 或更高
- Android SDK 26+ (Android 8.0)
- JDK 17

## 实现步骤

详细的分步实现文档见 [STEPS.md](STEPS.md)。

| 步骤 | 内容 |
|------|------|
| Step 1 | 项目骨架：Gradle + Hilt + Navigation + 主题 |
| Step 2 | 网络层 + 搜索功能 |
| Step 3 | 仓库详情页 + README 渲染 |
| Step 4 | Room 本地收藏 |
| Step 5 | 用户主页 |
| Step 6 | 首页热门仓库 |

## License

MIT
