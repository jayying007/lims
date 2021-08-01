# lims

### 目录结构

```
|-- dist                       # 打包后文件夹            
|-- public                     # 静态文件夹                                   
|   |-- favicon.ico              # 网站图标    
|   |-- index.html               # 入口页面
|-- src                        # 源码目录         
|   |--assets                    # 模块资源
|   |--components                # vue公共组件
|   |--views                     # vue页面    
|   |--App.vue                   # 页面入口文件
|   |--main.js                   # 入口文件，加载公共组件
|   |--router.js                 # 路由配置
|   |--store.js                  # 状态管理
|-- .env.pre-release           # 预发布环境    
|-- .env.production            # 生产环境       
|-- .env.test                  # 测试环境  
|-- vue.config.js              # 配置文件 
|-- .eslintrc.js               # ES-lint校验                   
|-- .gitignore                 # git忽略上传的文件格式   
|-- babel.config.js            # babel语法编译                        
|-- package.json               # 项目基本信息 
|-- package-lock.json          # 包版本控制 
|-- postcss.config.js          # CSS预处理器(此处默认启用autoprefixer)
```

## Project setup

```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
