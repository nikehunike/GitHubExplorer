# GitHubExplorer 实现路线图

> 每一步对应一个可编译、可运行的中间版本，做完一步跑通再进下一步。

---

## 总体架构

```
UI (Compose Screens) → ViewModel (StateFlow) → Repository → Remote (Retrofit) / Local (Room)
```

---

## Step 1：项目骨架 ✅ 已完成

**做了什么**：Gradle 配置、Hilt 链路、Navigation 框架、Material 3 主题、空白首页。

**包含文件**：
- `settings.gradle.kts` / `build.gradle.kts` / `app/build.gradle.kts` / `gradle/libs.versions.toml`
- `AndroidManifest.xml` (+ mipmap/drawable/colors/themes)
- `GithubExplorerApp.kt` / `MainActivity.kt`
- `ui/theme/Theme.kt`
- `ui/navigation/Route.kt` / `AppNavHost.kt`
- `ui/home/HomeScreen.kt` / `HomeViewModel.kt`
- `di/AppModule.kt`（空壳）

**验证**：App 编译运行，屏幕居中显示「GitHubExplorer + 骨架搭建成功 ✅」

---

## Step 2：网络层 + 搜索功能 ← 当前步骤

### 2.1 新建文件

| 文件 | 做什么 |
|------|--------|
| `data/remote/GitHubApi.kt` | Retrofit 接口：`@GET("search/repositories")` |
| `data/remote/dto/RepoSearchResponse.kt` | 搜索 API 返回的 JSON 外层（`total_count` + `items`） |
| `data/remote/dto/RepoDto.kt` | 单个仓库的 JSON 字段（id, name, owner, stars, language 等） |
| `data/repository/GithubRepository.kt` | 数据层入口，封装 `searchRepos()`，返回 `Result<List<RepoDto>>` |
| `di/NetworkModule.kt` | Hilt `@Provides` OkHttpClient + Retrofit + GitHubApi 实例 |
| `ui/search/SearchScreen.kt` | 搜索页：TextField + LazyColumn 展示结果 |
| `ui/search/SearchViewModel.kt` | 持有 `searchQuery` + `uiState`，debounce 500ms 后调接口 |
| `ui/component/RepoListItem.kt` | 单行仓库卡片（头像 + 仓库名 + 描述 + ⭐数 + 语言色点） |

### 2.2 改动文件

| 文件 | 改什么 |
|------|--------|
| `ui/navigation/Route.kt` | 添加 `Route.Search` |
| `ui/navigation/AppNavHost.kt` | 注册 Search 路由 |
| `ui/home/HomeScreen.kt` | 添加一个「搜索仓库」按钮，点击跳 Search 页 |

### 2.3 关键知识点

- **Retrofit + kotlinx.serialization**：`Retrofit.Builder().addConverterFactory(Json.asConverterFactory())`
- **OkHttp 拦截器**：加 `User-Agent` Header（GitHub API 要求）
- **sealed interface UiState**：`Idle / Loading / Success / Error` 四种状态
- **debounce**：用 `snapshotFlow { query }.debounce(500).collectLatest { search(it) }` 避免每个字都发请求
- **collectAsStateWithLifecycle()**：生命周期感知，页面后台不收集

### 2.4 验证

- App 首页点「搜索仓库」→ 跳转搜索页
- 输入 `kotlin` → 500ms 后自动搜索 → 列表显示仓库（头像、名称、星数、描述）
- 空结果 / 网络错误有对应提示

---

## Step 3：仓库详情页

### 3.1 新建文件

| 文件 | 做什么 |
|------|--------|
| `data/remote/dto/RepoDetailDto.kt` | 仓库详情 JSON（description, topics, license, 时间等） |
| `data/remote/dto/ReadmeDto.kt` | README API 的 JSON（`content` 字段是 Base64） |
| `ui/detail/RepoDetailScreen.kt` | 详情页 UI：统计区 + Topics 标签 + README 渲染 |
| `ui/detail/RepoDetailViewModel.kt` | 并行请求仓库详情 & README，Base64 解码 |

### 3.2 改动文件

| 文件 | 改什么 |
|------|--------|
| `GitHubApi.kt` | 添加 `@GET("repos/{owner}/{repo}")` 和 `@GET("repos/{owner}/{repo}/readme")` |
| `GithubRepository.kt` | 添加 `getRepoDetail()` 和 `getReadme()` |
| `Route.kt` | 添加 `Route.RepoDetail(owner, repo)` |
| `AppNavHost.kt` | 注册详情页路由 |
| `RepoListItem.kt` | 添加点击事件回调 |

### 3.3 关键知识点

- **并行网络请求**：`coroutineScope { async { api.getRepo() }; async { api.getReadme() } }`
- **Base64 解码**：`android.util.Base64.decode(content, Base64.DEFAULT)` → UTF-8 String
- **Markdown 渲染**：先用 `WebView` 或 `AndroidView` 渲染，后续可换 `compose-markdown`
- **懒加载**：路由参数用 `backStackEntry.toRoute<Route.RepoDetail>()`

### 3.4 验证

- 搜索列表中点击一个仓库 → 跳转详情页，显示描述、星数、Fork、语言标签
- README 区域显示仓库的 Markdown 文档
- 返回键回到搜索结果

---

## Step 4：本地收藏（Room）

### 4.1 新建文件

| 文件 | 做什么 |
|------|--------|
| `data/local/entity/BookmarkEntity.kt` | Room 实体：id, name, fullName, stars, avatar, bookmarkedAt |
| `data/local/BookmarkDao.kt` | DAO：`insert` / `delete` / `getAll()` 返回 `Flow<List<BookmarkEntity>>` |
| `data/local/AppDatabase.kt` | Room Database 定义 |
| `di/DatabaseModule.kt` | Hilt 提供 Room 实例 + DAO |
| `ui/bookmark/BookmarkScreen.kt` | 收藏列表页 |
| `ui/bookmark/BookmarkViewModel.kt` | 从 Room Flow 读取收藏列表 |

### 4.2 改动文件

| 文件 | 改什么 |
|------|--------|
| `GithubRepository.kt` | 添加 `bookmark()` / `removeBookmark()` / `isBookmarked()` / `getBookmarks()` |
| `RepoDetailScreen.kt` | 添加 ⭐ 收藏/取消按钮 |
| `RepoDetailViewModel.kt` | 初始化时查询是否已收藏 |
| `Route.kt` | 添加 `Route.Bookmark` |
| `AppNavHost.kt` | 注册收藏页路由 |
| `HomeScreen.kt` | 添加入口按钮「我的收藏」 |

### 4.3 关键知识点

- **Room + Flow**：DAO 返回 `Flow<List<T>>`，数据变化自动推送 UI
- **收藏状态同步**：详情页进来时调用 `isBookmarked(id)` 查 Room
- **跨表不存冗余**：BookmarkEntity 把需要的展示字段全存下来（避免再去网络查）

### 4.4 验证

- 详情页点收藏 ⭐ → 图标变实心
- 去收藏列表页 → 看到刚才收藏的仓库
- 收藏列表点取消 → 回到详情页 ⭐ 变空心

---

## Step 5：用户主页

### 5.1 新建文件

| 文件 | 做什么 |
|------|--------|
| `data/remote/dto/UserDto.kt` | 用户信息 JSON（login, avatar_url, bio, followers 等） |
| `ui/user/UserProfileScreen.kt` | 用户主页 UI：头像 + 简介 + 统计 + 用户仓库列表 |
| `ui/user/UserProfileViewModel.kt` | 并行加载用户信息 + 用户仓库 |

### 5.2 改动文件

| 文件 | 改什么 |
|------|--------|
| `GitHubApi.kt` | 添加 `@GET("users/{username}")` 和 `@GET("users/{username}/repos")` |
| `GithubRepository.kt` | 添加 `getUser()` / `getUserRepos()` |
| `Route.kt` | 添加 `Route.UserProfile(username)` |
| `AppNavHost.kt` | 注册用户主页路由 |
| `RepoListItem.kt` / `RepoDetailScreen.kt` | 头像/用户名设置为可点击，跳转用户主页 |

### 5.3 验证

- 点击仓库卡片上的用户名 → 跳转用户主页，看到头像、简介、仓库列表
- 用户主页里的仓库点击 → 又能跳仓库详情

---

## Step 6：首页热门仓库

### 6.1 改动文件

| 文件 | 改什么 |
|------|--------|
| `GithubRepository.kt` | 添加 `getTrendingRepos()`（用搜索接口 `stars:>1000&sort=stars`） |
| `HomeViewModel.kt` | 初始化时加载热门仓库 |
| `HomeScreen.kt` | 替换空白占位为 LazyColumn 展示热门仓库 |
| `RepoListItem.kt` | 复用（无需改动，直接复用） |

### 6.2 验证

- 打开 App → 首页直接显示热门 Kotlin/Android 仓库列表
- 点击可跳详情，用户名可跳用户主页

---

## Step 7：抛光（按需选做）

| 功能 | 技术点 |
|------|--------|
| **下拉刷新** | Material 3 `PullToRefreshBox` |
| **分页加载** | LazyColumn 触底加载 `page++`，追加到列表 |
| **暗黑模式开关** | Theme 里加 Switch，`remember { mutableStateOf(false) }` 控制 |
| **骨架屏** | Loading 态用 `shimmer` 动画占位 |
| **错误重试** | Error 状态显示 Retry 按钮 |
| **共享元素动画** | `SharedTransitionLayout` 列表→详情过渡 |
| **Token 认证** | OkHttp Interceptor 加 `Authorization: Bearer xxx`，用 `local.properties` 注入 |
| **Markdown 渲染优化** | 换 `compose-markdown` 或 `compose-rich-text` 库 |

---

## 文件结构总览（完成后）

```
app/src/main/java/com/example/githubexplorer/
├── GithubExplorerApp.kt
├── MainActivity.kt
├── di/
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   └── DatabaseModule.kt
├── data/
│   ├── remote/
│   │   ├── GitHubApi.kt
│   │   └── dto/
│   │       ├── RepoSearchResponse.kt
│   │       ├── RepoDto.kt
│   │       ├── RepoDetailDto.kt
│   │       ├── ReadmeDto.kt
│   │       └── UserDto.kt
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── BookmarkDao.kt
│   │   └── entity/
│   │       └── BookmarkEntity.kt
│   └── repository/
│       └── GithubRepository.kt
├── ui/
│   ├── navigation/
│   │   ├── Route.kt
│   │   └── AppNavHost.kt
│   ├── theme/
│   │   └── Theme.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── search/
│   │   ├── SearchScreen.kt
│   │   └── SearchViewModel.kt
│   ├── detail/
│   │   ├── RepoDetailScreen.kt
│   │   └── RepoDetailViewModel.kt
│   ├── user/
│   │   ├── UserProfileScreen.kt
│   │   └── UserProfileViewModel.kt
│   ├── bookmark/
│   │   ├── BookmarkScreen.kt
│   │   └── BookmarkViewModel.kt
│   └── component/
│       └── RepoListItem.kt
└── util/
    └── Constants.kt
```
