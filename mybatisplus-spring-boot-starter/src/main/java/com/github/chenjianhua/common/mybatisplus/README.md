- 需要在启动类加上`@MapperScan`
- 默认数据库类型配置为 mysql （方便生成的sql（方言）符合各个数据库版本的特征）
- 可配置项：

```properties
# 设置最大单页限制数量，默认 500 条，-1 不受限制
mybatis-plus.page-config.limit=100
# 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
mybatis-plus.page-config.overflow=true
# 数据库类型,默认 mysql
mybatis-plus.dbType=mysql
```

- 需要自己配置的项

```properties
# 枚举包
mybatis-plus.type-enums-package=com/example/dependcytest/test/enums
# xml位置
mybatis-plus.mapper-locations=classpath:mapper/*.xml
# sql打印，生产需关闭
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

## 功能点

- 默认已集成mybatis-plus的分页配置、字段自动填充配置，无需自己配置
- create_at , update_at 填充当前时间
- creator , updator 填充当前登陆账号

- 返回页面的分页model必须经过PageVo包装

- 分页公共类PageParam
- 排序已经过 驼峰-下划线 转换